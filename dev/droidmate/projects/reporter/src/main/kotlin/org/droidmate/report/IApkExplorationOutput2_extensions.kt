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
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.actions.WidgetExplorationAction
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.strategy.WidgetStrategy.WidgetInfo

fun IApkExplorationOutput2.uniqueViewCountByPartitionedTime(
  extractItems: (RunnableExplorationActionWithResult) -> Iterable<Widget>
): Map<Long, Int> {

  val partitionSize = 1000L
  return this
    .actRess
    .itemsAtTime(
      startTime = this.explorationStartTime,
      extractTime = { it.action.timestamp },
      extractItems = extractItems
    )
    .mapKeys {
      // KNOWN BUG got here time with relation to exploration start of -25, but it should be always > 0.
      // The currently applied workaround is to add 500 milliseconds.
      it.key + 500L
    }
    .accumulateUniqueStrings(
      extractUniqueString = { WidgetInfo(it).uniqueString }
    )
    .mapValues { it.value.count() }
    .partition(partitionSize)
    .accumulateMaxes(extractMax = { it.max() ?: 0 })
    .padPartitions(partitionSize, lastPartition = this.explorationTimeInMs.zeroDigits(3))
}

val IApkExplorationOutput2.uniqueSeenActionableViewsCountByTime: Map<Long, Int> 
get() {

  return this.uniqueViewCountByPartitionedTime(
    extractItems = { it.result.guiSnapshot.guiState.widgets.filter { it.canBeActedUpon() } }
  )
}

val IApkExplorationOutput2.uniqueClickedViewsCountByTime: Map<Long, Int> 
  get() {

  return this.uniqueViewCountByPartitionedTime(
    extractItems = {
      val action = it.action.base;
      when (action) {
        is WidgetExplorationAction -> setOf(action.widget)
        else -> emptySet()
      }
    }
  )
}