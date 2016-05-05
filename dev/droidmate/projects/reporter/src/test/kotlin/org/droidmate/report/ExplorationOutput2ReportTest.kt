package org.droidmate.report

import org.droidmate.configuration.Configuration
import org.droidmate.test_base.FilesystemTestFixtures
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Path

class ExplorationOutput2ReportTest {
 
  @Test
  fun reports() {

    val mockFs: FileSystem = mockFs()
    val cfg = Configuration.getDefault()
    val serExplOutput: Path = FilesystemTestFixtures.build().f_monitoredSer2

    // define sut
    val report = ExplorationOutput2Report(
      OutputDir(mockFs.dir(cfg.droidmateOutputDir).withFiles(serExplOutput)).notEmptyExplorationOutput2,
      mockFs.dir(cfg.reportOutputDir)
    )

    // Act
    report.writeOut()

    // print out for manual assessment
    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }
  }
}



