// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import kotlin.comparisons.compareBy
import kotlin.comparisons.naturalOrder

// KJA rename to "TimeSeriesTable" ?
// KJA make it just implement Table interface? Or TimeSeriesTable interface, to be introduced?
class GUICoverage(val data: IApkExplorationOutput2) {

  companion object {
    val headerTime = "Time_seconds"
    val headerViewsSeen = "Actionable_unique_views_seen"
    val headerViewsClicked = "Actionable_unique_views_clicked"
  }

  private val stepSizeInMs = 1000L

  val table: Table<Int, String, Int> by lazy {

    val timeRange: LongProgression = 0L.rangeTo(data.explorationTimeInMs).step(stepSizeInMs)
    val uniqueSeenActionableViewsCountByTime: Map<Long, Int> = data.uniqueSeenActionableViewsCountByTime
    val uniqueClickedViewsCountByTime: Map<Long, Int> = data.uniqueClickedViewsCountByTime

    val rows: List<List<Int>> = timeRange.mapIndexed { tickIndex, timePassed ->
      listOf(
        tickIndex, 
        (timePassed / stepSizeInMs).toInt(), 
        uniqueSeenActionableViewsCountByTime[timePassed]!!, 
        uniqueClickedViewsCountByTime[timePassed]!!)
    }

    tableBuilder().apply {
      rows.forEach { row ->
        put(row[0], headerTime, row[1])
        put(row[0], headerViewsSeen, row[2])
        put(row[0], headerViewsClicked, row[3])
      }
    }.build()
  }

  private fun tableBuilder(): ImmutableTable.Builder<Int, String, Int> {

    return ImmutableTable
      .Builder<Int, String, Int>()
      .orderColumnsBy(compareBy<String?> {
        when (it) {
          headerTime -> 0
          headerViewsSeen -> 1
          headerViewsClicked -> 2
          else -> throw UnexpectedIfElseFallthroughError()
        }
      })
      .orderRowsBy(naturalOrder<Int>())
  }
}
