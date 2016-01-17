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

    log.debug("2. Perform widget click: ${action}")
    device.perform(newClickGuiDeviceAction(action.widget, action.longClick))

    log.debug("3. Read and clear API logs if any, then seal logs reading")
    logsHandler.readAndClearApiLogs()
    this.logs = logsHandler.getLogs()

    log.debug("4. Get GUI snapshot")
    // KNOWN BUG reset / exhaust attempts to get valid GUI snapshot
    this.snapshot = device.guiSnapshot
  }

}

