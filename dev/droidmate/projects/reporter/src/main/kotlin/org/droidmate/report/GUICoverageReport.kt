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

  init {
    require(dir.isDirectory)
  }

  fun writeOut(includePlots: Boolean = true) {

    log.info("Writing out GUI coverage report for ${data.apk.fileName}")

    log.info("Writing out $viewCountFile")
    viewCountFile.writeOut()

    if (includePlots) {
      log.info("Writing out ${viewCountFile.plotFile}")
      viewCountFile.writeOutPlot()
    }

    log.info("Writing out $clickFrequencyFile")
    clickFrequencyFile.writeOut()

  }

  private val log: Logger = LoggerFactory.getLogger(GUICoverageReport::class.java)

  private val viewCountFile by lazy { TableDataFile(viewCountTable, viewCountPath) }
  private val clickFrequencyFile by lazy { TableDataFile(clickFrequencyTable, clickFrequencyPath) }

  val viewCountTable by lazy { TableViewsCounts.build(data) }
  val clickFrequencyTable by lazy { TableClickFrequency.build(data) }

  val viewCountPath: Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixViewCount") }
  val clickFrequencyPath: Path by lazy { dir.resolve("$fileNamePrefix$fileNameSuffixClickFrequency") }

  companion object {
    val fileNameSuffixViewCount = "_viewCount.txt"
    val fileNameSuffixClickFrequency = "_clickFrequency.txt"
  }

  private val fileNamePrefix by lazy { data.apk.fileName.replace(".", "_") }
}