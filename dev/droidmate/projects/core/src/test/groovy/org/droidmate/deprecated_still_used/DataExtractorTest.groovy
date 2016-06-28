// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.droidmate.common.logcat.TimeFormattedLogcatMessage
import org.droidmate.configuration.Configuration
import org.droidmate.configuration.ConfigurationBuilder
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.time.LocalDateTime

import static ExplorationOutputBuilder.build

@Deprecated
@TypeChecked(TypeCheckingMode.SKIP)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class DataExtractorTest extends DroidmateGroovyTestCase
{
  @Test
  void "Extracts input file for pgfplots 'APIs seen saturation' chart"()
  {

    LocalDateTime monitorInitTime1 = LocalDateTime.parse("${TimeFormattedLogcatMessage.assumedDate.year}-02-03T04:12:23.100")
    LocalDateTime monitorInitTime2 = LocalDateTime.parse("${TimeFormattedLogcatMessage.assumedDate.year}-05-09T06:03:44.353")

    ExplorationOutput explorationOutput = build() {

      apk(name: "com.example.app", monitorInitTime: monitorInitTime1) {

        explorationAction(mssSinceMonitorInit: 0, action: "reset")
        apiLogs(mssSinceMonitorInit: [200, 200, 250, 270], methodNames: [
          "api1", // 0.2   new
          "api1", // 0.2   ignored
          "api2", // 0.25  new
          "api1", // 0.27  ignored
        ])

        explorationAction(mssSinceMonitorInit: 350, action: "click")
        apiLogs(mssSinceMonitorInit: [380, 385, 387, 400, 470], methodNames: [
          "api2", // 0.38  ignored
          "api1", // 0.385 ignored
          "api3", // 0.387 new
          "api4", // 0.4   new
          "api5", // 0.47  new
        ])

        explorationAction(mssSinceMonitorInit: 475, action: "terminate")
        apiLogs(mssSinceMonitorInit: [480, 510, 520, 540], methodNames: [
          "api5", // 0.48  ignored
          "api7", // 0.51  new
          "api6", // 0.52  new
          "api2", // 0.54  ignored
        ])

        explorationEnd(mssSinceMonitorInit: 580)
      }

      apk(name: "org.another.interesting.program", monitorInitTime: monitorInitTime2) {

        explorationAction(mssSinceMonitorInit: 0, action: "reset")
        apiLogs(mssSinceMonitorInit: [10, 50, 100], methodNames: [
          "api1", // 0.01   new
          "api2", // 0.05   new
          "api3", // 0.1    new
        ])

        explorationAction(mssSinceMonitorInit: 105, action: "click")
        apiLogs(mssSinceMonitorInit: [110], methodNames: [
          "api4", // 0.11   new
        ])

        explorationAction(mssSinceMonitorInit: 150, action: "reset")
        apiLogs(mssSinceMonitorInit: [], methodNames: [])

        explorationAction(mssSinceMonitorInit: 300, action: "reset")
        apiLogs(mssSinceMonitorInit: [], methodNames: [])

        explorationAction(mssSinceMonitorInit: 350, action: "click")
        apiLogs(mssSinceMonitorInit: [370, 380], methodNames: [
          "api5", // 0.37   new
          "api6", // 0.38   new
        ])

        caughtException(mssSinceMonitorInit: 550)
      }
    }
    def actualTransformedOutput = new StringWriter()

    // Act
    getSut().pgfplotsChartInputData(explorationOutput, actualTransformedOutput, timeTickSize: 100, timeTicks: 9)

    String expectedTransformedOutput =
      " seconds_passed droidmate-run:com.example.app droidmate-run:org.another.interesting.program\n" +
      "            0.0   0   0\n" +
      "            0.1   0   3\n" +
      "            0.2   1   4\n" +
      "            0.3   2   4\n" +
      "            0.4   4   6\n" +
      "            0.5   5   6\n" +
      "            0.6   7 nan\n" +
      "            0.7 nan nan\n" +
      "            0.8 nan nan\n" +
      "            0.9 nan nan\n"

    assert actualTransformedOutput.toString() == expectedTransformedOutput
  }

  private static IExplorationOutputDataExtractor getSut()
  {
    String[] args = []
    Configuration cfg = new ConfigurationBuilder().build(args)
    return new ExplorationOutputDataExtractor(cfg.compareRuns, cfg)
  }

}
