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
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import java.time.Duration
import java.util.*

class GUICoverage(val data: IApkExplorationOutput2) {

  val table: Table<Int, String, Int> by lazy {

    val widgetsSeen = WidgetsSeen(data)

    // KJA extract ms step
    val timeRange = 0.rangeTo(data.explorationTimeInMs).step(1000)

    val rows: List<Triple<Int, Int, Int>> = timeRange.mapIndexed { tickIndex, timePassed ->
      // KJA the "widgetsSeen.byTime" is the only variable, all the rest can be extracted. The variable is "some value that got increment by given other value"
      Triple(tickIndex, timePassed, widgetsSeen.byTime(timePassed))
    }

    // KJA extract "make table from (column headers, rows represented by triplets)
    tableBuilder().apply {
      rows.forEach { row ->
        // KJA dry up column headers
        put(row.first, "Time", row.second)
        put(row.first, "Views seen", row.third)
      }
    }.build()
  }

  private fun tableBuilder(): ImmutableTable.Builder<Int, String, Int> {

    return ImmutableTable
      .Builder<Int, String, Int>()
      .orderColumnsBy(compareBy {
        when (it) {
          "Time" -> 0
          "Views seen" -> 1
          else -> throw UnexpectedIfElseFallthroughError()
        }
      })
      .orderRowsBy(naturalOrder<Int>())
  }
}

class WidgetsSeen(val data: IApkExplorationOutput2) {
  fun byTime(timePassed: Int): Int {

    val widgetsAtTime: List<Pair<Long, List<Widget>>> = data.actRess.map {
      val timePassedForWidgets = Duration.between(data.explorationStartTime, it.action.timestamp).toMillis()
      val widgets: List<Widget> = it.result.guiSnapshot.guiState.widgets
      Pair(timePassedForWidgets, widgets)
    }

    var uniqueWidgetsSeenAccumulator: MutableList<Widget> = ArrayList()

    val uniqueWidgetsAtTime: List<Pair<Long, List<Widget>>> = widgetsAtTime.map {

      // KJA
      val newUniqueWidgets: List<Widget> = it.second /* all such widgets in current item that are not yet in accumulator with regards to uniqueness comparison */
        uniqueWidgetsSeenAccumulator.addAll(newUniqueWidgets)
      it.copy(second = uniqueWidgetsSeenAccumulator.toList())
      //Pair(it.first, uniqueWidgets)
    }

    // KJA
    // val widgetsByTime: List<Widget> = uniqueWidgetsAtTime.dropWhile { it.first < timePassed }.first().second

    return timePassed / 2
  }

}
