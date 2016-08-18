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

import org.droidmate.apis.IApiLogcatMessage
import org.droidmate.apis.ITimeFormattedLogcatMessage
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceNeedsRebootException

import java.time.LocalDateTime

/**
 * <p>
 * This class is responsible for reading messages from the device. It can read messages from the device logcat or from the
 * monitor TCP server (for the server source code, see {@code org.droidmate.monitor.MonitorJavaTemplate.MonitorTCPServer}).
 *
 * </p><p>
 * The messages read are either monitor init messages coming from logcat, method instrumentation messages coming from logcat, or
 * monitored API logs coming from monitor TCP server. In addition, this class maintains the time difference between the device
 * and the host machine, to sync the time logs from the device's clock with the host machine's clock.
 * </p>
 */
class DeviceMessagesReader implements IDeviceMessagesReader
{
  @Deprecated
  private final IInitMsgsReader initMsgsReader
  private final IApiLogsReader  apiLogsReader
  private final IDeviceTimeDiff deviceTimeDiff

  DeviceMessagesReader(IExplorableAndroidDevice device, int monitorServerStartTimeout, int monitorServerStartQueryDelay)
  {
    this.initMsgsReader = new InitMsgsReader(device, monitorServerStartTimeout, monitorServerStartQueryDelay)
    this.apiLogsReader = new ApiLogsReader(device)
    this.deviceTimeDiff = new DeviceTimeDiff(device)
  }

  @Override
  void resetTimeSync()
  {
    this.deviceTimeDiff.reset()

  }

  // Used by old exploration code
  @Deprecated
  @Override
  LocalDateTime readMonitorMessages() throws DeviceException
  {
    return initMsgsReader.readMonitorMessages(deviceTimeDiff)
  }

  // Used by old exploration code
  @Deprecated
  @Override
  List<ITimeFormattedLogcatMessage> readInstrumentationMessages() throws DeviceException
  {
    return initMsgsReader.readInstrumentationMessages(deviceTimeDiff)
  }

  @Override
  List<IApiLogcatMessage> getAndClearCurrentApiLogsFromMonitorTcpServer() throws DeviceNeedsRebootException, DeviceException
  {
    return apiLogsReader.getAndClearCurrentApiLogsFromMonitorTcpServer(deviceTimeDiff)
  }
}
