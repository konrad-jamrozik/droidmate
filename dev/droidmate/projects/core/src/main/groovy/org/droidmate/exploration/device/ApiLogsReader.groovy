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
import org.droidmate.MonitorConstants
import org.droidmate.common.DroidmateException
import org.droidmate.common.logcat.ApiLogcatMessage
import org.droidmate.common.logcat.TimeFormattedLogcatMessage
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceNeedsRebootException
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * See {@link DeviceMessagesReader}
 */

@Slf4j
class ApiLogsReader implements IApiLogsReader
{

  private final IExplorableAndroidDevice device

  ApiLogsReader(IExplorableAndroidDevice device)
  {
    this.device = device
  }

  /**
   * <p>
   * The same remarks apply as for {@link org.droidmate.exploration.device.InitMsgsReader#monitorLogger}. Empirical observation
   * shows up to 5 seconds delay for API logs.
   *
   * </p>
   */
  private Logger monitorLogger = LoggerFactory.getLogger(LogbackConstants.logger_name_monitor)

  @Deprecated
  @Override
  List<IApiLogcatMessage> getCurrentApiLogsFromLogcat(IDeviceTimeDiff deviceTimeDiff) throws DeviceException
  {
    log.debug("getCurrentApiLogsFromLogcat(deviceTimeDiff)")
    assert deviceTimeDiff != null
    return readApiLogcatMessages(this.&getMessagesFromLogcat.curry(deviceTimeDiff))
  }

  @Override
  List<IApiLogcatMessage> getAndClearCurrentApiLogsFromMonitorTcpServer(IDeviceTimeDiff deviceTimeDiff) throws DeviceNeedsRebootException, DeviceException
  {
    log.debug("getAndClearCurrentApiLogsFromMonitorTcpServer(deviceTimeDiff)")
    assert deviceTimeDiff != null

    List<IApiLogcatMessage> logs = readApiLogcatMessages(this.&getAndClearMessagesFromMonitorTcpServer.curry(deviceTimeDiff))

    assert logs != null
    log.debug("apiLogs# ${logs.size()}")
    return logs
  }

  List<IApiLogcatMessage> readApiLogcatMessages(Closure<List<ITimeFormattedLogcatMessage>> messagesProvider) throws DeviceException
  {
    List<ITimeFormattedLogcatMessage> messages = messagesProvider.call()

    messages.each {monitorLogger.trace("${it.toLogcatMessageString()}")}

    List<IApiLogcatMessage> apiLogs
    try
    {
      apiLogs = messages.collect {ApiLogcatMessage.from(it) as IApiLogcatMessage }
    } catch (DroidmateException e)
    {
      throw new DeviceException("Failed to parse API call logs from one of the messages obtained from logcat.", e)
    }

    assert apiLogs.sortedByTimePerPID()

    assert apiLogs != null
    return apiLogs
  }

  @Deprecated
  List<ITimeFormattedLogcatMessage> getMessagesFromLogcat(IDeviceTimeDiff deviceTimeDiff) throws DeviceException
  {
    def messages = device.readLogcatMessages(MonitorConstants.tag_api)

    return deviceTimeDiff.syncMessages(messages)
  }

  private List<ITimeFormattedLogcatMessage> getAndClearMessagesFromMonitorTcpServer(IDeviceTimeDiff deviceTimeDiff) throws DeviceNeedsRebootException, DeviceException
  {
    List<List<String>> messages = device.readAndClearMonitorTcpMessages()

    return extractLogcatMessagesFromTcpMessages(messages, deviceTimeDiff)
  }

  private List<ITimeFormattedLogcatMessage> extractLogcatMessagesFromTcpMessages(List<List<String>> messages, IDeviceTimeDiff deviceTimeDiff) throws DeviceNeedsRebootException, DeviceException
  {
    return deviceTimeDiff.syncMessages(messages.collect {List<String> msg ->

      String pid = msg[0]

      LocalDateTime deviceTime = LocalDateTime.parse(msg[1],
        DateTimeFormatter.ofPattern(
          MonitorConstants.monitor_time_formatter_pattern,
          MonitorConstants.monitor_time_formatter_locale))

      String payload = msg[2]

      return TimeFormattedLogcatMessage.from(
        deviceTime,
        MonitorConstants.loglevel.toUpperCase(),
        "[Adapted]" + MonitorConstants.tag_api,
        pid,
        payload)
    })
  }
}
