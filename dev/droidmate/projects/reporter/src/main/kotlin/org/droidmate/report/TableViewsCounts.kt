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

class TableViewsCounts() {
  companion object {
    
    val headerTime = "Time_seconds"
    val headerViewsSeen = "Actionable_unique_views_seen"
    val headerViewsClicked = "Actionable_unique_views_clicked"
    
    val stepSizeInMs = 1000L

    fun build(data: IApkExplorationOutput2): Table<Int, String, Int> {

      val timeRange: List<Long> = 0L.rangeTo(data.explorationTimeInMs).step(stepSizeInMs).toList()
      val uniqueSeenActionableViewsCountByTime: Map<Long, Int> = data.uniqueSeenActionableViewsCountByTime
      val uniqueClickedViewsCountByTime: Map<Long, Int> = data.uniqueClickedViewsCountByTime

      return buildTable(
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
  }
}

