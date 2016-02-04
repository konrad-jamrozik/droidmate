// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.actions

import com.google.common.base.MoreObjects
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.MissingGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exceptions.ForbiddenOperationError
import org.droidmate.exploration.device.IDeviceLogs
import org.droidmate.exploration.device.MissingDeviceLogs

class ExplorationActionRunResult implements IExplorationActionRunResult
{

  private static final long serialVersionUID = 1

  final boolean            successful
  final IDeviceLogs        deviceLogs
  final IDeviceGuiSnapshot guiSnapshot
  final DeviceException    exception


  ExplorationActionRunResult(boolean successful, IDeviceLogs deviceLogs, IDeviceGuiSnapshot guiSnapshot, DeviceException exception)
  {
    this.successful = successful
    this.deviceLogs = deviceLogs
    this.guiSnapshot = guiSnapshot
    this.exception = exception

    assert deviceLogs != null
    assert guiSnapshot != null
    assert exception != null

    assert successful.implies(!(this.deviceLogs instanceof MissingDeviceLogs))
    assert successful.implies(!(this.guiSnapshot instanceof MissingGuiSnapshot))
    assert successful == (this.exception instanceof DeviceExceptionMissing)
  }


  @Override
  DeviceException getException() throws ForbiddenOperationError
  {
    if (successful)
      throw new ForbiddenOperationError()

    return exception
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
      .add("successful", successful)
      .add("snapshot", guiSnapshot)
      .addValue(deviceLogs)
      .add("exception", exception)
      .toString()
  }
}
