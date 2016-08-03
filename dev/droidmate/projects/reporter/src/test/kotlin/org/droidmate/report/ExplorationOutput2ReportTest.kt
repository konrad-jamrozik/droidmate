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

import org.droidmate.common.BuildConstants
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
import java.nio.file.Paths

class ExplorationOutput2ReportTest {

  val printToStdout = true
  
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
          ViewCountTable.headerTime,
          ViewCountTable.headerViewsSeen,
          ViewCountTable.headerViewsClicked
        )
      )
      assertThat(it.clickFrequencyTable.rowKeySet().size, greaterThan(0))
      assertThat(it.clickFrequencyTable.columnKeySet(),
        hasItems(
          ClickFrequencyTable.headerNoOfClicks,
          ClickFrequencyTable.headerViewsCount
        )
      )
      assertThat(it.apiCountTable.rowKeySet().size, greaterThan(0))
      assertThat(it.apiCountTable.columnKeySet(),
        hasItems(
          ApiCountTable.headerTime,
          ApiCountTable.headerApisSeen,
          ApiCountTable.headerApiEventsSeen
        )
      )      
    }
  }

  private fun assertOnFiles(report: ExplorationOutput2Report) {
    assertThat(report.dir.fileNames, hasItems(
      containsString(TabularDataReport.fileNameSuffixViewCount),
      containsString(TabularDataReport.fileNameSuffixClickFrequency),
      containsString(TabularDataReport.fileNameSuffixApiCount),
      equalTo(ExplorationOutput2Report.fileNameSummary)
    )
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

  @Test
  fun `reports to file system`()
  {
    val serExplOutput: Path = FilesystemTestFixtures.build().f_monitoredSer2
    val report = ExplorationOutput2Report(
      rawData = OutputDir(serExplOutput.parent).notEmptyExplorationOutput2,
      dir = Paths.get(BuildConstants.getTest_temp_dir_name())
    )
    report.writeOut()
  }
}