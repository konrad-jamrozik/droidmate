// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.device

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.common.DroidmateException
import org.droidmate.common.logcat.ApiLogcatMessage
import org.droidmate.common.logcat.TimeFormattedLogcatMessage
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.lib_android.MonitorJavaTemplate
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * See {@link DeviceMessagesReader}
 */

@TypeChecked
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
  List<IApiLogcatMessage> getAndClearCurrentApiLogsFromMonitorTcpServer(IDeviceTimeDiff deviceTimeDiff) throws TcpServerUnreachableException, DeviceException
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

    List<ApiLogcatMessage> apiLogs
    try
    {
      apiLogs = messages.collect {ApiLogcatMessage.from(it)}
    } catch (DroidmateException e)
    {
      throw new DeviceException("Failed to parse API call logs from one of the messages obtained from logcat.", e)
    }

    assert apiLogs == apiLogs.collect().sort {it.time}

    assert apiLogs != null
    return apiLogs
  }

  @Deprecated
  List<ITimeFormattedLogcatMessage> getMessagesFromLogcat(IDeviceTimeDiff deviceTimeDiff) throws DeviceException
  {
    def messages = device.readLogcatMessages(MonitorJavaTemplate.tag_api)

    return deviceTimeDiff.syncMessages(messages)
  }

  List<ITimeFormattedLogcatMessage> getAndClearMessagesFromMonitorTcpServer(IDeviceTimeDiff deviceTimeDiff) throws TcpServerUnreachableException, DeviceException
  {
    List<List<String>> messages = device.readAndClearMonitorTcpMessages()

    return extractLogcatMessagesFromTcpMessages(messages, deviceTimeDiff)
  }

  private List<ITimeFormattedLogcatMessage> extractLogcatMessagesFromTcpMessages(List<List<String>> messages, IDeviceTimeDiff deviceTimeDiff)
  {
    return deviceTimeDiff.syncMessages(messages.collect {List<String> msg ->

      String pid = msg[0]

      LocalDateTime deviceTime = LocalDateTime.parse(msg[1],
        DateTimeFormatter.ofPattern(
          MonitorJavaTemplate.monitor_time_formatter_pattern,
          MonitorJavaTemplate.monitor_time_formatter_locale))

      String payload = msg[2]

      return TimeFormattedLogcatMessage.from(
        deviceTime,
        MonitorJavaTemplate.loglevel.toUpperCase(),
        "[Adapted]" + MonitorJavaTemplate.tag_api,
        pid,
        payload)
    })
  }
}
