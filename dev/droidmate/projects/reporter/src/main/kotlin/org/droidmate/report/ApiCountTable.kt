// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.Table
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class ApiCountTable private constructor(val table: Table<Int, String, Int>) : Table<Int, String, Int> by table {

    constructor(data: IApkExplorationOutput2) : this(ApiCountTable.build(data))
  
    companion object {

      val headerTime = "Time_seconds"
      val headerApisSeen = "Apis_seen"
      val headerApiEventsSeen = "Api+Event_pairs_seen"

      // KJA DRY with ViewCountTable
      fun build(data: IApkExplorationOutput2): Table<Int, String, Int> {
        
        val timeRange: List<Long> = 0L.rangeTo(data.explorationTimeInMs).step(ReportTable.parititonSize).toList()
        val uniqueApisCountByTime = data.uniqueApisCountByTime
        val uniqueEventApiPairsCountByTime = data.uniqueEventApiPairsCountByTime

        return buildTable(
          headers = listOf(headerTime, headerApisSeen, headerApiEventsSeen),
          rowCount = timeRange.size,
          computeRow = { rowIndex ->
            val timePassed = timeRange[rowIndex]
            listOf(
              (timePassed / ReportTable.parititonSize).toInt(),
              uniqueApisCountByTime[timePassed]!!,
              uniqueEventApiPairsCountByTime[timePassed]!!)
          }
        )
      }

      private val IApkExplorationOutput2.uniqueApisCountByTime: Map<Long, Int> get() {
        return this.actRess.itemsAtTimes(
          extractItems = { it.result.deviceLogs.apiLogsOrEmpty },
          startTime = this.explorationStartTime,
          extractTime = { it.time }
        ).countsPartitionedByTime(
          extractUniqueString = { it.uniqueString },
          partitionSize = ReportTable.parititonSize,
          lastPartition = this.explorationTimeInMs
        )
      }

      private val IApkExplorationOutput2.uniqueEventApiPairsCountByTime: Map<Long, Int> get() {

        return this.actRess.itemsAtTimes(
          extractItems = RunnableExplorationActionWithResult::extractEventApiPairs,
          startTime = this.explorationStartTime,
          extractTime = EventApiPair::time
        ).countsPartitionedByTime(
          extractUniqueString = EventApiPair::uniqueString,
          partitionSize = ReportTable.parititonSize,
          lastPartition = this.explorationTimeInMs
        )
      }


  }
}