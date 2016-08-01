// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
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