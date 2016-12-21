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

import static org.droidmate.device.datatypes.AndroidDeviceAction.newTurnWifiOnDeviceAction

@Slf4j
class RunnableResetAppExplorationAction extends RunnableExplorationAction
{
  private static final long serialVersionUID = 1

  private final boolean isFirst

  RunnableResetAppExplorationAction(ResetAppExplorationAction action, LocalDateTime timestamp)
  {
    super(action, timestamp)
    this.isFirst = action.isFirst
  }

  @Override
  protected void performDeviceActions(IApk app, IRobustDevice device) throws DeviceException
  {
    log.debug("1. Clear package ${app?.packageName}.")

    assert app != null
    assert device != null

    device.clearPackage(app.packageName)

    log.debug("2. Clear logcat.")
    // This is made to clean up the logcat if previous app exploration failed. If the clean would not be made, it might be
    // possible some API logs will be read from it, wreaking all kinds of havoc, e.g. having timestamp < than the current
    // exploration start time.
    device.clearLogcat()
    
    log.debug("3. Ensure home screen is displayed.")
    device.ensureHomeScreenIsDisplayed()

    log.debug("4. Turn wifi on.")
    device.perform(newTurnWifiOnDeviceAction())

    log.debug("5. Get GUI snapshot to ensure device displays valid screen that is not \"app has stopped\" dialog box.")
    device.getGuiSnapshot()

    log.debug("6. Ensure app is not running.")
    if (device.appIsRunning(app.packageName))
    {
      log.trace("App is still running. Clearing package again.")
      device.clearPackage(app.packageName)
    }

    log.debug("7. Launch app $app.packageName.")
    device.launchApp(app)

    if (this.isFirst)
    {
      log.debug("7.firstReset: Take a screenshot of first reset action.")
      device.takeScreenshot(app, "firstReset")
    }

    log.debug("8. Get GUI snapshot.")
    this.snapshot = device.guiSnapshot

    log.debug("9. Try to read API logs.")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.readAndClearApiLogs()
    this.logs = logsHandler.getLogs()

  }
}

