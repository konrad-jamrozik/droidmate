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

data class GUICoverageReport(val data: IApkExplorationOutput2, val dir: Path) {

  companion object {
    val fileNameSuffixViewsCountsOverTime = "_viewsCountsOverTime.txt"
    val fileNameSuffixClickFrequency = "_clickFrequency.txt"
  }

  private val log: Logger = LoggerFactory.getLogger(GUICoverageReport::class.java)

  init {
    require(dir.isDirectory)
  }

  private val fileNamePrefix by lazy { data.apk.fileName.replace(".", "_") }

  val fileViewsCountsOverTime: Path by lazy {
    this.dir.resolve("$fileNamePrefix$fileNameSuffixViewsCountsOverTime")
  }

  val fileClickFrequency: Path by lazy {
    this.dir.resolve("$fileNamePrefix$fileNameSuffixClickFrequency")
  }

  val tableViewsCounts: Table<Int, String, Int> by lazy { data.tableOfViewsCounts }
  val tableClickFrequency: Table<Int, String, Int> by lazy { data.tableOfClickFrequencies }

  private val tableViewsCountDataFile = this.tableViewsCounts.dataFile(fileViewsCountsOverTime)
  private val tableClickFrequencyDataFile = this.tableClickFrequency.dataFile(fileClickFrequency)

  fun writeOut(includePlots: Boolean = true) {

    log.info("Writing out GUI coverage report for ${data.apk.fileName}")

    log.info("Writing out $tableViewsCountDataFile")
    tableViewsCountDataFile.writeOut()
    
    if (includePlots) {
      log.info("Writing out ${tableViewsCountDataFile.plotFile}")
      tableViewsCountDataFile.writeOutPlot()
    }

    log.info("Writing out $tableClickFrequencyDataFile")
    tableClickFrequencyDataFile.writeOut()
    
  }
}