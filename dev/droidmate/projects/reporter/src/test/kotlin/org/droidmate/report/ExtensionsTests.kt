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
  fun uniqueCountByTimeTest() {

    val startTime: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0)

    val list: List<Pair<LocalDateTime, List<String>>> = listOf(
      Pair(startTime.plusSeconds(3), listOf("a ", "b", "c")),
      Pair(startTime.plusSeconds(7), listOf("a", "  c", "d")),
      Pair(startTime.plusSeconds(15), listOf("  c")),
      Pair(startTime.plusSeconds(23), listOf("b ", "e"))
    )

    // Act
    val out: Map<Long, Int> = list.uniqueCountByTime(
      { Duration.between(startTime, it.first).toMillis() },
      { it.second },
      { it.trim() }
    )

    assertEquals(mapOf(
      Pair(3000L, 3), // a b c
      Pair(7000L, 4), // a b c d
      Pair(15000L, 4), // a b c d
      Pair(23000L, 5)), // a b c d e
      out,
      ""
    )
  }

  @Test
  fun multiPartitionTest() {

    val list: List<Pair<Long, Int>> = listOf(
      Pair(7L, 1),
      Pair(9L, 2),
      Pair(13L, 3),
      Pair(17L, 4),
      Pair(31L, 5),
      Pair(45L, 6)
    )

    // Act
    val out: Collection<Pair<Long, List<Int>>> = list.multiPartition(10)

    assertEquals(listOf(
      Pair(10L, listOf(1,2)),
      Pair(20L, listOf(3,4)),
      Pair(30L, listOf()),
      Pair(40L, listOf(5)),
      Pair(50L, listOf(6))
        ),
      out,
      ""
    )
  }
}

