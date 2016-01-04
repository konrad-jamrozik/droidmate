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

import static org.droidmate.device.datatypes.AndroidDeviceAction.*

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
    log.debug("1. Reset package ${app?.packageName}")

    assert app != null
    assert device != null

    device.perform(newResetPackageDeviceAction(app.packageName))

    device.ensureHomeScreenIsDisplayed()

    device.perform(newTurnWifiOnDeviceAction())

    // Get GUI snapshot to ensure device displays valid screen that is not "app has stopped" dialog box.
    device.getGuiSnapshot()

    IDeviceLogsHandler logsHandler = new DeviceLogsHandler(device)

    if ((base as ResetAppExplorationAction).isFirst)
    {
      assertAppIsNotRunning(logsHandler)
    } else
    {
      // In this case app might or might not be running.
      // It won't be running if before issuing this reset action its last activity crashed,
      // but it might still run if it has other components (activities, services) running.
      logsHandler.throwIfMonitorInitLogcatLogsArePresent()
      logsHandler.readClearAndAssertOnlyBackgroundApiLogsIfAny()
    }

    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()

    log.debug("2. Launch main activity")
    device.perform(newLaunchActivityDeviceAction(app.launchableActivityComponentName))

    log.debug("3. Log uia-daemon logs from logcat")
    logsHandler.logUiaDaemonLogsFromLogcat()

    log.debug("4. Read monitor init logs and clear logcat")
    logsHandler.readMonitorInitLogsAndClearLogcat()

    log.debug("5. Read and clear api logs, then seal reading")
    logsHandler.readAndClearApiLogs()

    this.logs = logsHandler.sealReadingAndReturnDeviceLogs()

    log.debug("6. Get GUI snapshot, then log uia-daemon logs, then clear logcat")
    this.snapshot = device.guiSnapshot

    logsHandler.logUiaDaemonLogsFromLogcat()
    logsHandler.clearLogcat()
  }



  private void assertAppIsNotRunning(IDeviceLogsHandler logsHandler)
  {
    logsHandler.throwIfMonitorInitLogcatLogsArePresent()
    logsHandler.assertNoApiLogsCanBeRead()
  }
}

