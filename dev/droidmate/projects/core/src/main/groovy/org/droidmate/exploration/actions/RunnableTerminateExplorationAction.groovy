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
    
    log.debug("2. Close monitor servers, if any.")
    device.closeMonitorServers()

    log.debug("3. Clear package ${app.packageName}}.")
    device.clearPackage(app.packageName)

    log.debug("4. Assert app is not running.")
    assertAppIsNotRunning(device, app)

    log.debug("5. Ensure home screen is displayed.")
    this.snapshot = device.ensureHomeScreenIsDisplayed()

  }

}

