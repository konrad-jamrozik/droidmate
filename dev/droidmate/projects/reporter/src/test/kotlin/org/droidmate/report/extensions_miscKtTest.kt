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

