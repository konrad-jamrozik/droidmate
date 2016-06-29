// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.exploration.actions.ExplorationActionRunResult
import org.droidmate.exploration.actions.IExplorationActionRunResult
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.ApkExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.IDeviceLogs
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// KJA annotate all apis in appguard_apis.txt with links to doc, source and to origin

private val log: Logger = LoggerFactory.getLogger(GUICoverageReport::class.java)

val List<IApkExplorationOutput2>.withFilteredApiLogs: List<IApkExplorationOutput2> get() {

  fun filterApiLogs(output: IApkExplorationOutput2): IApkExplorationOutput2 {

    fun filterApiLogs(results: List<RunnableExplorationActionWithResult>): List<RunnableExplorationActionWithResult> {

      fun filterApiLogs(result: IExplorationActionRunResult): IExplorationActionRunResult {

        fun filterApiLogs(deviceLogs: IDeviceLogs, packageName: String): IDeviceLogs {

          return FilteredDeviceLogs(deviceLogs.apiLogsOrEmpty, packageName)
        }

        return ExplorationActionRunResult(
          result.successful,
          result.exploredAppPackageName,
          filterApiLogs(result.deviceLogs, result.exploredAppPackageName),
          result.guiSnapshot,
          result.exception
        )
      }

      return results.map { RunnableExplorationActionWithResult(it.first, filterApiLogs(it.second)) }
    }

    return ApkExplorationOutput2(output.apk, filterApiLogs(output.actRess), output.explorationStartTime, output.explorationEndTime)
  }

  return this.map { filterApiLogs(it) }
}

