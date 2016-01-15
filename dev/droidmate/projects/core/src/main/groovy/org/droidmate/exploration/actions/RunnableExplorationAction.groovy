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
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.MissingGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.device.IDeviceLogs
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.exploration.device.MissingDeviceLogs
import org.droidmate.logcat.IApiLogcatMessage

import java.time.LocalDateTime

@Slf4j
abstract class RunnableExplorationAction implements IRunnableExplorationAction
{

  private static final long serialVersionUID = 1


  @Delegate
  ExplorationAction base

  LocalDateTime timestamp


  RunnableExplorationAction(ExplorationAction base, LocalDateTime timestamp)
  {
    this.base = base
    this.timestamp = timestamp
  }

  static RunnableExplorationAction from(ExplorationAction action, LocalDateTime timestamp)
  {
    switch (action.class)
    {
      case ResetAppExplorationAction:
        return new RunnableResetAppExplorationAction(action as ResetAppExplorationAction, timestamp)
        break

      case WidgetExplorationAction:
        return new RunnableWidgetExplorationAction(action as WidgetExplorationAction, timestamp)

      case TerminateExplorationAction:
        return new RunnableTerminateExplorationAction(action as TerminateExplorationAction, timestamp)

      default:
        throw new UnexpectedIfElseFallthroughError("Unhandled ExplorationAction class. The class: ${action.class}")
    }
  }

  protected IDeviceGuiSnapshot snapshot
  protected IDeviceLogs        logs
  protected DeviceException    exception

  public IExplorationActionRunResult run(IApk app, IRobustDevice device)
  {
    assert app != null
    assert device != null

    boolean successful = true

    // @formatter:off
    this.logs      = new MissingDeviceLogs()
    this.snapshot  = new MissingGuiSnapshot()
    this.exception = new DeviceExceptionMissing()
    // @formatter:on

    try
    {
      log.trace("${this.class.simpleName}.performDeviceActions(app=${app.fileName}, device)")
      this.performDeviceActions(app, device)
      log.trace("${this.class.simpleName}.performDeviceActions(app=${app.fileName}, device) - DONE")
    } catch (DeviceException e)
    {
      successful = false
      this.exception = e
      log.warn("! Caught ${e.class.simpleName} while performing device actions of ${this.class.simpleName}. " +
        "Returning failed ${ExplorationActionRunResult.class.simpleName} with the exception assigned to a field.")
    }

    // For post-conditions, see inside the constructor call made line below.
    ExplorationActionRunResult result = new ExplorationActionRunResult(successful, this.logs, this.snapshot, this.exception)
    frontendHook(result)
    return result
  }

  /**
   * Allows to hook into the result of interacting with the device after an ExplorationAction has been executed on it.
   */
  void frontendHook(IExplorationActionRunResult result)
  {
    if (!(result.guiSnapshot instanceof MissingGuiSnapshot))
    {
      List<Widget> widgets = result.guiSnapshot.guiState.widgets
      boolean isANR = result.guiSnapshot.guiState.isAppHasStoppedDialogBox()
      // And so on. see org.droidmate.device.datatypes.IGuiState
    }

    if (!(result.deviceLogs instanceof MissingDeviceLogs))
    {
      List<IApiLogcatMessage> logs = result.deviceLogs.apiLogsOrEmpty
      logs.each { IApiLogcatMessage log ->
        String time = log.time
        String methodName = log.methodName
        // And so on. See org.droidmate.logcat.ITimeFormattedLogcatMessage
        // and org.droidmate.apis.IApi
      }
    }

    if (!(result.successful))
    {
      DeviceException exception = result.exception
    }

    // To-do for SE team
  }

  abstract protected void performDeviceActions(IApk app, IRobustDevice device) throws DeviceException

  protected void assertAppIsNotRunning(IRobustDevice device, IApk apk)
  {
    assert device.appIsNotRunning(apk)
  }

  protected void assertAppIsRunning(IRobustDevice device, IApk apk)
  {
    assert device.appIsRunning(apk)
  }


  protected Boolean appIsRunning(IRobustDevice device, IApk app)
  {
    device.appProcessIsRunning(app.packageName) && device.anyMonitorIsReachable()
  }

  @Override
  public String toString()
  {
    return "Runnable " + base.toString()
  }
}

