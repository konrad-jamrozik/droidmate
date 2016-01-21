package org.droidmate.report

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import java.nio.file.Path

class ExplorationOutput2Report(val output: ExplorationOutput2, val dir: Path) {

  fun report(): Unit {
    // KJA current work
    output.forEach {
      GUICoverageReportFile(it, dir).writeOut()
    }
  }
}

