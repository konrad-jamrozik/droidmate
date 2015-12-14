// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.actions

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.MissingGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.device.IDeviceLogs
import org.droidmate.exploration.device.IDeviceLogsHandler
import org.droidmate.exploration.device.IDeviceWithReadableLogs
import org.droidmate.exploration.device.MissingDeviceLogs
import org.droidmate.logcat.ITimeFormattedLogcatMessage

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
        // KJA bug #995 here (snapchat special case: enter text)
        throw new UnexpectedIfElseFallthroughError("Unhandled ExplorationAction class. The class: ${action.class}")
    }
  }

  protected IDeviceGuiSnapshot snapshot
  protected IDeviceLogs        logs
  protected DeviceException    exception

  public IExplorationActionRunResult run(IApk app, IDeviceWithReadableLogs device)
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
      log.debug("! Caught ${e.class.simpleName} while performing device actions of ${this.class.simpleName}. " +
        "Returning failed ${ExplorationActionRunResult.class.simpleName} with the exception assigned to a field.")
    }

    // For post-conditions, see the constructor.
    return new ExplorationActionRunResult(successful, this.logs, this.snapshot, this.exception)
  }

  abstract protected void performDeviceActions(IApk app, IDeviceWithReadableLogs device) throws DeviceException

  @Override
  public String toString()
  {
    return "Runnable " + base.toString()
  }
}

