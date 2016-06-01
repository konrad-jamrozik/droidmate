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
import org.droidmate.test_base.FilesystemTestFixtures
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
    report.writeOut()
    
    // KJA implement automatic generation of .pdf with charts.

    // Asserts on the data structure
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
    
    // Asserts on the reports written to (here - mocked) file system.
    assertThat(report.dir.fileNames, hasItems(
      containsString(GUICoverageReport.fileNameSuffix_viewsCountsOverTime), 
      containsString(GUICoverageReport.fileNameSuffix_clickFrequency))
    )

    val manualInspection = true
    if (manualInspection)
    {
      report.reportFiles.forEach {
        println(it.toAbsolutePath().toString())
        println(it.text())
      }
    }
  }
}