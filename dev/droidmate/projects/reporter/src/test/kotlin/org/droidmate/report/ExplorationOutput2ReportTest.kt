package org.droidmate.report

import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.junit.Test

class ExplorationOutput2ReportTest {

  @Test
  fun reports() {

    val cfg = ConfigurationForTests().withMockFileSystem().get()
    val out = ReportDir(cfg.reportInputDirPath).readOutput()

    val report = ExplorationOutput2Report(out, cfg.reportInputDirPath)

    // Act
    report.writeOut()

    report.files.forEach {
      println(it.fileName)
      println(it.text())
    }
  }
}


