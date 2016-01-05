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

@Slf4j
class RunnableTerminateExplorationAction extends RunnableExplorationAction
{

  private static final long serialVersionUID = 1

  RunnableTerminateExplorationAction(TerminateExplorationAction action, LocalDateTime timestamp)
  {
    super(action, timestamp)
  }

  @Override
  protected void performDeviceActions(IApk app, IDeviceWithReadableLogs device) throws DeviceException
  {
    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)
    log.debug("1. Do asserts and throws using logs handler.")
    logsHandler.throwIfMonitorInitLogcatLogsArePresent()
    logsHandler.readClearAndAssertOnlyBackgroundApiLogs()

    log.debug("2. Seal logs reading.")
    this.logs = logsHandler.sealReadingAndReturnDeviceLogs()

    log.debug("3. Reset package ${app.packageName}}")
    device.clearPackage(app.packageName)

    log.debug("4. Do asserts and throws using logs handler.")
    // KJA to replace with org.droidmate.exploration.actions.RunnableResetAppExplorationAction.assertAppIsNotRunning
    logsHandler.throwIfMonitorInitLogcatLogsArePresent()
    logsHandler.assertNoApiLogsCanBeRead()

    log.debug("5. Get GUI snapshot, ensuring home screen is displayed.")
    this.snapshot = device.ensureHomeScreenIsDisplayed()

    log.debug("6. Log uia-daemon logs and clear logcat")
    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()
  }

}

