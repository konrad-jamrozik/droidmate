// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.junit.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class SummaryTest {

  // KJA curr work
  @Test
  fun buildsString() {
    val summaryString = Summary.buildString("zorg", Duration.of(3, ChronoUnit.MINUTES),50,5,17,listOf("api1","api2"),33, listOf("apiPair1", "apiPair2"))

    val manualInspection = true
    if (manualInspection)
      println(summaryString)
  }
}