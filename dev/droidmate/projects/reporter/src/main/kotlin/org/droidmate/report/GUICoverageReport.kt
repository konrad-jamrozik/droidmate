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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

class GUICoverageReport(val data: IApkExplorationOutput2, val dir: Path) {

  private val log: Logger = LoggerFactory.getLogger(GUICoverageReport::class.java)

  val file: Path by lazy {
    this.dir.resolve("${data.apk.fileName}_GUIReportFile.txt")
  }

  val guiCoverage: GUICoverage by lazy {
    GUICoverage(this.data)
  }

  fun writeOut() {

    log.info("Writing out GUI coverage report for ${data.apk.fileName} to $file")
    this.guiCoverage.table.writeOut(file)
  }
  }