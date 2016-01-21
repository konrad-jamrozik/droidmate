package org.droidmate.report

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import java.nio.file.Path

class ExplorationOutput2Report(val output: ExplorationOutput2, val dir: Path) {

  val files: List<Path>
    get() = emptyList() // KJA current work

  fun writeOut(): Unit {
    // KJA current work
    output.forEach {
      GUICoverageReportFile(it, dir).writeOut()
    }
  }
}

