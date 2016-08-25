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

import org.droidmate.apis.IApiLogcatMessage
import org.droidmate.device.datatypes.Widget
import org.droidmate.exploration.actions.ResetAppExplorationAction
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

val IApkExplorationOutput2.uniqueActionableWidgets: Set<Widget>
  get() = this.actRess.setByUniqueString(
    extractItems = RunnableExplorationActionWithResult::actionableWidgets,
    uniqueString = Widget::uniqueString
  )

val IApkExplorationOutput2.uniqueClickedWidgets: Set<Widget>
  get() = this.actRess.setByUniqueString(
    extractItems = RunnableExplorationActionWithResult::clickedWidget,
    uniqueString = Widget::uniqueString
  )

val IApkExplorationOutput2.uniqueApis: Set<IApiLogcatMessage>
  get() = this.actRess.setByUniqueString(
    extractItems = { it.result.deviceLogs.apiLogsOrEmpty },
    uniqueString = { it.uniqueString } 
  )

val IApkExplorationOutput2.uniqueEventApiPairs: Set<EventApiPair>
  get() = this.actRess.setByUniqueString(
    extractItems = RunnableExplorationActionWithResult::extractEventApiPairs,
    uniqueString = { it.uniqueString }
  )

val IApkExplorationOutput2.resetActionsCount: Int
  get() = actions.count { it.base is ResetAppExplorationAction }

val IApkExplorationOutput2.fileNamePrefix: String
  get() = apk.fileName.replace(".", "_")