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

class MapExtensionsKtTest {

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

}

