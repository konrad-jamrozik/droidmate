// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.actions

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.common.Boolean3
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.device.DeviceLogsHandler
import org.droidmate.exploration.device.IDeviceLogsHandler
import org.droidmate.exploration.device.IRobustDevice

import java.time.LocalDateTime

import static org.droidmate.device.datatypes.AndroidDeviceAction.newLaunchActivityDeviceAction
import static org.droidmate.device.datatypes.AndroidDeviceAction.newTurnWifiOnDeviceAction

@Slf4j
class RunnableResetAppExplorationAction extends RunnableExplorationAction
{

  private static final long serialVersionUID = 1

  RunnableResetAppExplorationAction(ResetAppExplorationAction action, LocalDateTime timestamp)
  {
    super(action, timestamp)
  }

  @Override
  protected void performDeviceActions(IApk app, IRobustDevice device) throws DeviceException
  {
    log.debug("1. Clear package ${app?.packageName}")

    assert app != null
    assert device != null

    device.clearPackage(app.packageName)

    log.debug("2. ensure home screen is displayed")

    device.ensureHomeScreenIsDisplayed()

    log.debug("3. turn wifi on")

    device.perform(newTurnWifiOnDeviceAction())

    log.debug("4. get GUI snapshot to ensure device displays valid screen that is not \"app has stopped\" dialog box.")
    device.getGuiSnapshot()

    log.debug("5. Assert app is not running.")
    assertAppIsNotRunning(device, app)

    log.debug("6. Log uia-daemon logs and clear logcat")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()

    log.debug("7. Launch main activity")
    Boolean3 launchResult = device.launchMainActivity(app.launchableActivityComponentName)

    log.debug("8. Get GUI snapshot")
    // GUI snapshot has to be obtained before a check is made if app is running. Why? Because obtaining GUI snapshot closes all
    // ANR dialogs, and if the app crashed with ANR, it will be deemed as running until the ANR is closed.
    this.snapshot = device.guiSnapshot

    if (launchResult == Boolean3.True)
    {
      log.debug("9. [Launch successful] Assert app is running and read API logs.")
      assertAppIsRunning(device, app)
      logsHandler.readAndClearApiLogs()

    } else if (launchResult == Boolean3.False){
      log.debug("9. [Launch failed] Assert app is not running. Skip reading API logs.")
      assertAppIsNotRunning(device, app)

    } else if (launchResult == Boolean3.Unknown)
    {
      log.debug("9. [Launch result unknown] Try to read API logs.")
      logsHandler.readAndClearApiLogs()
    }

    log.debug("10. Log uia-daemon logs, clear logcat and seal reading")
    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()
    this.logs = logsHandler.sealReadingAndReturnDeviceLogs()
  }
}

