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
    
    /* KJA next to implement: click distribution: amount of clicks per view. X axis: no of clicks. Y axis: no of views.
    
      This will require table with column oone having the number of clicks and column two number of widgets with this amount of 
      clicks.
      
      To obtain this data we have to iterate the data structure counting the clicks. Each time a click is counted, increase a 
      counter on the view being counted. So we will have a map View -> No of clicks, to which values will be inserted/increased 
      as the data structure is iterated. The data structure iteration will look for view clicks, i.e. like in:
      org.droidmate.report.uniqueClickedViewsCountByTime
      
      This means that most likely the relevant functionality will be extracted from org.droidmate.report.itemsAtTime,
      as we just need the items, not time.
      
      This will give us the data to be transformed into the View -> No of clicks map, which then will have to be injected into
      Table reusing some code from org.droidmate.report.GUICoverage.table. However, the logic will be a bit different, because
      now we won't have any time series.
      
      Such Table would live in org.droidmate.report.GUICoverageReport, beside org.droidmate.report.GUICoverage, and would be
      written out in org.droidmate.report.GUICoverageReport.writeOut
    
     */
    
    /* KJA implement automatic generation of .pdf with charts.
      
      */

    // Asserts on the data structure
    report.guiCoverageReports.forEach {
      assertThat(it.guiCoverage.table.rowKeySet().size, greaterThan(0))
      assertThat(it.guiCoverage.table.columnKeySet(),
        hasItems(
          GUICoverage.headerTime,
          GUICoverage.headerViewsSeen,
          GUICoverage.headerViewsClicked
        )
      )
    }
    
    // Asserts on the reports written to (here - mocked) file system.
    assertThat(report.dir.fileNames, hasItem(containsString(GUICoverageReport.fileNameSuffix)))

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