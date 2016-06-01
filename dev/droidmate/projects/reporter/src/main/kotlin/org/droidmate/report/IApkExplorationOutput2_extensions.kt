// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

fun org.droidmate.exploration.data_aggregators.IApkExplorationOutput2.uniqueViewCountByPartitionedTime(
  extractItems: (org.droidmate.exploration.actions.RunnableExplorationActionWithResult) -> Iterable<org.droidmate.common.exploration.datatypes.Widget>
): Map<Long, Int> {

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
      extractUniqueString = { org.droidmate.exploration.strategy.WidgetStrategy.WidgetInfo(it).uniqueString }
    )
    .mapValues { it.value.count() }
    .partition(1000L)
    .maxValueUntilPartition(
      lastPartition = this.explorationTimeInMs.zeroDigits(3),
      partitionSize = 1000L,
      extractMax = { it.max() ?: 0 })
    .toMap()
}

fun org.droidmate.exploration.data_aggregators.IApkExplorationOutput2.uniqueWidgetCountByTime(): Map<Long, Int> {

  return this.uniqueViewCountByPartitionedTime(
    extractItems = { it.result.guiSnapshot.guiState.widgets.filter { it.canBeActedUpon() } }
  )
}

fun org.droidmate.exploration.data_aggregators.IApkExplorationOutput2.uniqueClickedWidgetCountByTime(): Map<Long, Int> {

  return this.uniqueViewCountByPartitionedTime(
    extractItems = {
      val action = it.action.base;
      when (action) {
        is org.droidmate.exploration.actions.WidgetExplorationAction -> setOf(action.widget)
        else -> emptySet()
      }
    }
  )
}