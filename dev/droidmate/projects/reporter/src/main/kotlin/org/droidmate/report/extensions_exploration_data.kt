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
import org.droidmate.exploration.actions.IExplorationActionRunResult
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.actions.WidgetExplorationAction
import org.droidmate.exploration.data_aggregators.ApkExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

val List<IApkExplorationOutput2>.withFilteredApiLogs: List<IApkExplorationOutput2> get() {

  fun filterApiLogs(data: IApkExplorationOutput2): IApkExplorationOutput2 {

    fun filterApiLogs(data: List<RunnableExplorationActionWithResult>): List<RunnableExplorationActionWithResult> {

      fun filterApiLogs(data: IExplorationActionRunResult): IExplorationActionRunResult {
        return data // KJA filter api logs hers
      }

      return data.map { RunnableExplorationActionWithResult(it.first, filterApiLogs(it.second)) }
    }

    return ApkExplorationOutput2(data.apk, filterApiLogs(data.actRess), data.explorationStartTime, data.explorationEndTime)
  }

  return this.map { filterApiLogs(it) }
}

val RunnableExplorationActionWithResult.clickedWidgets: Set<Widget> get() {
  val action = this.action.base
  return when (action) {
    is WidgetExplorationAction -> setOf(action.widget)
    else -> emptySet()
  }
}
