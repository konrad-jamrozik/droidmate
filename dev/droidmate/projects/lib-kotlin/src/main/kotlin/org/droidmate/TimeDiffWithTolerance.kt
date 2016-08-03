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
package org.droidmate

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime

class TimeDiffWithTolerance(private val tolerance: Duration) {

  private val log: Logger = LoggerFactory.getLogger(TimeDiffWithTolerance::class.java)

  fun warnIfBeyond(start: LocalDateTime, end: LocalDateTime, startName: String, endName: String): Boolean {
    val endBeforeStart = Duration.between(end, start)
    if (endBeforeStart > tolerance) {

      val (startNamePadded, endNamePadded) = Pad(startName, endName)
      log.warn("The expected end time '$endName' is before the expected start time '$startName' by more than the tolerance.\n" +
        "$startNamePadded : $start\n" +
        "$endNamePadded : $end\n" +
        "Tolerance  : ${tolerance.toMillis()} ms\n" +
        "Difference : ${endBeforeStart.toMillis()} ms")
      return true
    } else
      return false
  }
}