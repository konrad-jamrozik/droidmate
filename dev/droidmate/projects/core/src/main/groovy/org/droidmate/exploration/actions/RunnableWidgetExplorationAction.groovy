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
package org.droidmate.exploration.actions

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.device.DeviceLogsHandler
import org.droidmate.exploration.device.IDeviceLogsHandler
import org.droidmate.exploration.device.IRobustDevice

import java.time.LocalDateTime

import static org.droidmate.device.datatypes.AndroidDeviceAction.newClickGuiDeviceAction

@Slf4j
class RunnableWidgetExplorationAction extends RunnableExplorationAction
{

  private static final long serialVersionUID = 1

  private final WidgetExplorationAction action

  RunnableWidgetExplorationAction(WidgetExplorationAction action, LocalDateTime timestamp)
  {
    super(action, timestamp)
    this.action = action
  }

  protected void performDeviceActions(IApk app, IRobustDevice device) throws DeviceException
  {
    log.debug("1. Assert only background API logs are present, if any.")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.readClearAndAssertOnlyBackgroundApiLogsIfAny()

    log.debug("2. Perform widget click: ${action}.")
    device.perform(newClickGuiDeviceAction(action.widget, action.longClick))

    log.debug("3. Read and clear API logs if any, then seal logs reading.")
    logsHandler.readAndClearApiLogs()
    this.logs = logsHandler.getLogs()

    log.debug("4. Get GUI snapshot.")
    this.snapshot = device.guiSnapshot
  }

}

