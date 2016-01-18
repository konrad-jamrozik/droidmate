// Copyright (c) 2012-2016 Saarland University
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

    log.debug("8. Get GUI snapshot.")
    // GUI snapshot has to be obtained before a check is made if app is running. Why? Because obtaining GUI snapshot closes all
    // ANR dialogs, and if the app crashed with ANR, it will be deemed as running until the ANR is closed.
    this.snapshot = device.guiSnapshot

    log.debug("9. Try to read API logs.")
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    logsHandler.readAndClearApiLogs()
    this.logs = logsHandler.getLogs()
  }
}

