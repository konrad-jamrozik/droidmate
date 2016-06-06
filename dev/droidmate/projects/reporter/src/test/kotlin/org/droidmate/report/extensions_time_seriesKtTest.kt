// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class extensions_time_seriesKtTest {

  private val startTimeFixture: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0)
  private val inputDataFixture = listOf(
    Pair(startTimeFixture.plusSeconds(3), listOf("a ", "b", "c")),
    Pair(startTimeFixture.plusSeconds(7), listOf("a", "  c", "d")),
    Pair(startTimeFixture.plusSeconds(15), listOf("  c")),
    Pair(startTimeFixture.plusSeconds(23), listOf("b ", "e"))
  )

  private val itemsAtTimeFixture: Map<Long, List<String>> = mapOf(
    Pair(3000L, listOf("a ", "b", "c")),
    Pair(7000L, listOf("a", "  c", "d")),
    Pair(15000L, listOf("  c")),
    Pair(23000L, listOf("b ", "e"))
  )

  private val accumulatedUniqueStringsFixture: Map<Long, List<String>> = mapOf(
    Pair(3000L, listOf("a", "b", "c")),
    Pair(7000L, listOf("a", "b", "c", "d")),
    Pair(15000L, listOf("a", "b", "c", "d")),
    Pair(23000L, listOf("a", "b", "c", "d", "e"))
  )


  @Test
  fun itemsAtTimeTest() {

    // Act
    val itemsAtTime: Map<Long, Iterable<String>> = inputDataFixture.itemsAtTime(
      startTime = startTimeFixture,
      extractTime = { it.first },
      extractItems = { it.second }
    )

    assertEquals(expected = itemsAtTimeFixture, actual = itemsAtTime)
  }

  @Test
  fun accumulateUniqueStringsTest() {

    // Act
    val accumulatedUniqueStrings = itemsAtTimeFixture.accumulateUniqueStrings(
      extractUniqueString = { it.trim() }
    )

    assertEquals(expected = accumulatedUniqueStringsFixture, actual = accumulatedUniqueStrings)
  }


  private val unpartitionedTimeSeriesFixture = mapOf(
    Pair(7L, 1),
    Pair(9L, 2),
    Pair(13L, 3),
    Pair(17L, 4),
    Pair(31L, 5),
    Pair(45L, 6)
  )

  private val partitionedTimeSeriesFixture: Map<Long, List<Int>> = mapOf(
    Pair(0L, listOf()),
    Pair(10L, listOf(1, 2)),
    Pair(20L, listOf(3, 4)),
    Pair(30L, listOf()),
    Pair(40L, listOf(5)),
    Pair(50L, listOf(6))
  )

  private val partitionedAccumulatedAndExtendedTimeSeriesFixture = mapOf(
    Pair(0L, 0),
    Pair(10L, 2),
    Pair(20L, 4),
    Pair(30L, 4),
    Pair(40L, 5),
    Pair(50L, 6),
    Pair(60L, -1),
    Pair(70L, -1)
  )

  @Test
  fun partitionTest() {

    val map: Map<Long, Int> = unpartitionedTimeSeriesFixture

    // Act
    val partitionedTimeSeries: Map<Long, List<Int>> = map.partition(10)

    assertEquals(expected = partitionedTimeSeriesFixture, actual = partitionedTimeSeries)
  }

  @Test
  fun accumulateMaxesAndPadTest() {

    // Act
    val accumulatedAndPadded: Map<Long, Int> = partitionedTimeSeriesFixture
      .accumulateMaxes(extractMax = { it.max() ?: 0 })
      .padPartitions(partitionSize = 10L, lastPartition = 70L)
    
    assertEquals(expected = partitionedAccumulatedAndExtendedTimeSeriesFixture, actual = accumulatedAndPadded)
  }
}

