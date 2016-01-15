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
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.device.DeviceLogsHandler
import org.droidmate.exploration.device.IDeviceLogsHandler
import org.droidmate.exploration.device.IRobustDevice

import java.time.LocalDateTime

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
    // KJA2 assert fail here
    assertAppIsNotRunning(device, app)

    log.debug("6. Log uia-daemon logs and clear logcat")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()

    log.debug("7. Launch main activity")
    // Launch result is ignored because practice shows that the success of launching main activity cannot be used to determine
    // if app is running or not.
    device.launchMainActivity(app.launchableActivityComponentName)

    log.debug("8. Get GUI snapshot")
    // GUI snapshot has to be obtained before a check is made if app is running. Why? Because obtaining GUI snapshot closes all
    // ANR dialogs, and if the app crashed with ANR, it will be deemed as running until the ANR is closed.
    this.snapshot = device.guiSnapshot

    log.debug("9. Try to read API logs.")
    logsHandler.readAndClearApiLogs()

    log.debug("10. Log uia-daemon logs, clear logcat and seal reading")
    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()
    this.logs = logsHandler.sealReadingAndReturnDeviceLogs()
  }
}

