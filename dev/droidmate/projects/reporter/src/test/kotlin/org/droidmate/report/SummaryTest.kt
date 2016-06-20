// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.common.logcat.ApiLogcatMessage
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.test_suite_categories.UnderConstruction
import org.junit.Test
import org.junit.experimental.categories.Category
import java.time.Duration
import java.time.temporal.ChronoUnit

class SummaryTest {

  // KJA current test
  @Category(UnderConstruction::class)
  @Test
  fun buildsString() {

    val msg = """
    |TId: 1 objCls: android.webkit.WebView mthd: methd retCls: void params:  stacktrace: dalvik
    """.trimMargin()
    val api1 = ApiLogcatMessage.from(msg)
    // Act
    val summaryString = Summary.buildString(
      "example.package.name",
      Duration.of(3, ChronoUnit.MINUTES),
      50,
      5,
      DeviceExceptionMissing(),
      17,
      listOf(
        Summary.ApiEntry(time = Duration.of(112, ChronoUnit.SECONDS), actionIndex = 1, threadId = 7, apiSignature = "api_1_signature"),
        Summary.ApiEntry(time = Duration.of(4, ChronoUnit.MINUTES), actionIndex = 2, threadId = 1, apiSignature = "api_2_signature")
      ),
      33, listOf("apiPair1", "apiPair2")
    )

    val manualInspection = true
    if (manualInspection)
      println(summaryString)
  }
}