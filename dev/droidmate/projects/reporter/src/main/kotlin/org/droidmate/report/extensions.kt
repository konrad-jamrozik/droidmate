// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.Table
import com.konradjamrozik.FileSystemsOperations
import com.konradjamrozik.createDirIfNotExists
import com.konradjamrozik.isDirectory
import org.codehaus.groovy.runtime.NioGroovyMethods
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.actions.WidgetExplorationAction
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.strategy.WidgetStrategy
import java.lang.Math.max
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime

fun Path.text(): String {
  return NioGroovyMethods.getText(this)
}

fun Path.deleteDir(): Boolean {
  return NioGroovyMethods.deleteDir(this)
}

val Path.fileNames: Iterable<String>
  get() {
    require(this.isDirectory)
    return Files.newDirectoryStream(this).map { it.fileName.toString() }
  }

fun Path.withFiles(vararg files: Path): Path {
  files.asList().copyFilesToDirInDifferentFileSystem(this)
  return this
}

fun FileSystem.dir(dirName: String): Path {
  val dir = this.getPath(dirName)
  dir.createDirIfNotExists()
  return dir
}

fun List<Path>.copyFilesToDirInDifferentFileSystem(destDir: Path): Unit {
  FileSystemsOperations().copyFilesToDirInDifferentFileSystem(this, destDir)
}

fun <R, C, V> Table<R, C, V>.writeOut(file: Path) {

  val headerRowString = this.columnKeySet().joinToString(separator = "\t")

  val dataRowsStrings: List<String> = this.rowMap().map {
    val rowValues = it.value.values
    rowValues.joinToString(separator = "\t")
  }

  val tableString = headerRowString + "\n" + dataRowsStrings.joinToString(separator = "\n")

  Files.write(file, tableString.toByteArray())
}

fun <T, TItem> Iterable<T>.itemsAtTime(
  startTime: LocalDateTime,
  extractTime: (T) -> LocalDateTime,
  extractItems: (T) -> Iterable<TItem>
): Map<Long, Iterable<TItem>> {

  fun computeDuration(time: LocalDateTime): Long {
    return Duration.between(startTime, time).toMillis()
  }

  return this.associate { Pair(computeDuration(extractTime(it)), extractItems(it)) }
}

fun <TItem>  Map<Long, Iterable<TItem>>.accumulateUniqueStrings(
  extractUniqueString: (TItem) -> String
): Map<Long, Iterable<String>> {

  val uniqueStringsAcc: MutableSet<String> = hashSetOf()
  
  return this.mapValues {
    uniqueStringsAcc.addAll(it.value.map { extractUniqueString(it) })
    uniqueStringsAcc.toList()
  }
}

fun <T> Map<Long, T>.partition(partitionSize: Long): Collection<Pair<Long, List<T>>> {

  tailrec fun <T> _partition(
    acc: Collection<Pair<Long, List<T>>>,
    remainder: Collection<Pair<Long, T>>,
    partitionSize: Long,
    currentPartitionValue: Long): Collection<Pair<Long, List<T>>> {

    if (remainder.isEmpty()) return acc else {

      val currentPartition = remainder.partition { it.first <= currentPartitionValue }
      val current: List<Pair<Long, T>> = currentPartition.first
      val currentValues: List<T> = current.fold<Pair<Long, T>, MutableList<T>>(mutableListOf(), { out, pair -> out.add(pair.second); out })

      return _partition(acc.plus(Pair(currentPartitionValue, currentValues)), currentPartition.second, partitionSize, currentPartitionValue + partitionSize)
    }
  }

  return _partition(mutableListOf(Pair(0L, emptyList<T>())), this.toList(), partitionSize, partitionSize)
}

/**
  Assumes a receiver is a sequence of partitioned collections of values. 
 */
// KJA split extending to last partition and extracting max. 
fun <T> Collection<Pair<Long, T>>.maxValueUntilPartition(
  lastPartition: Long,
  partitionSize: Long,
  extractMax: (T) -> Int
): Collection<Pair<Long, Int>> {

  require(lastPartition % partitionSize == 0L, { "lastPartition: $lastPartition partitionSize: $partitionSize" })
  require(this.all { it.first % partitionSize == 0L })

  return if (this.isEmpty())
    (0..lastPartition step partitionSize).map { Pair(it, -1) }
  else {

    var currMaxVal: Int = 0
    val currLastPartition: Long = this.last().first

    this.toMap().mapValues {
        currMaxVal = max(extractMax(it.value), currMaxVal)
        currMaxVal
    }.plus(((currLastPartition + partitionSize)..lastPartition step partitionSize).map { Pair(it, -1) }).toList()
  }
}

// Reference: http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
fun Int.zeroDigits(digitsToZero: Int): Long {
  return BigDecimal(toString()).setScale(-digitsToZero, RoundingMode.DOWN).toBigInteger().toLong()
}

fun IApkExplorationOutput2.uniqueViewCountByPartitionedTime(
  extractItems: (RunnableExplorationActionWithResult) -> Iterable<Widget>
): Map<Long, Int> {

  return this
    .actRess
    .itemsAtTime(
      startTime = this.explorationStartTime,
      extractTime = { it.action.timestamp },
      extractItems = extractItems
    )
    .mapKeys {
      // KNOWN BUG got here time with relation to exploration start of -25, but it should be always > 0.
      // The currently applied workaround is to add 500 milliseconds.
      it.key + 500L
    }
    .accumulateUniqueStrings(
      extractUniqueString = { WidgetStrategy.WidgetInfo(it).uniqueString }
    )
    .mapValues { it.value.count() }
    .partition(1000L)
    .maxValueUntilPartition(
      lastPartition = this.explorationTimeInMs.zeroDigits(3),
      partitionSize = 1000L,
      extractMax = { it.max() ?: 0 })
    .toMap()
}

fun IApkExplorationOutput2.uniqueWidgetCountByTime(): Map<Long, Int> {

  return this.uniqueViewCountByPartitionedTime(
    extractItems = { it.result.guiSnapshot.guiState.widgets.filter { it.canBeActedUpon() } }
  )
}

fun IApkExplorationOutput2.uniqueClickedWidgetCountByTime(): Map<Long, Int> {

  return this.uniqueViewCountByPartitionedTime(
    extractItems = {
      val action = it.action.base;
      when (action) {
        is WidgetExplorationAction -> setOf(action.widget)
        else -> emptySet()
      }
    }
  )
}

