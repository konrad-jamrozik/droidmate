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
import org.droidmate.device.datatypes.MissingGuiSnapshot
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.strategy.WidgetStrategy

class ViewCountTable : CountsPartitionedByTimeTable {

  constructor(data: IApkExplorationOutput2) : super(
    data.explorationTimeInMs,
    listOf(
      headerTime,
      headerViewsSeen,
      headerViewsClicked
    ),
    listOf(
      data.uniqueSeenActionableViewsCountByTime,
      data.uniqueClickedViewsCountByTime
    )
  )
  
  companion object {
    
    val headerTime = "Time_seconds"
    val headerViewsSeen = "Actionable_unique_views_seen"
    val headerViewsClicked = "Actionable_unique_views_clicked"

    private val IApkExplorationOutput2.uniqueSeenActionableViewsCountByTime: Map<Long, Iterable<String>> get() {
      return this.uniqueViewCountByPartitionedTime(
        extractItems = {
          when (it.result.guiSnapshot) {
            is MissingGuiSnapshot -> emptyList()
            else -> it.result.guiSnapshot.guiState.widgets.filter { it.canBeActedUpon() }
          }
        }
      )
    }

    private val IApkExplorationOutput2.uniqueClickedViewsCountByTime: Map<Long, Iterable<String>> get() {
      return this.uniqueViewCountByPartitionedTime(extractItems = { it.clickedWidget })
    }

    private fun IApkExplorationOutput2.uniqueViewCountByPartitionedTime(
      extractItems: (RunnableExplorationActionWithResult) -> Iterable<Widget>
    ): Map<Long, Iterable<String>> {

      return this.actRess.itemsAtTime(
        startTime = this.explorationStartTime,
        extractTime = { it.action.timestamp },
        extractItems = extractItems
      ).mapValues {
        val widgets = it.value
        widgets.map { WidgetStrategy.WidgetInfo(it).uniqueString }
      }
    }
  }
}

