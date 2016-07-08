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

data class GUICoverageReport(val data: IApkExplorationOutput2, val dir: Path) {

  private val log: Logger = LoggerFactory.getLogger(GUICoverageReport::class.java)

  init {
    require(dir.isDirectory)
  }

  fun writeOut(includePlots: Boolean = true) {

    log.info("Writing out tabular data report for ${data.apk.fileName}")

    log.info("Writing out $viewCountFile")
    viewCountFile.writeOut()

    log.info("Writing out $clickFrequencyFile")
    clickFrequencyFile.writeOut()

    log.info("Writing out $apiCountFile")
    apiCountFile.writeOut()

    if (includePlots) {
      log.info("Writing out ${viewCountFile.plotFile}")
      viewCountFile.writeOutPlot()

      log.info("Writing out ${apiCountFile.plotFile}")
      apiCountFile.writeOutPlot()
    }
  }

  private val viewCountFile by lazy { TableDataFile(viewCountTable, viewCountPath) }
  private val clickFrequencyFile by lazy { TableDataFile(clickFrequencyTable, clickFrequencyPath) }
  private val apiCountFile by lazy { TableDataFile(apiCountTable, apiCountPath) }

  val viewCountTable by lazy { TableViewCount.build(data) }
  val clickFrequencyTable by lazy { TableClickFrequency.build(data) }
  val apiCountTable by lazy { TableApiCount.build(data) }
  
  val viewCountPath: Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixViewCount") }
  val clickFrequencyPath: Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixClickFrequency") }
  val apiCountPath: Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixApiCount") }

  private val fileNamePrefix by lazy { data.apk.fileName.replace(".", "_") }
  
  companion object {
    val fileNameSuffixViewCount = "_viewCount.txt"
    val fileNameSuffixClickFrequency = "_clickFrequency.txt"
    val fileNameSuffixApiCount = "_apiCount.txt"
  }
}