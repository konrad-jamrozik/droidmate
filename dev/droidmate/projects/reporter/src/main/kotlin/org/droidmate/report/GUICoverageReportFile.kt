// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import java.nio.file.Path

class GUICoverageReportFile(val data: IApkExplorationOutput2, val dir: Path) {

  val file = dir.resolve("${data.apk.fileName}_GUIReportFile.txt")

  fun writeOut() {

    GUICoverage(data).toTable().writeOut(file)
  }
}