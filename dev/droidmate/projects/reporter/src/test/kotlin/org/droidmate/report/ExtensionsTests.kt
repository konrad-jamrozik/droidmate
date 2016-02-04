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
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ExtensionsTests {

  @Test
  fun uniqueCountAtTimeTest() {

    val startTime: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0)

    val list: List<Pair<LocalDateTime, List<String>>> = listOf(
      Pair(startTime.plusSeconds(3), listOf("a ", "b", "c")),
      Pair(startTime.plusSeconds(7), listOf("a", "  c", "d")),
      Pair(startTime.plusSeconds(15), listOf("  c")),
      Pair(startTime.plusSeconds(23), listOf("b ", "e"))
    )

    // Act
    val out: Map<Int, Int> = list.uniqueCountAtTime(
      extractTime = { Duration.between(startTime, it.first).toMillis().toInt() },
      extractItems = { it.second },
      uniqueString = { it.trim() }
    )

    assertEquals(mapOf(
      Pair(3000, 3), // a b c
      Pair(7000, 4), // a b c d
      Pair(15000, 4), // a b c d
      Pair(23000, 5)), // a b c d e
      out,
      ""
    )
  }

  @Test
  fun multiPartitionTest() {

    val map: Map<Int, Int> = mapOf(
      Pair(7, 1),
      Pair(9, 2),
      Pair(13, 3),
      Pair(17, 4),
      Pair(31, 5),
      Pair(45, 6)
    )

    // Act
    val out: Collection<Pair<Int, List<Int>>> = map.multiPartition(10)

    val multiPartitionFixture = multiPartitionFixture
    assertEquals(multiPartitionFixture, out, "")
  }

  private val multiPartitionFixture: List<Pair<Int, List<Int>>> = listOf(
    Pair(0, listOf()),
    Pair(10, listOf(1, 2)),
    Pair(20, listOf(3, 4)),
    Pair(30, listOf()),
    Pair(40, listOf(5)),
    Pair(50, listOf(6))
  )

  @Test
  fun maxCountAtPartitionTest() {

    // Act
    val out: Collection<Pair<Int, Int>> = multiPartitionFixture.maxValueAtPartition(
      maxPartition = 70,
      partitionSize = 10,
      extractMax = { it.max() ?: 0 }
    )

    assertEquals(listOf(
      Pair(0, 0),
      Pair(10, 2),
      Pair(20, 4),
      Pair(30, 4),
      Pair(40, 5),
      Pair(50, 6),
      Pair(60, -1),
      Pair(70, -1)
    ),
      out,
      ""
    )
  }
}

