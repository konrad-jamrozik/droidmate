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

  val guiCoverageReports: List<GUICoverageReport> by lazy {
    this.data.map { GUICoverageReport(it, dir) }
  }

  val txtReportFiles: List<Path> by lazy {
    this.guiCoverageReports.flatMap { setOf(it.file_viewsCountsOverTime, it.file_clickFrequency) }
  }

  fun writeOut(includePlots : Boolean = true): Unit {
    this.guiCoverageReports.forEach { it.writeOut(includePlots) }
  }
}

