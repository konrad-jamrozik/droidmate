// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import groovy.util.logging.Slf4j
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.ForbiddenOperationError
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.lib_android.MonitorJavaTemplate
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.droidmate.common_android.Constants.instrumentation_redirectionTag
import static org.droidmate.common_android.Constants.uiaDaemon_logcatTag

@Slf4j
class DeviceLogsHandler implements IDeviceLogsHandler
{

  IDeviceWithReadableLogs device

  boolean readingSealed = false

  IDeviceLogs logs = new DeviceLogs()

  DeviceLogsHandler(IDeviceWithReadableLogs device)
  {
    this.device = device
  }

  @Override
  boolean monitorInitLogcatMessagesArePresent(List<ITimeFormattedLogcatMessage> outMessages) throws DeviceException
  {
    assert outMessages.empty

    outMessages.addAll(device.readLogcatMessages(MonitorJavaTemplate.tag_init))
    outMessages.addAll(device.readLogcatMessages(instrumentation_redirectionTag))

    if (!outMessages.empty)
      return true

    return false
  }

  @Override
  void readMonitorInitTimeAndClearLogcat() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    this.logs.monitorInitTime = device.readMonitorInitTime()

    this.clearLogcat()
  }


  @Override
  void clearLogcat() throws DeviceException
  {
    device.clearLogcat()
  }

  @Override
  void readAndClearApiLogs() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs()
    addApiLogs(apiLogs)
  }

  public void addApiLogs(List<IApiLogcatMessage> apiLogs)
  {
    assert apiLogs != null

    if (this.logs.apiLogs == null)
      this.logs.apiLogs = []

    if (!this.logs.apiLogs.empty && !apiLogs.empty)
      assert this.logs.apiLogs.last().time <= apiLogs.first().time

    this.logs.apiLogs.addAll(apiLogs)
  }

  private static final String uiThreadId = "1"

  @Override
  void readClearAndAssertOnlyBackgroundApiLogs() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs()
    assert this.logs.apiLogs.every {it.threadId != uiThreadId}

    addApiLogs(apiLogs)
  }

  private Logger uiadLogger = LoggerFactory.getLogger(LogbackConstants.logger_name_uiad)

  @Override
  void logUiaDaemonLogsFromLogcat() throws DeviceException
  {
    List<ITimeFormattedLogcatMessage> uiaDaemonLogs = device.readLogcatMessages(uiaDaemon_logcatTag)

    uiaDaemonLogs.each {
      if (it.level == "W")
        uiadLogger.warn("${it.messagePayload}")
      else
        uiadLogger.trace("${it.messagePayload}")
    }
  }

  @Override
  IDeviceLogs sealReadingAndReturnDeviceLogs()
  {
    if (this.readingSealed)
      throw new ForbiddenOperationError()

    this.readingSealed = true
    return this.logs
  }

  /**
   * This method fails early on known bugs:<br/>
   * KNOWN BUG See https://hg.st.cs.uni-saarland.de/issues/976
   */
  @Override
  void throwIfMonitorInitLogcatLogsArePresent() throws DeviceException
  {
    List<ITimeFormattedLogcatMessage> messages = []

    if (this.monitorInitLogcatMessagesArePresent(messages))
    {
      assert !messages.empty
      throw new DeviceException("Monitor init logcat messages are present, " +
        "while none have been expected. The messages:\n${messages.truncateAndPrint(3)}")
    }

    assert messages.empty
  }

  private List<IApiLogcatMessage> _readAndClearApiLogs() throws DeviceException
  {
    try
    {
      def logs = device.getAndClearCurrentApiLogsFromMonitorTcpServer()
      assert logs != null
      return logs

    } catch (TcpServerUnreachableException e)
    {
      log.warn("! Caught ${TcpServerUnreachableException.simpleName} from " +
        "messagesReader.getAndClearCurrentApiLogsFromMonitorTcpServer(). Rethrowing.")
      throw e
    }
  }

}
