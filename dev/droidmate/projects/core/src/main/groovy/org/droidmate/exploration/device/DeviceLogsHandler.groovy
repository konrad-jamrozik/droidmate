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
package org.droidmate.exploration.device

import groovy.util.logging.Slf4j
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.ForbiddenOperationError
import org.droidmate.logcat.IApiLogcatMessage

@Slf4j
class DeviceLogsHandler implements IDeviceLogsHandler
{

  IRobustDevice device

  IDeviceLogs logs = new DeviceLogs()

  DeviceLogsHandler(IRobustDevice device)
  {
    this.device = device
  }

  @Override
  void readAndClearApiLogs() throws DeviceException
  {
    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs()
    addApiLogs(apiLogs)
  }

  private static final String uiThreadId = "1"

  @Override
  void readClearAndAssertOnlyBackgroundApiLogsIfAny() throws DeviceException
  {
    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs()
    assert this.logs.apiLogsOrNull.every {it.threadId != uiThreadId}

    addApiLogs(apiLogs)
  }

  private void addApiLogs(List<IApiLogcatMessage> apiLogs)
  {
    assert apiLogs != null

    if (this.logs.apiLogsOrNull == null)
      this.logs.apiLogs = []

    if (!this.logs.apiLogsOrNull.empty && !apiLogs.empty)
      assert this.logs.apiLogsOrNull.last().time <= apiLogs.first().time

    this.logs.apiLogsOrNull.addAll(apiLogs)
  }

  boolean gotLogs = false
  @Override
  IDeviceLogs getLogs()
  {
    if (gotLogs)
      throw new ForbiddenOperationError()
    this.gotLogs = true
    return this.logs
  }

  private List<IApiLogcatMessage> _readAndClearApiLogs() throws DeviceException
  {
    def logs = this.device.getAndClearCurrentApiLogsFromMonitorTcpServer()
    assert logs != null
    return logs
  }

}
