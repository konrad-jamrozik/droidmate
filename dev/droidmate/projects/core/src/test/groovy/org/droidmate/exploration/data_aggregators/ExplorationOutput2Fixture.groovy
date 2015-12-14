// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.data_aggregators

import org.droidmate.common.logcat.TimeFormattedLogcatMessage

import java.time.LocalDateTime

import static ExplorationOutput2Builder.build

class ExplorationOutput2Fixture implements IExplorationOutput2Fixture
{

  final ExplorationOutput2 fixture

  final Integer timeTicks = 9
  final Integer timeTickSizeInMs = 100

  ExplorationOutput2Fixture()
  {
    this.fixture = buildApkApisChartFixture()
  }


  @Override
  List<String> getExpectedChartFileContents()
  {
    String expectedChart1 =
        "seconds_passed droidmate-run:com.example.app\n" +
        "           0.0   0\n" +
        "           0.1   0\n" +
        "           0.2   1\n" +
        "           0.3   2\n" +
        "           0.4   4\n" +
        "           0.5   5\n" +
        "           0.6   7\n" +
        "           0.7 nan\n" +
        "           0.8 nan\n" +
        "           0.9 nan\n"

    String expectedChart2 =
        "seconds_passed droidmate-run:org.another.interesting.program\n" +
        "           0.0   0\n" +
        "           0.1   3\n" +
        "           0.2   4\n" +
        "           0.3   4\n" +
        "           0.4   6\n" +
        "           0.5   6\n" +
        "           0.6   6\n" +
        "           0.7 nan\n" +
        "           0.8 nan\n" +
        "           0.9 nan\n"

    return [expectedChart1, expectedChart2]
  }

  private ExplorationOutput2 buildApkApisChartFixture()
  {
    LocalDateTime init1 = LocalDateTime.parse("${TimeFormattedLogcatMessage.assumedDate.year}-02-03T04:12:23.100")
    LocalDateTime init2 = LocalDateTime.parse("${TimeFormattedLogcatMessage.assumedDate.year}-05-09T06:03:44.353")

    ExplorationOutput2 explOut2 = build() {

      apk(name: "com.example.app", monitorInitTime: init1, explorationEndTimeMss: 580) {

        actRes(action: "reset", mss: 0,
          logs: [
            ["api1", 200], // new
            ["api1", 200], // ignored
            ["api2", 250], // new
            ["api1", 270], // ignored
          ])

        actRes(action: "click", mss: 350,
          logs: [
            ["api2", 380], // ignored
            ["api1", 385], // ignored
            ["api3", 387], // new
            ["api4", 400], // new
            ["api5", 470], // new
          ])

        actRes(action: "terminate", mss: 475,
          logs: [
            ["api5", 480], //  ignored
            ["api7", 510], //  new
            ["api6", 520], //  new
            ["api2", 540], //  ignored
          ])
      }

      apk(name: "org.another.interesting.program", monitorInitTime: init2, explorationEndTimeMss: 600) {

        actRes(action: "reset", mss: 0,
          logs: [
            ["api1", 10], //  new
            ["api2", 50], //  new
            ["api3", 100], //  new
          ])

        actRes(action: "click", mss: 105, logs: [["api4", 110] /* new */ ])

        actRes(action: "reset", mss: 150, logs: [])

        actRes(action: "reset", mss: 300, logs: [])

        actRes(action: "click", mss: 350, successful: false, logs: [["api5", 370], /* new */ ["api6", 380]  /* new */])
      }
    }
    return explOut2

  }

}
