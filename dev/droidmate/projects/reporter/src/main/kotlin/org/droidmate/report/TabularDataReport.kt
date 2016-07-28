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

data class TabularDataReport(val data: IApkExplorationOutput2, val dir: Path) {

  private val log: Logger = LoggerFactory.getLogger(TabularDataReport::class.java)

  init {
    require(dir.isDirectory)
  }

  fun writeOut(includePlots: Boolean = true) {

    log.info("Writing out table report for ${data.apk.fileName}")

    log.info("Writing out $viewCountFile")
    viewCountFile.writeOut()

    log.info("Writing out $clickFrequencyFile")
    clickFrequencyFile.writeOut()

    log.info("Writing out $apiCountFile")
    apiCountFile.writeOut()

    if (includePlots) {
      log.info("Writing out ${viewCountFile.plotFile}")
      viewCountFile.writeOutPlot()

      // KJA 1 ensure plots for api count file are rendered correctly. See test: org.droidmate.report.functionsKtTest.plots
      log.info("Writing out ${apiCountFile.plotFile}")
      apiCountFile.writeOutPlot()
    }
  }

  // @formatter:off
  val viewCountFile      by lazy { TableDataFile(viewCountTable      , viewCountPath) }
  val clickFrequencyFile by lazy { TableDataFile(clickFrequencyTable , clickFrequencyPath) }
  val apiCountFile       by lazy { TableDataFile(apiCountTable       , apiCountPath) }

  val viewCountTable      by lazy { TableViewCount(data) }
  val clickFrequencyTable by lazy { TableClickFrequency(data) }
  val apiCountTable       by lazy { TableApiCount(data) }
  
  val viewCountPath      : Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixViewCount") }
  val clickFrequencyPath : Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixClickFrequency") }
  val apiCountPath       : Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixApiCount") }
  
  val paths by lazy { setOf(viewCountPath, clickFrequencyPath, apiCountPath) }

  private val fileNamePrefix by lazy { data.apk.fileName.replace(".", "_") }
  
  companion object {
    val fileNameSuffixViewCount      = "_viewCount.txt"
    val fileNameSuffixClickFrequency = "_clickFrequency.txt"
    val fileNameSuffixApiCount       = "_apiCount.txt"
  }
  // @formatter:on
}