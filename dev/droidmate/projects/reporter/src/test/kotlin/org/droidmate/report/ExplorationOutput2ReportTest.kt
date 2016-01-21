package org.droidmate.report

import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.junit.Test

class ExplorationOutput2ReportTest {

  @Test
  fun reports() {

    // KJA use ConfigurationForTests().withMockFileSystem().get()
    // For that, I have to copy data to the mock file system. See
    val cfg = ConfigurationForTests().get()
    val out = ReportDir(cfg.reportInputDirPath).readOutput()

    val report = ExplorationOutput2Report(out, cfg.reportInputDirPath)

    // Act
    report.writeOut()

    report.reportFiles.forEach {
      println(it.fileName)
      println(it.text())
    }
  }
}


