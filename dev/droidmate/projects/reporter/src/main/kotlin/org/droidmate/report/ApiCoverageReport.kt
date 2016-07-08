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
    log.info("Writing out $apiCountFile")
    apiCountFile.writeOut()

    if (includePlots) {
      log.info("Writing out ${apiCountFile.plotFile}")
      apiCountFile.writeOutPlot()
    }
  }
  private val apiCountFile by lazy { TableDataFile(apiCountTable, apiCountPath) }
  
  val apiCountTable by lazy { TableApiCount.build(data) }

  val apiCountPath: Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixApiCount") }

  private val fileNamePrefix by lazy { data.apk.fileName.replace(".", "_") }

  companion object {
    val fileNameSuffixApiCount = "_apiCount.txt"
  }
}