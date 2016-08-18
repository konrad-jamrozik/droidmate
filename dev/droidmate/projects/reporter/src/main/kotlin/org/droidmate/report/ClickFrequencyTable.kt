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
import com.konradjamrozik.frequencies
import com.konradjamrozik.transpose
import org.droidmate.device.datatypes.Widget
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class ClickFrequencyTable private constructor(val table: Table<Int, String, Int>) : Table<Int, String, Int> by table {

  constructor(data: IApkExplorationOutput2) : this(ClickFrequencyTable.build(data))
  
  companion object {
    val headerNoOfClicks = "No_of_clicks"
    val headerViewsCount = "Views_count"

    fun build(data: IApkExplorationOutput2): Table<Int, String, Int> {

      val countOfViewsHavingNoOfClicks: Map<Int, Int> = data.countOfViewsHavingNoOfClicks

      return buildTable(
        headers = listOf(headerNoOfClicks, headerViewsCount),
        rowCount = countOfViewsHavingNoOfClicks.keys.size,
        computeRow = { rowIndex ->
          check(countOfViewsHavingNoOfClicks.containsKey(rowIndex))
          val noOfClicks = rowIndex
          listOf(
            noOfClicks,
            countOfViewsHavingNoOfClicks[noOfClicks]!!
          )
        }
      )
    }

    private val IApkExplorationOutput2.countOfViewsHavingNoOfClicks: Map<Int, Int> get() {

      val clickedWidgets: List<Widget> = this.actRess.flatMap { it.clickedWidget }
      val noOfClicksPerWidget: Map<Widget, Int> = clickedWidgets.frequencies
      val widgetsHavingNoOfClicks: Map<Int, Set<Widget>> = noOfClicksPerWidget.transpose
      val widgetsCountPerNoOfClicks: Map<Int, Int> = widgetsHavingNoOfClicks.mapValues { it.value.size }

      val maxNoOfClicks = noOfClicksPerWidget.values.max() ?: 0
      val noOfClicksProgression = 0..maxNoOfClicks step 1
      val zeroWidgetsCountsPerNoOfClicks = noOfClicksProgression.associate { Pair(it, 0) }
      
      return zeroWidgetsCountsPerNoOfClicks + widgetsCountPerNoOfClicks
    }
  }
}