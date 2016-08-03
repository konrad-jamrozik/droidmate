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
    ExplorationActionRunResult result = new ExplorationActionRunResult(successful, app.packageName, this.logs, this.snapshot, this.exception)

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

  protected void assertAppIsNotRunning(IRobustDevice device, IApk apk) throws DeviceException
  {
    assert device.appIsNotRunning(apk)
  }

  @Override
  public String toString()
  {
    return "Runnable " + base.toString()
  }
}

