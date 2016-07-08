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

class IterableExtensionsKtTest {
  @Test
  fun frequenciesTest() {
    assertThat(
      listOf(
        "a", "a", "b", "a", "x", "x", "x", "X", "e", "f", "e", "x")
        .frequencies,
      equalTo(
        mapOf(
          Pair("a", 3),
          Pair("b", 1),
          Pair("x", 4),
          Pair("X", 1),
          Pair("e", 2),
          Pair("f", 1))
      )
    )
  }


}