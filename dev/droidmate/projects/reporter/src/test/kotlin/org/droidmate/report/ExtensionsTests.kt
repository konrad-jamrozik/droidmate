// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.test_suite_categories.UnderConstruction
import org.junit.Test
import org.junit.experimental.categories.Category
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ExtensionsTests {

  @Test
  @Category(UnderConstruction::class)
  fun uniqueCountByTimeTest() {

    val startTime: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0)

    val list: List<Pair<LocalDateTime, List<String>>> = listOf(
      Pair(startTime.plusSeconds(3), listOf("a", "b", "c")),
      Pair(startTime.plusSeconds(7), listOf("a", "c", "d")),
      Pair(startTime.plusSeconds(15), listOf("c")),
      Pair(startTime.plusSeconds(23), listOf("b", "e"))
    )

    val out: Map<Long, Int> = list.uniqueCountByTime(
      { Duration.between(startTime, it.first).toMillis() },
      { it.second }
    )

    assertEquals(mapOf(
      Pair(3L, 3), // a b c
      Pair(7L, 4), // a b c d
      Pair(15L, 4), // a b c d
      Pair(23L, 5)), // a b c d e
      out,
      ""
    )

  }
}