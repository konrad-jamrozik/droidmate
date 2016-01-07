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
import org.droidmate.exploration.device.IDeviceWithReadableLogs

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
  protected void performDeviceActions(IApk app, IDeviceWithReadableLogs device) throws DeviceException
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
    // KJA2 this sometimes fails. Idea for fix: enumerate all ports, asking "what is your PID?" if server replies, kill the PID.
    // Continue the process until no server replies or number of attempts is exhausted.
    // KJA2 the same way I can obtain the package name of the process! Useful for identifying processes
    assertAppIsNotRunning(device, app)

    log.debug("6. Log uia-daemon logs and clear logcat")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()

    log.debug("7. Launch main activity")
    device.perform(newLaunchActivityDeviceAction(app.launchableActivityComponentName))

    log.debug("8. Log uia-daemon logs from logcat")
    logsHandler.logUiaDaemonLogsFromLogcat()

    log.debug("9. Read monitor init logs and clear logcat")
    assertAppIsRunning(device, app)
    logsHandler.readMonitorInitTimeAndClearLogcat()

    log.debug("10. Read and clear api logs, then seal reading")
    logsHandler.readAndClearApiLogs()
    this.logs = logsHandler.sealReadingAndReturnDeviceLogs()

    log.debug("11. Get GUI snapshot, then log uia-daemon logs, then clear logcat")
    this.snapshot = device.guiSnapshot

    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()
  }
}

