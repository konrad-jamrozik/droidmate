package org.droidmate.report

import org.droidmate.configuration.Configuration
import org.droidmate.test_base.FilesystemTestFixtures
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Path
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

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

    /* KJA now we have "views seen". We also need:
    - "views clicked"  
    - automatic generation of .pdf with chart.
    - click distribution: amount of click per view. X axis: no of clicks. Y axis: no of views.
       */

    // Asserts on the data structure
    report.guiCoverageReports.forEach {
      assertThat(it.guiCoverage.table.rowKeySet().size, greaterThan(0))
      assertThat(it.guiCoverage.table.columnKeySet().size, `is`(2))
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



