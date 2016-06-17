// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import java.nio.file.Path

class ExplorationOutput2Report(val data: ExplorationOutput2, val dir: Path) {

  companion object {
    val fileNameSummary = "summary.txt"
  }

  val summaryFile: IDataFile by lazy { Summary(this.data, dir.resolve(fileNameSummary)) }

  val guiCoverageReports: List<GUICoverageReport> by lazy {
    data.map { GUICoverageReport(it, dir) }
  }

  val txtReportFiles: List<Path> by lazy {
    listOf(summaryFile.path) + guiCoverageReports.flatMap { setOf(it.fileViewsCountsOverTime, it.fileClickFrequency) }
  }

  fun writeOut(includePlots : Boolean = true) {
    summaryFile.writeOut()
    guiCoverageReports.forEach { it.writeOut(includePlots) }
  }
}

