package org.droidmate.report

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import java.nio.file.Path

class ExplorationOutput2Report(val data: ExplorationOutput2, val dir: Path) {

  val guiCoverageReports: List<GUICoverageReport> by lazy {
    this.data.map { GUICoverageReport(it, dir) }
  }

  val reportFiles: List<Path> by lazy {
    this.guiCoverageReports.map { it.file }
  }

  fun writeOut(): Unit {
    this.guiCoverageReports.forEach { it.writeOut() }
  }
}

