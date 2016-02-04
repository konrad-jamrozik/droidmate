package org.droidmate.report

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import java.nio.file.Path

class ExplorationOutput2Report(val output: ExplorationOutput2, val dir: Path) {

  val guiCoverageReports: List<GUICoverageReport> by lazy {
    this.output.map { GUICoverageReport(it, dir) }
  }

  val reportFiles: List<Path> by lazy {
    this.guiCoverageReports.map { it.file }
  }

  fun writeOut(): Unit {
    this.guiCoverageReports.forEach { it.writeOut() }
  }
}

