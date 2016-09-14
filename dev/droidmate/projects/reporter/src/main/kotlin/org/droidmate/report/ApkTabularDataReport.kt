// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.report

import com.konradjamrozik.isDirectory
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import java.nio.file.Path

data class ApkTabularDataReport(val data: IApkExplorationOutput2, val dir: Path) {

//  private val log: Logger = LoggerFactory.getLogger(ApkTabularDataReport::class.java)

  init {
    require(dir.isDirectory)
  }

  fun writeOut(includePlots: Boolean = true) {

//    log.info("Writing out table report for ${data.apk.fileName}")

//    log.info("Writing out $viewCountFile")
    viewCountFile.writeOut()

//    log.info("Writing out $clickFrequencyFile")
    clickFrequencyFile.writeOut()

//    log.info("Writing out $apiCountFile")
    apiCountFile.writeOut()

    if (includePlots) {
//      log.info("Writing out ${viewCountFile.plotFile}")
      viewCountFile.writeOutPlot()

//      log.info("Writing out ${apiCountFile.plotFile}")
      apiCountFile.writeOutPlot()
    }
  }

  // @formatter:off
  val viewCountFile      by lazy { TableDataFile(viewCountTable      , viewCountPath) }
  val clickFrequencyFile by lazy { TableDataFile(clickFrequencyTable , clickFrequencyPath) }
  val apiCountFile       by lazy { TableDataFile(apiCountTable       , apiCountPath) }

  val viewCountTable      by lazy { ViewCountTable(data) }
  val clickFrequencyTable by lazy { ClickFrequencyTable(data) }
  val apiCountTable       by lazy { ApiCountTable(data) }
  
  val viewCountPath      : Path by lazy { dir.resolve("${data.apkFileNameWithUnderscoresForDots}$fileNameSuffixViewCount") }
  val clickFrequencyPath : Path by lazy { dir.resolve("${data.apkFileNameWithUnderscoresForDots}$fileNameSuffixClickFrequency") }
  val apiCountPath       : Path by lazy { dir.resolve("${data.apkFileNameWithUnderscoresForDots}$fileNameSuffixApiCount") }
  
  val paths by lazy { setOf(viewCountPath, clickFrequencyPath, apiCountPath) }

  
  companion object {
    val fileNameSuffixViewCount      = "_viewCount.txt"
    val fileNameSuffixClickFrequency = "_clickFrequency.txt"
    val fileNameSuffixApiCount       = "_apiCount.txt"
  }
  // @formatter:on
}