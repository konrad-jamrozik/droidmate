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

import org.droidmate.device.datatypes.Widget
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

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
        extractItems = { it.actionableWidgets }
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
        widgets.map { it.uniqueString }
      }
    }
  }
}

