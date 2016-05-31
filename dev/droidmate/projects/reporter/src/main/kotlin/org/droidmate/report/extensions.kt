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

fun <T, TItem> Iterable<T>.uniqueCountAtTime(
  extractTime: (T) -> Int,
  extractItems: (T) -> Iterable<TItem>,
  uniqueString: (TItem) -> String
)
  : Map<Int, Int> {

  val timedItems: Map<Int, Iterable<TItem>> = this.associate { Pair(extractTime(it), extractItems(it)) }

  val uniqueItemsAcc: MutableSet<String> = hashSetOf()

  val timedUniqueItems = timedItems.mapValues {

    uniqueItemsAcc.addAll(it.value.map { uniqueString(it) })
    uniqueItemsAcc.toSet()
  }

  return timedUniqueItems.mapValues { it.value.count() }
}

fun <T> Map<Int, T>.partition(partitionSize: Int): Collection<Pair<Int, List<T>>> {

  tailrec fun <T> _partition(
    acc: Collection<Pair<Int, List<T>>>,
    remainder: Collection<Pair<Int, T>>,
    partitionSize: Int,
    currentPartitionValue: Int): Collection<Pair<Int, List<T>>> {

    if (remainder.isEmpty()) return acc else {

      val currentPartition = remainder.partition { it.first <= currentPartitionValue }
      val current: List<Pair<Int, T>> = currentPartition.first
      val currentValues: List<T> = current.fold<Pair<Int, T>, MutableList<T>>(mutableListOf(), { out, pair -> out.add(pair.second); out })

      return _partition(acc.plus(Pair(currentPartitionValue, currentValues)), currentPartition.second, partitionSize, currentPartitionValue + partitionSize)
    }
  }

  return _partition(mutableListOf(Pair(0, emptyList<T>())), this.toList(), partitionSize, partitionSize)
}

/**
  Assumes a receiver is a sequence of partitioned collections of values. 
 */
// KJA split extending to last partition and extracting max. 
fun <T> Collection<Pair<Int, T>>.maxValueUntilPartition(
  lastPartition: Int,
  partitionSize: Int,
  extractMax: (T) -> Int
): Collection<Pair<Int, Int>> {

  require(lastPartition % partitionSize == 0, { "lastPartition: $lastPartition partitionSize: $partitionSize" })
  require(this.all { it.first % partitionSize == 0 })

  return if (this.isEmpty())
    (0..lastPartition step partitionSize).map { Pair(it, -1) }
  else {

    var currMaxVal: Int = 0
    var currLastPartition: Int = this.last().first

    this.toMap().mapValues {
        currMaxVal = max(extractMax(it.value), currMaxVal)
        currMaxVal
    }.plus(((currLastPartition + partitionSize)..lastPartition step partitionSize).map { Pair(it, -1) }).toList()
  }
}

// Reference: http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
fun Int.zeroDigits(digitsToZero: Int) : Int {
  return BigDecimal(toString()).setScale(-digitsToZero, RoundingMode.DOWN).toBigInteger().toInt()
}

fun IApkExplorationOutput2.uniqueWidgetCountByTime(): Map<Int, Int> {

  fun uniqueWidgetCountAtTime(): Map<Int, Int> {
    return this.actRess.uniqueCountAtTime(
      // KNOWN BUG got here time with relation to exploration start of -25, but it should be always > 0.
      // The currently applied workaround is to add 500 milliseconds.
      extractTime = { Duration.between(this.explorationStartTime, it.action.timestamp).toMillis().toInt() + 500 },
      extractItems = { it.result.guiSnapshot.guiState.widgets.filter { it.canBeActedUpon() } },
      uniqueString = { WidgetStrategy.WidgetInfo(it).uniqueString }
    )
  }

  return uniqueWidgetCountAtTime()
    .partition(1000)
    .maxValueUntilPartition(
      lastPartition = this.explorationTimeInMs.zeroDigits(3),
      partitionSize = 1000,
      extractMax = { it.max() ?: 0 })
    .toMap()
}

// KJA DRY-up with extension above
fun IApkExplorationOutput2.uniqueClickedWidgetCountByTime(): Map<Int, Int> {

  fun uniqueWidgetCountAtTime(): Map<Int, Int> {
    return this.actRess.uniqueCountAtTime(
      // KNOWN BUG got here time with relation to exploration start of -25, but it should be always > 0.
      // The currently applied workaround is to add 500 milliseconds.
      extractTime = { Duration.between(this.explorationStartTime, it.action.timestamp).toMillis().toInt() + 500 },
      extractItems = { 
        val action = it.action.base;
        when (action) {
          is WidgetExplorationAction -> setOf(action.widget)
          else -> emptySet()
        }
      },
        //extractItems = { it.result.guiSnapshot.guiState.widgets.filter { it.canBeActedUpon() } },
        uniqueString = { WidgetStrategy.WidgetInfo(it).uniqueString }
    )
  }

  return uniqueWidgetCountAtTime()
    .partition(1000)
    .maxValueUntilPartition(
      lastPartition = this.explorationTimeInMs.zeroDigits(3),
      partitionSize = 1000,
      extractMax = { it.max() ?: 0 })
    .toMap()

}


