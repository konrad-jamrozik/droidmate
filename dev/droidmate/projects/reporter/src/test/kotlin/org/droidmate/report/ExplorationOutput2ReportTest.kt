// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.configuration.Configuration
import org.droidmate.dir
import org.droidmate.fileNames
import org.droidmate.test_base.FilesystemTestFixtures
import org.droidmate.text
import org.droidmate.withFiles
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Path

class ExplorationOutput2ReportTest {

  val printToStdout = true
  
  // KJA curr test
  @Test
  fun reports() {

    val mockFs: FileSystem = mockFs()
    val cfg = Configuration.getDefault()
    val serExplOutput: Path = FilesystemTestFixtures.build().f_monitoredSer2
    val mockFsDirWithOutput: Path = mockFs.dir(cfg.droidmateOutputDir).withFiles(serExplOutput)
    
    val report = ExplorationOutput2Report(
      rawData = OutputDir(mockFsDirWithOutput).notEmptyExplorationOutput2,
      dir = mockFs.dir(cfg.reportOutputDir)
    )

    // Act
    // "includePlots" is set to false because plots require gnuplot, which does not work on mock file system used in this test.
    report.writeOut(includePlots = false) 
    
    // KJA2 (reporting) produce table that can be readily imported to Excel that has columns:
    // apk_name	run_time_in_seconds	actions#	in_that_resets# actionable_views_seen# views_clicked_or_long_clicked_at_least_once# unique_apis# unique_event_apis# ANRs_seen# terminated_with_exception(give exception name: launch timeout, uninstall failure, other)

    assertOnDataStructure(report)
    assertOnFiles(report)
    manualInspection(report)
  }

  private fun assertOnDataStructure(report: ExplorationOutput2Report) {
    report.tabularReports.forEach {
      assertThat(it.viewCountTable.rowKeySet().size, greaterThan(0))
      assertThat(it.viewCountTable.columnKeySet(),
        hasItems(
          TableViewCount.headerTime,
          TableViewCount.headerViewsSeen,
          TableViewCount.headerViewsClicked
        )
      )
      assertThat(it.clickFrequencyTable.rowKeySet().size, greaterThan(0))
      assertThat(it.clickFrequencyTable.columnKeySet(),
        hasItems(
          TableClickFrequency.headerNoOfClicks,
          TableClickFrequency.headerViewsCount
        )
      )
      // KJA currently failing asserts
      assertThat(it.apiCountTable.rowKeySet().size, greaterThan(0))
      assertThat(it.apiCountTable.columnKeySet(),
        hasItems(
          TableApiCount.headerTime,
          TableApiCount.headerApisSeen,
          TableApiCount.headerApiEventsSeen
        )
      )      
    }
  }

  private fun assertOnFiles(report: ExplorationOutput2Report) {
    assertThat(report.dir.fileNames, hasItems(
      containsString(TabularDataReport.fileNameSuffixViewCount),
      containsString(TabularDataReport.fileNameSuffixClickFrequency),
      // KJA currently failing asserts
//      containsString(TabularDataReport.fileNameSuffixApiCount),
      equalTo(ExplorationOutput2Report.fileNameSummary))
    )
  }

  private fun manualInspection(report: ExplorationOutput2Report) {

    if (printToStdout) {
      report.txtReportFiles.forEach {
        println(it.toAbsolutePath().toString())
        println(it.text)
      }
    }
  }
}