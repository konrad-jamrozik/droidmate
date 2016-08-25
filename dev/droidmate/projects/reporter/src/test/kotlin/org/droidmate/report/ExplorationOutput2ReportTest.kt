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

import org.droidmate.configuration.Configuration
import org.droidmate.dir
import org.droidmate.fileNames
import org.droidmate.misc.BuildConstants
import org.droidmate.tests.fixture_monitoredSer2
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
    val serExplOutput: Path = fixture_monitoredSer2
    val mockFsDirWithOutput: Path = mockFs.dir(cfg.droidmateOutputDir).withFiles(serExplOutput)
    
    val report = ExplorationOutput2Report(
      rawData = OutputDir(mockFsDirWithOutput).notEmptyExplorationOutput2,
      dir = mockFs.dir(cfg.reportOutputDir)
    )

    // Act
    // "includePlots" is set to false because plots require gnuplot, which does not work on mock file system used in this test.
    report.writeOut(includePlots = false) 

    assertOnDataStructure(report)
    assertOnFiles(report)
    manualInspection(report)
  }

  private fun assertOnDataStructure(report: ExplorationOutput2Report) {
    
    assertThat(report.aggregateStatsFile.table.rowKeySet().size, greaterThan(0))
    assertThat(report.aggregateStatsFile.table.columnKeySet(),
      with(AggregateStatsTable) {
        hasItems(
          headerApkName,
          headerPackageName,
          headerExplorationTimeInSeconds,
          headerActionsCount,
          headerResetActionsCount,
          headerViewsSeenCount,
          headerViewsClickedCount,
          headerApisSeenCount,
          headerEventApiPairsSeenCount,
          headerException
        )  
      }
      
    )
    report.apksTabularReports.forEach {
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
      containsString(ApkTabularDataReport.fileNameSuffixViewCount),
      containsString(ApkTabularDataReport.fileNameSuffixClickFrequency),
      containsString(ApkTabularDataReport.fileNameSuffixApiCount),
      containsString(ApkViewsFile.fileNameSuffix),
      equalTo(ExplorationOutput2Report.fileNameSummary),
      equalTo(ExplorationOutput2Report.fileNameAggregateStats)
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
    val serExplOutput: Path = fixture_monitoredSer2
    val report = ExplorationOutput2Report(
      rawData = OutputDir(serExplOutput.parent).notEmptyExplorationOutput2,
      dir = Paths.get(BuildConstants.getTest_temp_dir_name())
    )
    report.writeOut()
  }
}