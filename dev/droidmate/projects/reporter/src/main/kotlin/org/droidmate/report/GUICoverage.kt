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

class GUICoverage(val data: IApkExplorationOutput2) {

  private val headerTime = "Time"
  private val headerViewsSeen = "Actionable unique views seen"
  private val stepSizeInMs = 1000

  val table: Table<Int, String, Int> by lazy {

    val uniqueWidgetCountByTime: Map<Int, Int> = data.uniqueWidgetCountByTime()

    val timeRange = 0.rangeTo(data.explorationTimeInMs).step(stepSizeInMs)

    val rows: List<Triple<Int, Int, Int>> = timeRange.mapIndexed { tickIndex, timePassed ->
      Triple(tickIndex, timePassed / stepSizeInMs, uniqueWidgetCountByTime[timePassed]!!)
    }

    tableBuilder().apply {
      rows.forEach { row ->
        put(row.first, headerTime, row.second)
        put(row.first, headerViewsSeen, row.third)
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
          else -> throw UnexpectedIfElseFallthroughError()
        }
      })
      .orderRowsBy(naturalOrder<Int>())
  }
}
