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
 
  @Test
  fun reports() {

    val mockFs: FileSystem = mockFs()
    val cfg = Configuration.getDefault()
    val serExplOutput: Path = FilesystemTestFixtures.build().f_monitoredSer2
    val mockFsDirWithOutput: Path = mockFs.dir(cfg.droidmateOutputDir).withFiles(serExplOutput)
    
    val report = ExplorationOutput2Report(
      data = OutputDir(mockFsDirWithOutput).notEmptyExplorationOutput2,
      dir = mockFs.dir(cfg.reportOutputDir)
    )

    // Act
    // includePlots = false because plots require gnuplot, which does not work on mock file system used in this test.
    report.writeOut(includePlots = false) 
    
    // KJA2 (reporting) produce table that can be readily imported to Excel that has columns:
    // apk_name	run_time_in_seconds	actions#	in_that_resets# actionable_views_seen# views_clicked_or_long_clicked_at_least_once# unique_apis# unique_event_apis# ANRs_seen# terminated_with_exception(give exception name: launch timeout, uninstall failure, other)

    assertOnDataStructure(report)
    assertOnFiles(report)
    manualInspection(report)
  }

  private fun assertOnDataStructure(report: ExplorationOutput2Report) {
    report.guiCoverageReports.forEach {
      assertThat(it.tableViewsCounts.rowKeySet().size, greaterThan(0))
      assertThat(it.tableViewsCounts.columnKeySet(),
        hasItems(
          TableViewsCounts.headerTime,
          TableViewsCounts.headerViewsSeen,
          TableViewsCounts.headerViewsClicked
        )
      )
      assertThat(it.tableClickFrequency.rowKeySet().size, greaterThan(0))
      assertThat(it.tableClickFrequency.columnKeySet(),
        hasItems(
          TableClickFrequency.headerNoOfClicks,
          TableClickFrequency.headerViewsCount
        )
      )
    }
  }

  private fun assertOnFiles(report: ExplorationOutput2Report) {
    assertThat(report.dir.fileNames, hasItems(
      containsString(GUICoverageReport.fileNameSuffixViewsCountsOverTime),
      containsString(GUICoverageReport.fileNameSuffixClickFrequency), 
      equalTo(ExplorationOutput2Report.fileNameSummary))
    )
  }

  private fun manualInspection(report: ExplorationOutput2Report) {
    val manualInspection = true
    if (manualInspection) {
      report.txtReportFiles.forEach {
        println(it.toAbsolutePath().toString())
        println(it.text)
      }
    }
  }
}