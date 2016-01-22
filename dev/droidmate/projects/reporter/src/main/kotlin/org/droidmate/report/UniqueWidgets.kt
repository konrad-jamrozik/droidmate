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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

class UniqueWidgets(val data: IApkExplorationOutput2) {

  val DEBUGlog: Logger = LoggerFactory.getLogger("UniqueWidgets")

  fun byTime(timePassed: Int): Int {

    DEBUGlog.info("By time for time passed of $timePassed")

    val uniqueWidgetsByTime: List<Widget>? = uniqueWidgetsAtTime.firstOrNull() { it.first >= timePassed }?.second

    return uniqueWidgetsByTime?.count() ?: uniqueWidgetsAtTime.last().second.count()
  }

  private val uniqueWidgetsAtTime: List<Pair<Long, List<Widget>>> by lazy {

    DEBUGlog.info("DEBUG Computing unique widgets at time")

    var uniqueWidgetsAccumulator: MutableList<Widget> = ArrayList()

    val uniqueWidgetsAtTime: List<Pair<Long, List<Widget>>> = widgetsAtTime.map {

      DEBUGlog.info("DEBUG Processing pair having time ${it.first}")

      val newUniqueWidgets = it.second.filterNot { widget ->
        uniqueWidgetsAccumulator.any {
          WidgetStrategy.WidgetInfo(it).uniqueString == WidgetStrategy.WidgetInfo(widget).uniqueString
        }
      }

      uniqueWidgetsAccumulator.addAll(newUniqueWidgets)

      // KJA this is slow + debugger never reaches this place, even though logs are output.
      // Maybe because it is a lazy property called by other lazy property?
      DEBUGlog.info("DEBUG Copying accumulator")
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