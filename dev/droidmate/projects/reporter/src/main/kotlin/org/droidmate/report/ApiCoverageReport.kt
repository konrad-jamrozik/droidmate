// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.konradjamrozik.isDirectory
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

// KJA curr work. Base it on GuiCoverageReport
data class ApiCoverageReport(val data: IApkExplorationOutput2, val dir: Path) {

  private val log: Logger = LoggerFactory.getLogger(ApiCoverageReport::class.java)

  init {
    require(dir.isDirectory)
  }

  fun writeOut(includePlots: Boolean = true) {

    log.info("Writing out API coverage report for ${data.apk.fileName}")
    log.info("TODO!")

    if (includePlots) {
      log.info("includePlots - TODO!")
    }
  }

  val apiCountTable by lazy { TableApiCount.build(data) }
  
  companion object {
    val fileNameSuffixApiCount = "_apiCount.txt"
  }
}