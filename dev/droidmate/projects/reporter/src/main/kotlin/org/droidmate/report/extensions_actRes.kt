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
import org.droidmate.exploration.actions.EnterTextExplorationAction
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.actions.WidgetExplorationAction

val RunnableExplorationActionWithResult.clickedWidget: Set<Widget> get() {
  val action = this.action.base
  return when (action) {
    is WidgetExplorationAction -> setOf(action.widget)
    is EnterTextExplorationAction -> setOf(action.widget)
    else -> emptySet()
  }
}

fun RunnableExplorationActionWithResult.extractEventApiPairs(): List<EventApiPair> {
  return this.result.deviceLogs.apiLogsOrEmpty.map { apiLog -> EventApiPair(this, apiLog) }
}