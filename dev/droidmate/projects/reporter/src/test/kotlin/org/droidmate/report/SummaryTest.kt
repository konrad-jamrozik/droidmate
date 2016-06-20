// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.exceptions.DeviceExceptionMissing
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class SummaryTest {

  val manualInspection = true
  
  @Test
  fun buildsString() {

    val apiEntry1 = Summary.ApiEntry(time = Duration.of(112, ChronoUnit.SECONDS), actionIndex = 1, threadId = 7, apiSignature = "api_1_signature")
    val apiEntry2 = Summary.ApiEntry(time = Duration.of(4, ChronoUnit.MINUTES), actionIndex = 2, threadId = 1, apiSignature = "api_2_signature")

    // Act
    val summaryString = Summary.buildString(
      appPackageName = "example.package.name",
      totalRunTime = Duration.of(3, ChronoUnit.MINUTES),
      totalActionsCount = 50,
      totalResetsCount = 5,
      exception = DeviceExceptionMissing(),
      uniqueApisCount = 17,
      apiEntries = listOf(apiEntry1, apiEntry2),
      uniqueApiEventPairsCount = 33,
      apiEventEntries = listOf(
        Summary.ApiEventEntry(apiEntry1, "<event1>"),
        Summary.ApiEventEntry(apiEntry2, "<event2>")
      )
    )

    // Non-exhaustive asserts
    assertThat(summaryString, containsString("example.package.name"))
    assertThat(summaryString, containsString("33"))
    assertThat(summaryString, containsString("api_1_signature"))
    assertThat(summaryString, containsString("<event2>"))

    if (manualInspection)
      println(summaryString)
  }
}