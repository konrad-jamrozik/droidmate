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
  }

  private val log: Logger = LoggerFactory.getLogger(GUICoverageReport::class.java)

  init {
    require(dir.isDirectory)
  }

  val file_viewsCountOverTime: Path by lazy {
    this.dir.resolve("${data.apk.fileName}$fileNameSuffix_viewsCountsOverTime")
  }

  val file_clickFrequency: Path by lazy {
    this.dir.resolve("${data.apk.fileName}$fileNameSuffix_clickFrequency")
  }

  val tableViewsCounts: Table<Int, String, Int> by lazy { data.tableOfViewsCounts }

  val tableClickFrequency: Table<Int, String, Int> by lazy { data.tableOfClickFrequencies }


  fun writeOut() {

    log.info("Writing out GUI coverage report for ${data.apk.fileName} to $file_viewsCountOverTime and $fileNameSuffix_clickFrequency")
    this.tableViewsCounts.writeOut(file_viewsCountOverTime)
    this.tableClickFrequency.writeOut(file_clickFrequency)
  }
}