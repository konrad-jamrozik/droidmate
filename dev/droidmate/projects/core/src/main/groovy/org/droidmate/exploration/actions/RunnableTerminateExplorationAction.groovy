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
import org.droidmate.android_sdk.DeviceException
import org.droidmate.android_sdk.IApk
import org.droidmate.exploration.device.DeviceLogsHandler
import org.droidmate.exploration.device.IDeviceLogsHandler
import org.droidmate.exploration.device.IRobustDevice

import java.time.LocalDateTime

@Slf4j
class RunnableTerminateExplorationAction extends RunnableExplorationAction
{

  private static final long serialVersionUID = 1

  RunnableTerminateExplorationAction(TerminateExplorationAction action, LocalDateTime timestamp)
  {
    super(action, timestamp)
  }

  @Override
  protected void performDeviceActions(IApk app, IRobustDevice device) throws DeviceException
  {
    log.debug("1. Read background API logs, if any.")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.readClearAndAssertOnlyBackgroundApiLogsIfAny()
    this.logs = logsHandler.getLogs()
    
    log.debug("2. Take a screenshot.")
    device.takeScreenshot(app, "terminate")
    
    log.debug("3. Close monitor servers, if any.")
    device.closeMonitorServers()

    log.debug("4. Clear package ${app.packageName}}.")
    device.clearPackage(app.packageName)

    log.debug("5. Assert app is not running.")
    assertAppIsNotRunning(device, app)

    log.debug("6. Ensure home screen is displayed.")
    this.snapshot = device.ensureHomeScreenIsDisplayed()

  }

}

