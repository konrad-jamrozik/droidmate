package org.droidmate.report

import com.google.common.jimfs.Jimfs
import org.droidmate.configuration.Configuration
import org.droidmate.test_base.FilesystemTestFixtures
import org.junit.Test

class ExplorationOutput2ReportTest {
 
  @Test
  fun reports() {

    val ser2 = FilesystemTestFixtures.build().f_monitoredSer2
    val mockFs = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix())

    val cfg = Configuration.getDefault()
    val report = ExplorationOutput2Report(
      OutputDir(mockFs.dir(cfg.droidmateOutputDir).withFiles(ser2)).notEmptyExplorationOutput2,
      mockFs.dir(cfg.reportOutputDir)
    )

    // Act
    report.writeOut()

    // Print out for manual assessment
    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }
  }
}



