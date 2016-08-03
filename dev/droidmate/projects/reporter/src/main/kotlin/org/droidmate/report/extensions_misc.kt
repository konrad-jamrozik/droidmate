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

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration

/**
 * Zeroes digits before (i.e. left of) comma. E.g. if [digitsToZero] is 2, then 6789 will become 6700.
 */
// Reference: http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
fun Int.zeroLeastSignificantDigits(digitsToZero: Int): Long {
  return BigDecimal(this.toString()).setScale(-digitsToZero, RoundingMode.DOWN).toBigInteger().toLong()
}


/**
 * Given a string builder over a string containing variables in form of "$var_name" (without ""), it will replace
 * all such variables with their value. For examples, see [org.droidmate.report.extensions_miscKtTest.replaceVariableTest].
 */
fun StringBuilder.replaceVariable(varName: String, value: String) : StringBuilder
{
  val fullVarName = '$'+varName
  while (this.indexOf(fullVarName) != -1) {
    val startIndex = this.indexOf(fullVarName)
    val endIndex = startIndex + fullVarName.length
    this.replace(startIndex, endIndex, value)
  }
  return this
}

val Duration.minutesAndSeconds: String get() {
  val m = this.toMinutes()
  val s = this.seconds - m * 60
  return "$m".padStart(4, ' ') + "m " + "$s".padStart(2, ' ') + "s"
}