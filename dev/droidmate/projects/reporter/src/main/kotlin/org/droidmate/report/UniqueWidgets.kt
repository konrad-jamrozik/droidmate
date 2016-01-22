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
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.strategy.WidgetStrategy
import java.time.Duration
import java.util.*

class UniqueWidgets(val data: IApkExplorationOutput2) {

  fun byTime(timePassed: Int): Int {

    val uniqueWidgetsByTime: List<Widget>? = uniqueWidgetsAtTime.firstOrNull() { it.first >= timePassed }?.second

    return uniqueWidgetsByTime?.count() ?: uniqueWidgetsAtTime.last().second.count()
  }

  private val uniqueWidgetsAtTime: List<Pair<Long, List<Widget>>> by lazy {

    var uniqueWidgetsAccumulator: MutableList<Widget> = ArrayList()

    val uniqueWidgetsAtTime: List<Pair<Long, List<Widget>>> = widgetsAtTime.map {

      val newUniqueWidgets = it.second.filterNot { widget ->
        uniqueWidgetsAccumulator.any {
          WidgetStrategy.WidgetInfo(it).uniqueString == WidgetStrategy.WidgetInfo(widget).uniqueString
        }
      }

      uniqueWidgetsAccumulator.addAll(newUniqueWidgets)

      it.copy(second = uniqueWidgetsAccumulator.toList())
    }

    uniqueWidgetsAtTime
  }

  private val widgetsAtTime: List<Pair<Long, List<Widget>>> by lazy {

    val zeroTimeZeroWidgets = listOf(Pair<Long, List<Widget>>(0, emptyList()))

    val widgetsAtTime: List<Pair<Long, List<Widget>>> =
      zeroTimeZeroWidgets.plus(data.actRess.map {

        // KJA KNOWN BUG got here time with relation to exploration start of -25, but it should be always > 0.
        // This + 500 is just a dirty workaround
        val timePassedForWidgets = Duration.between(data.explorationStartTime, it.action.timestamp).toMillis() + 500
        val widgets: List<Widget> = it.result.guiSnapshot.guiState.widgets

        Pair(timePassedForWidgets, widgets)
      })
    widgetsAtTime
  }

}