// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
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