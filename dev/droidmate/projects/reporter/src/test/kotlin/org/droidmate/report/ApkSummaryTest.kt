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

import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.test_base.FilesystemTestFixtures
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.Test
import java.nio.file.Path
import java.time.Duration
import java.time.temporal.ChronoUnit

class ApkSummaryTest {

  val printToStdout = false
  
  @Test
  fun buildsFromPayload() {

    val apiEntry1 = ApkSummary.ApiEntry(time = Duration.of(112, ChronoUnit.SECONDS), actionIndex = 1, threadId = 7, apiSignature = "api_1_signature")
    val apiEntry2 = ApkSummary.ApiEntry(time = Duration.of(4, ChronoUnit.MINUTES), actionIndex = 2, threadId = 1, apiSignature = "api_2_signature")

    // Act
    val summaryString = ApkSummary.build(ApkSummary.Payload(
      appPackageName = "example.package.name",
      totalRunTime = Duration.of(3, ChronoUnit.MINUTES),
      totalActionsCount = 50,
      totalResetsCount = 5,
      exception = DeviceExceptionMissing(),
      uniqueApisCount = 17,
      apiEntries = listOf(apiEntry1, apiEntry2),
      uniqueEventApiPairsCount = 33,
      apiEventEntries = listOf(
        ApkSummary.ApiEventEntry(apiEntry1, "<event1>"),
        ApkSummary.ApiEventEntry(apiEntry2, "<event2>")
      ))
    )

    // Non-exhaustive asserts
    assertThat(summaryString, containsString("example.package.name"))
    assertThat(summaryString, containsString("33"))
    assertThat(summaryString, containsString("api_1_signature"))
    assertThat(summaryString, containsString("<event2>"))

    if (printToStdout)
      println(summaryString)
  }

  @Test
  fun buildsFromApkExplorationOutput2() {
  
    val serExplOutput2: Path = FilesystemTestFixtures.build().f_monitoredSer2
    val explOut2 = OutputDir(serExplOutput2.parent).notEmptyExplorationOutput2

    // Act
    val summaryString = ApkSummary.build(explOut2.first())

    if (printToStdout)
      println(summaryString)
  }
}