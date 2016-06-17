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
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class TableClickFrequency() {
  
  companion object {
    val headerNoOfClicks = "No_of_clicks"
    val headerViewsCount = "Views_count"

    // Design note: Instead of "build" one could implement the logic in constructor: http://stackoverflow.com/a/37598292/986533
    fun build(data: IApkExplorationOutput2): Table<Int, String, Int> {

      val countOfViewsHavingNoOfClicks: Map<Int, Int> = data.countOfViewsHavingNoOfClicks

      return buildTable(
        headers = listOf(headerNoOfClicks, headerViewsCount),
        rowCount = countOfViewsHavingNoOfClicks.keys.size,
        computeRow = { rowIndex ->
          check(countOfViewsHavingNoOfClicks.containsKey(rowIndex))
          val noOfClicks = rowIndex
          listOf(
            noOfClicks,
            countOfViewsHavingNoOfClicks[noOfClicks]!!
          )
        })
    }

    private val IApkExplorationOutput2.countOfViewsHavingNoOfClicks: Map<Int, Int> get() {

      val clickedWidgets: List<Widget> = this.actRess.flatMap { it.clickedWidgets }
      val noOfClicksPerWidget: Map<Widget, Int> = clickedWidgets.frequencies
      val widgetsHavingNoOfClicks: Map<Int, Set<Widget>> = noOfClicksPerWidget.transpose
      val widgetsCountPerNoOfClicks: Map<Int, Int> = widgetsHavingNoOfClicks.mapValues { it.value.size }

      val maxNoOfClicks = noOfClicksPerWidget.values.max() ?: 0
      val noOfClicksProgression = 0..maxNoOfClicks step 1
      return noOfClicksProgression.associate { Pair(it, 0) } + widgetsCountPerNoOfClicks
    }
  }
}