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

import com.google.common.collect.Table
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class AggregateStatsTable private constructor(val table: Table<Int, String, String>) : Table<Int, String, String> by table {

  constructor(data: List<IApkExplorationOutput2>) : this(AggregateStatsTable.build(data))
  
  companion object {
    val headerApkName = "file_name"
    val headerPackageName = "package_name"
    val headerExplorationTimeInSeconds = "exploration_seconds"
    val headerActionsCount = "actions"
    val headerResetActionsCount = "in_this_reset_actions"
    val headerViewsSeenCount = "actionable_unique_views_seen_at_least_once"
    val headerViewsClickedCount = "actionable_unique_views_clicked_or_long_clicked_at_least_once"
    val headerApisSeenCount = "unique_apis"
    val headerEventApiPairsSeenCount = "unique_event_api_pairs"
    val headerException = "exception"

    fun build(data: List<IApkExplorationOutput2>): Table<Int, String, String> {

      return buildTable(
        headers = listOf(
          headerApkName,
          headerPackageName,
          headerExplorationTimeInSeconds,
          headerActionsCount,
          headerResetActionsCount,
          headerViewsSeenCount,
          headerViewsClickedCount,
          headerApisSeenCount,
          headerEventApiPairsSeenCount,
          headerException
        ),
        rowCount = data.size,
        computeRow = { rowIndex ->
          val apkData = data[rowIndex]
          listOf(
            apkData.apk.fileName,
            apkData.packageName,
            apkData.explorationDuration.seconds.toString(),
            apkData.actions.size.toString(),
            apkData.resetActionsCount.toString(),
            apkData.uniqueActionableWidgets.size.toString(),
            apkData.uniqueClickedWidgets.size.toString(),
            apkData.uniqueApis.size.toString(),
            apkData.uniqueEventApiPairs.size.toString(),
            apkData.exception.toString()
          )
        }
      )
    }

  }
}