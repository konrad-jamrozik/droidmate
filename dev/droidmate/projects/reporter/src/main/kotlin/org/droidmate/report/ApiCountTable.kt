// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class ApiCountTable : CountsPartitionedByTimeTable {

  constructor(data: IApkExplorationOutput2) : super(
    data.explorationTimeInMs,
    listOf(
      headerTime,
      headerApisSeen,
      headerApiEventsSeen
    ),
    listOf(
      data.uniqueApisCountByTime,
      data.uniqueEventApiPairsCountByTime
    )
  )

  companion object {

    val headerTime = "Time_seconds"
    val headerApisSeen = "Apis_seen"
    val headerApiEventsSeen = "Api+Event_pairs_seen"

    private val IApkExplorationOutput2.uniqueApisCountByTime: Map<Long, Iterable<String>> get() {
      return this.actRess.itemsAtTimes(
        extractItems = { it.result.deviceLogs.apiLogsOrEmpty },
        startTime = this.explorationStartTime,
        extractTime = { it.time }
      ).mapValues {
        val apis = it.value
        apis.map { it.uniqueString }
      }
    }

    private val IApkExplorationOutput2.uniqueEventApiPairsCountByTime: Map<Long, Iterable<String>> get() {

      return this.actRess.itemsAtTimes(
        extractItems = RunnableExplorationActionWithResult::extractEventApiPairs,
        startTime = this.explorationStartTime,
        extractTime = EventApiPair::time
      ).mapValues {
        val eventApiPairs = it.value
        eventApiPairs.map { it.uniqueString }
      }
    }
  }
}