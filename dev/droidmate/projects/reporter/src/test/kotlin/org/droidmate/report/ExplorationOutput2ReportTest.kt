package org.droidmate.report

import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.junit.Test

class ExplorationOutput2ReportTest {

  @Test
  fun reports() {

    val cfg = ConfigurationForTests().get()
    val out = ReportDir(cfg.reportInputDirPath).readOutput()

    // Act
    ExplorationOutput2Report(out, cfg.reportInputDirPath).report()
  }
}

