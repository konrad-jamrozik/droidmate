// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.Table
import com.konradjamrozik.isDirectory
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

class GUICoverageReport(val data: IApkExplorationOutput2, val dir: Path) {

  companion object {
    val fileNameSuffix_viewsCountsOverTime = "_viewsCountsOverTime.txt"
    val fileNameSuffix_clickFrequency = "_clickFrequency.txt"
    // KJA dry up
    val fileNameSuffix_viewsCountsOverTimePlot = "_viewsCountsOverTime.pdf"
  }

  private val log: Logger = LoggerFactory.getLogger(GUICoverageReport::class.java)

  init {
    require(dir.isDirectory)
  }

  private val fileNamePrefix by lazy { data.apk.fileName.replace(".", "_") }

  val file_viewsCountsOverTime: Path by lazy {
    this.dir.resolve("$fileNamePrefix$fileNameSuffix_viewsCountsOverTime")
  }

  val file_clickFrequency: Path by lazy {
    this.dir.resolve("$fileNamePrefix$fileNameSuffix_clickFrequency")
  }

  val file_viewsCountsOverTimePlot: Path by lazy {
    this.dir.resolve("$fileNamePrefix$fileNameSuffix_viewsCountsOverTimePlot")
  }


  val tableViewsCounts: Table<Int, String, Int> by lazy { data.tableOfViewsCounts }

  val tableClickFrequency: Table<Int, String, Int> by lazy { data.tableOfClickFrequencies }

  private val tableViewsCountDataFile = this.tableViewsCounts.dataFile(file_viewsCountsOverTime)
  private val tableClickFrequencyDataFile = this.tableClickFrequency.dataFile(file_clickFrequency)

  fun writeOut(includePlots: Boolean = true) {

    // KJA add here mention of plot file
    log.info("Writing out GUI coverage report for ${data.apk.fileName}")

    log.info("Writing out ${tableViewsCountDataFile.toString()}")
    tableViewsCountDataFile.writeOut()
    if (includePlots) {
      log.info("Writing out ${tableViewsCountDataFile.plotFile.toString()}")
      tableViewsCountDataFile.writeOutPlot()
    }

    log.info("Writing out ${tableClickFrequencyDataFile.toString()}")
    tableClickFrequencyDataFile.writeOut()
    
  }
}