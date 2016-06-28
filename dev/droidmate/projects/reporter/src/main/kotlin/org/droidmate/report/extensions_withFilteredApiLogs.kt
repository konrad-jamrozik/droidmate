// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.apis.ExcludedApis
import org.droidmate.apis.IApi
import org.droidmate.exploration.actions.ExplorationActionRunResult
import org.droidmate.exploration.actions.IExplorationActionRunResult
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.ApkExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.DeviceLogs
import org.droidmate.exploration.device.IDeviceLogs
import org.droidmate.exploration.output.FilteredApis

val List<IApkExplorationOutput2>.withFilteredApiLogs: List<IApkExplorationOutput2> get() {

  fun filterApiLogs(output: IApkExplorationOutput2): IApkExplorationOutput2 {

    fun filterApiLogs(results: List<RunnableExplorationActionWithResult>): List<RunnableExplorationActionWithResult> {

      fun filterApiLogs(result: IExplorationActionRunResult): IExplorationActionRunResult {

        fun filterApiLogs(deviceLogs: IDeviceLogs, packageName: String): IDeviceLogs {

          return DeviceLogs(
            deviceLogs.apiLogsOrEmpty
              .apply { forEach { it.checkIsInternalMonitorLog() } }
              .filterNot {
                it.isRedundant
                  || it.isExcluded
                  || it.isCallToStartInternalActivity(packageName
                )
              }
          )
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

fun IApi.checkIsInternalMonitorLog() {
  check(!FilteredApis.isStackTraceOfMonitorTcpServerSocketInit(this.stackTraceFrames),
    { "The Socket.<init> monitor logs were expected to be removed by monitor before being sent to the host machine." })
}

val IApi.isRedundant: Boolean get() {
  return FilteredApis.isStackTraceOfRedundantApiCall(this.stackTraceFrames)
}

val IApi.isExcluded: Boolean get() {
  // KJA investigate if this can be simplified into oblivion. Maybe pull the excluded APIs list from a file? Will not require recompilation.
  return ExcludedApis().contains(this.methodName)
}