// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.report

import com.konradjamrozik.associateMany
import com.konradjamrozik.flatten
import java.lang.Math.max
import java.time.Duration
import java.time.LocalDateTime

private fun computeDuration(startTime: LocalDateTime, time: LocalDateTime): Long {
  return Duration.between(startTime, time).toMillis()
}

fun <T, TItem> Iterable<T>.itemsAtTime(
  startTime: LocalDateTime,
  extractTime: (T) -> LocalDateTime,
  extractItems: (T) -> Iterable<TItem>
): Map<Long, Iterable<TItem>> {

  // Implicit assumption: each element of receiver has a different key (== duration).
  return this.associate {
    Pair(
      computeDuration(startTime, extractTime(it)),
      extractItems(it)
    )
  }
}

fun <T, TItem> Iterable<T>.itemsAtTimes(
  startTime: LocalDateTime,
  extractTime: (TItem) -> LocalDateTime,
  extractItems: (T) -> Iterable<TItem>
): Map<Long, Iterable<TItem>> {

  val itemsAtTimesGroupedByOriginElement: Iterable<Map<Long, Iterable<TItem>>> = this.map {

    // Items from the currently processed element.
    val items: Iterable<TItem> = extractItems(it)

    // Items from the currently processed element, keyed by the computed duration. Multiple items might have
    // the same time.
    val itemsAtTime: Map<Long, Iterable<TItem>> =
      items
        .associateMany {
          Pair(
            computeDuration(startTime, extractTime(it)),
            it)
        }
    
    itemsAtTime
  }
  
  // We do not care from which element given (time->item) mapping came, so we flatten it.
  return itemsAtTimesGroupedByOriginElement.flatten()
}

fun Map<Long, Iterable<String>>.accumulate(): Map<Long, Iterable<String>> {

  val uniqueStringsAcc: MutableSet<String> = hashSetOf()

  return this.mapValues {
    uniqueStringsAcc.addAll(it.value)
    uniqueStringsAcc.toList()
  }
}

fun <T> Map<Long, T>.partition(partitionSize: Long): Map<Long, List<T>> {

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

  return _partition(mutableListOf(Pair(0L, emptyList<T>())), this.toList(), partitionSize, partitionSize).toMap()
}

fun <T> Map<Long, T>.accumulateMaxes(
  extractMax: (T) -> Int
): Map<Long, Int>
{
  var currMaxVal: Int = 0

  return this.mapValues {
    currMaxVal = max(extractMax(it.value), currMaxVal)
    currMaxVal
  }
}

fun Map<Long, Int>.padPartitions(
  partitionSize: Long,
  lastPartition: Long
): Map<Long, Int> {

  require(lastPartition.rem(partitionSize) == 0L, { "lastPartition: $lastPartition partitionSize: $partitionSize" })
  require(this.all { it.key.rem(partitionSize) == 0L })

  return if (this.isEmpty())
    (0..lastPartition step partitionSize).associate { Pair(it, -1) }
  else {
    val maxKey = this.keys.max() ?: 0
    val paddedPartitions: Map<Long, Int> = ((maxKey + partitionSize)..lastPartition step partitionSize).associate { Pair(it, -1) }
    return this.plus(paddedPartitions)
  }
}

fun Map<Long, Iterable<String>>.countsPartitionedByTime(
  partitionSize: Long,
  lastPartition: Int
): Map<Long, Int> {
  return this
    .mapKeys {
      // KNOWN BUG got here time with relation to exploration start of -25, but it should be always > 0.
      // The currently applied workaround is to add 100 milliseconds.
      it.key + 100L
    }
    .accumulate()
    .mapValues { it.value.count() }
    .partition(partitionSize)
    .accumulateMaxes(extractMax = { it.max() ?: 0 })
    .padPartitions(partitionSize, lastPartition.zeroLeastSignificantDigits(3))
}