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
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class TableClickFrequency() {
  companion object {
    val headerNoOfClicks = "No_of_clicks"
    val headerViewsCount = "Views_count"

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
  }
}