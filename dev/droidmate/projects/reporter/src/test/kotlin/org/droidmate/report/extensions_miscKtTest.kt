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

class extensions_miscKtTest {

  @Test
  fun zeroDigitsTest() {

    println(1299.zeroLeastSignificantDigits(2))
  }

  @Test
  fun frequenciesTest() {
    assertThat(
      listOf(
        "a", "a", "b", "a", "x", "x", "x", "e", "f", "e", "x")
        .frequencies,
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
  fun transposeTest() {
    assertThat(
      mapOf(
        Pair("abc", 4),
        Pair("def", 4),
        Pair("x", 1)
      ).transpose,
      equalTo(
        mapOf(
          Pair(4, setOf("abc", "def")),
          Pair(1, setOf("x"))
        )
      )
    )
  }

  @Test
  fun replaceVariableTest() {

    assertThat(
      StringBuilder(
        "Value of var_1 is \$var_1, value of xyz is \$xyz, and again, \$var_1 is the value of var_1.")
        .replaceVariable("var_1", "7")
        .replaceVariable("xyz", "magic").toString(),
      equalTo(
        "Value of var_1 is 7, value of xyz is magic, and again, 7 is the value of var_1."
      ))
  }
}

