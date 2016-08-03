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

import com.google.common.base.MoreObjects
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.MissingGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exploration.device.IDeviceLogs
import org.droidmate.exploration.device.MissingDeviceLogs

class ExplorationActionRunResult implements IExplorationActionRunResult
{

  private static final long serialVersionUID = 1

  final boolean            successful
  final String             exploredAppPackageName
  final IDeviceLogs        deviceLogs
  final IDeviceGuiSnapshot guiSnapshot
  final DeviceException    exception


  ExplorationActionRunResult(boolean successful, String exploredAppPackageName, IDeviceLogs deviceLogs, IDeviceGuiSnapshot guiSnapshot, DeviceException exception)
  {
    this.successful = successful
    this.exploredAppPackageName = exploredAppPackageName
    this.deviceLogs = deviceLogs
    this.guiSnapshot = guiSnapshot
    this.exception = exception

    assert exploredAppPackageName?.size() >= 1
    assert deviceLogs != null
    assert guiSnapshot != null
    assert exception != null

    assert successful.implies(!(this.deviceLogs instanceof MissingDeviceLogs))
    assert successful.implies(!(this.guiSnapshot instanceof MissingGuiSnapshot))
    assert successful == (this.exception instanceof DeviceExceptionMissing)
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
