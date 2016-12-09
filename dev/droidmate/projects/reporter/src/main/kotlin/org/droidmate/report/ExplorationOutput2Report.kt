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

import com.konradjamrozik.createDirIfNotExists
import org.droidmate.deleteDir
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

class ExplorationOutput2Report(rawData: List<IApkExplorationOutput2>, val dir: Path) {

  private val log: Logger = LoggerFactory.getLogger(ExplorationOutput2Report::class.java)
  
  val data: List<IApkExplorationOutput2>

  init { data = rawData.withFilteredApiLogs }
  
  fun writeOut(includePlots : Boolean = true, includeSummary: Boolean = true) {

    log.info("Writing out exploration report to $dir")
    
    dir.deleteDir()
    dir.createDirIfNotExists()
    apkReportsDir.createDirIfNotExists()
    
    if (includeSummary)
      summaryFile.writeOut()
    
    aggregateStatsFile.writeOut()

    apksTabularReports.forEach { it.writeOut(includePlots) }

    apksViewsFiles.forEach { it.writeOut() }
  }

  val apkReportsDir: Path by lazy { dir.resolve("app_reports") }
  
  val summaryFile: IDataFile by lazy { Summary(data, dir.resolve(fileNameSummary)) }

  val aggregateStatsFile: TableDataFile<Int, String, String> by lazy {
    TableDataFile(AggregateStatsTable(data), dir.resolve(fileNameAggregateStats))
  }
  
  val apksTabularReports: List<ApkTabularDataReport> by lazy { data.map { ApkTabularDataReport(it, apkReportsDir) } }
  val apksViewsFiles: List<ApkViewsFile> by lazy { data.map { ApkViewsFile(it, apkReportsDir) } }

  companion object { 
    val fileNameSummary = "summary.txt"
    val fileNameAggregateStats = "aggregate_stats.txt"
  }

  val txtReportFiles: List<Path> by lazy {
    listOf(summaryFile.path, aggregateStatsFile.path) +
      apksTabularReports.flatMap { it.paths } +
      apksViewsFiles.map { it.path }
  }
}