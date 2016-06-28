// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.exploration.actions.*
import org.droidmate.exploration.data_aggregators.ApkExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.DeviceLogs
import org.droidmate.exploration.device.IDeviceLogs

val List<IApkExplorationOutput2>.withFilteredApiLogs: List<IApkExplorationOutput2> get() {

  fun filterApiLogs(output: IApkExplorationOutput2): IApkExplorationOutput2 {

    fun filterApiLogs(results: List<RunnableExplorationActionWithResult>): List<RunnableExplorationActionWithResult> {

      fun filterApiLogs(result: IExplorationActionRunResult): IExplorationActionRunResult {
        
        fun filterApiLogs(deviceLogs: IDeviceLogs) : IDeviceLogs {
          val apiLogs = deviceLogs.apiLogsOrEmpty
          
          // KJA filter api logs here
          return DeviceLogs(apiLogs)
        }
        
        return ExplorationActionRunResult(result.successful, result.exploredAppPackageName, filterApiLogs(result.deviceLogs), result.guiSnapshot, result.exception)
      }

      return results.map { RunnableExplorationActionWithResult(it.first, filterApiLogs(it.second)) }
    }

    return ApkExplorationOutput2(output.apk, filterApiLogs(output.actRess), output.explorationStartTime, output.explorationEndTime)
  }

  return this.map { filterApiLogs(it) }
}

val RunnableExplorationActionWithResult.clickedWidget: Set<Widget> get() {
  val action = this.action.base
  return when (action) {
    is WidgetExplorationAction -> setOf(action.widget)
    is EnterTextExplorationAction -> setOf(action.widget)
    else -> emptySet()
  }
}
