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
    assertThat(1299.zeroLeastSignificantDigits(2), equalTo(1200L))
  }

  @Test
  fun replaceVariableTest() {
    assertThat(
      StringBuilder(
        "Value of var_1 is \$var_1, value of xyz is \$xyz, and again, \$var_1 is the value of var_1.")
        .replaceVariable("var_1", "777")
        .replaceVariable("xyz", "magic")
        .toString(),
      equalTo(
        "Value of var_1 is 777, value of xyz is magic, and again, 777 is the value of var_1."
      ))
  }
}

