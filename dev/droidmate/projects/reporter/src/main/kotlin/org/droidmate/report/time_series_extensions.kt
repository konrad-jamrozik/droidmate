// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import java.lang.Math.max
import java.time.Duration
import java.time.LocalDateTime

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

