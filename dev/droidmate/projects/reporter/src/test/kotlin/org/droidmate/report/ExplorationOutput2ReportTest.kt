package org.droidmate.report

import org.droidmate.exploration.output.DroidmateOutputDir
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.junit.Test

class ExplorationOutput2ReportTest {

  @Test
  fun reports() {

    val cfg = ConfigurationForTests().get()
    val out = DroidmateOutputDir(cfg.reportInputDirPath).readOutput()
    ExplorationOutput2Report(out).report()
  }
}