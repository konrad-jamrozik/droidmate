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
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.actions.WidgetExplorationAction
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2


val RunnableExplorationActionWithResult.clickedWidgets: Set<Widget> get() {
  val action = this.action.base;
  return when (action) {
    is WidgetExplorationAction -> setOf(action.widget)
    else -> emptySet()
  }
}

val IApkExplorationOutput2.tableOfViewsCounts: Table<Int, String, Int> get() {
  return TableViewsCounts.build(this)
}

val IApkExplorationOutput2.tableOfClickFrequencies: Table<Int, String, Int> get() {
  return TableClickFrequency.build(this)
}
  