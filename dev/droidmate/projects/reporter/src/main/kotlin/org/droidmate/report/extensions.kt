// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.github.konrad_jamrozik.FileSystemsOperations
import com.google.common.collect.Table
import org.codehaus.groovy.runtime.NioGroovyMethods
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Math.max
import java.nio.file.Files
import java.nio.file.Path

fun Path.text(): String {
  return NioGroovyMethods.getText(this)
}

fun Path.copyDirContentsRecursivelyToDirInDifferentFileSystem(destDir: Path): Unit {
  FileSystemsOperations().copyDirContentsRecursivelyToDirInDifferentFileSystem(this, destDir)
}

fun <R, C, V> Table<R, C, V>.writeOut(file: Path) {

  val DEBUGlog: Logger = LoggerFactory.getLogger("Table")
  DEBUGlog.info("DEBUG Writing out to $file")

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

  val timedItems: Map<Int, Iterable<TItem>> = this.toMap { Pair(extractTime(it), extractItems(it)) }

  val uniqueItemsAcc: MutableSet<String> = hashSetOf()

  val timedUniqueItems = timedItems.mapValues {

    uniqueItemsAcc.addAll(it.value.map { uniqueString(it) })
    uniqueItemsAcc.toSet()
  }

  return timedUniqueItems.mapValues { it.value.count() }
}

fun <T> Map<Int, T>.multiPartition(partitionSize: Int): Collection<Pair<Int, List<T>>> {

  tailrec fun <T> _multiPartition(
    acc: Collection<Pair<Int, List<T>>>,
    remainder: Collection<Pair<Int, T>>,
    partitionSize: Int,
    currentPartitionValue: Int): Collection<Pair<Int, List<T>>> {

    if (remainder.isEmpty()) return acc else {

      val currentPartition = remainder.partition { it.first <= currentPartitionValue }
      val current: List<Pair<Int, T>> = currentPartition.first
      val currentValues: List<T> = current.fold<Pair<Int, T>, MutableList<T>>(linkedListOf(), { out, pair -> out.add(pair.second); out })

      return _multiPartition(acc.plus(Pair(currentPartitionValue, currentValues)), currentPartition.second, partitionSize, currentPartitionValue + partitionSize)
    }
  }

  return _multiPartition(linkedListOf(Pair(0, emptyList<T>())), this.toList(), partitionSize, partitionSize)
}

fun <T> Collection<Pair<Int, T>>.maxValueAtPartition(
  maxPartition: Int,
  partitionSize: Int,
  extractMax: (T) -> Int
): Collection<Pair<Int, Int>> {

  require(maxPartition % partitionSize == 0)
  require(this.all { it.first % partitionSize == 0 })

  return if (this.isEmpty())
    (0..maxPartition step partitionSize).map { Pair(it, -1) }
  else {

    var currMaxVal: Int = 0
    var currMaxPartition: Int = this.last().first

    this.toMap().mapValues {
        currMaxVal = max(extractMax(it.value), currMaxVal)
        currMaxVal
    }.plus(((currMaxPartition + partitionSize)..maxPartition step partitionSize).map { Pair(it, -1) }).toList()
  }
}


