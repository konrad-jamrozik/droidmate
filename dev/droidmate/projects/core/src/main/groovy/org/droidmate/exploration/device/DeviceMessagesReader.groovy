// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

/**
 * <p>
 * This class is responsible for reading messages from the device. It can read messages from the device logcat or from the
 * monitor TCP server (for the server source code, see {@code org.droidmate.lib_android.MonitorJavaTemplate.MonitorTCPServer}).
 *
 * </p><p>
 * The messages read are either monitor init messages coming from logcat, method instrumentation messages coming from logcat, or
 * monitored API logs coming from monitor TCP server. In addition, this class maintains the time difference between the device
 * and the host machine, to sync the time logs from the device's clock with the host machine's clock.
 * </p>
 */
class DeviceMessagesReader implements IDeviceMessagesReader
{

  private final IInitMsgsReader initMsgsReader
  private final IApiLogsReader  apiLogsReader
  private final IDeviceTimeDiff deviceTimeDiff


  DeviceMessagesReader(IExplorableAndroidDevice device, int monitorServerStartTimeout, int monitorServerStartQueryInterval)
  {
    this.initMsgsReader = new InitMsgsReader(device, monitorServerStartTimeout, monitorServerStartQueryInterval)
    this.apiLogsReader = new ApiLogsReader(device)
    this.deviceTimeDiff = new DeviceTimeDiff(device)
  }

  @Override
  LocalDateTime readMonitorInitTime() throws DeviceException
  {
    // KJA
    return initMsgsReader.readMonitorMessages(deviceTimeDiff)
  }


  @Override
  LocalDateTime readMonitorMessages() throws DeviceException
  {
    return initMsgsReader.readMonitorMessages(deviceTimeDiff)
  }

  @Override
  List<ITimeFormattedLogcatMessage> readInstrumentationMessages() throws DeviceException
  {
    return initMsgsReader.readInstrumentationMessages(deviceTimeDiff)
  }

  /**
   * Deprecated and unused. It is superseded by {@link #getAndClearCurrentApiLogsFromMonitorTcpServer()}.
   * Left here for reference and in case if a rollback from the monitor-TCP-based infrastructure of message receipt
   * from the device will be necessary.
   */
  @Deprecated
  @Override
  List<IApiLogcatMessage> getCurrentApiLogsFromLogcat() throws DeviceException
  {
    return apiLogsReader.getCurrentApiLogsFromLogcat(deviceTimeDiff)
  }

  @Override
  List<IApiLogcatMessage> getAndClearCurrentApiLogsFromMonitorTcpServer() throws TcpServerUnreachableException, DeviceException
  {
    return apiLogsReader.getAndClearCurrentApiLogsFromMonitorTcpServer(deviceTimeDiff)
  }
}
