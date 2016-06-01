// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class misc_extensionsKtTest {


  @Test
  fun zeroDigitsTest() {

    println(1299.zeroLeastSignificantDigits(2))
  }

  @Test
  fun frequenciesTest() {
    assertThat(
      listOf(
        "a",
        "a",
        "b",
        "a",
        "x",
        "x",
        "x",
        "e",
        "f",
        "e",
        "x").frequencies,
      equalTo(
        mapOf(
          Pair("a", 3),
          Pair("b", 1),
          Pair("x", 4),
          Pair("e", 2),
          Pair("f", 1))
      )
    )
  }

  @Test
  fun inverseTest() {
    assertThat(
      mapOf(
        Pair("abc", 4),
        Pair("def", 4),
        Pair("x", 1)
      ).inverse,
      equalTo(
        mapOf(
          Pair(4, setOf("abc", "def")),
          Pair(1, setOf("x"))
        )
      )
    )
  }
}

