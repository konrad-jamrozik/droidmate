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

// KJA make it just implement Table interface? Or TimeSeriesTable interface, to be introduced?
class GUICoverage(val data: IApkExplorationOutput2) {

  companion object {
    val headerTime = "Time_seconds"
    val headerViewsSeen = "Actionable_unique_views_seen"
    val headerViewsClicked = "Actionable_unique_views_clicked"
    
    // KJA separate from values above
    val headerNoOfClicks = "No_of_clicks"
    val headerViewsCount = "Views_count"
    
  }

  private val stepSizeInMs = 1000L

  val tableViewsCounts: Table<Int, String, Int> by lazy {

    val timeRange: List<Long> = 0L.rangeTo(data.explorationTimeInMs).step(stepSizeInMs).toList()
    val uniqueSeenActionableViewsCountByTime: Map<Long, Int> = data.uniqueSeenActionableViewsCountByTime
    val uniqueClickedViewsCountByTime: Map<Long, Int> = data.uniqueClickedViewsCountByTime

    buildTable(
      headers = listOf(headerTime, headerViewsSeen, headerViewsClicked),
      rowCount = timeRange.size,
      computeRow = { rowIndex ->
        val timePassed = timeRange[rowIndex]
        listOf(
          (timePassed / stepSizeInMs).toInt(),
          uniqueSeenActionableViewsCountByTime[timePassed]!!,
          uniqueClickedViewsCountByTime[timePassed]!!)
      })
  }

  val tableClickFrequency: Table<Int, String, Int> by lazy {

    val countOfViewsHavingNoOfClicks: Map<Int, Int> = data.countOfViewsHavingNoOfClicks

    buildTable(
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

