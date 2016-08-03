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

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.exploration.actions.EnterTextExplorationAction
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.actions.WidgetExplorationAction

val RunnableExplorationActionWithResult.clickedWidget: Set<Widget> get() {
  val action = this.action.base
  return when (action) {
    is WidgetExplorationAction -> setOf(action.widget)
    is EnterTextExplorationAction -> setOf(action.widget)
    else -> emptySet()
  }
}

fun RunnableExplorationActionWithResult.extractEventApiPairs(): List<EventApiPair> {
  return this.result.deviceLogs.apiLogsOrEmpty.map { apiLog -> EventApiPair(this, apiLog) }
}