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
  void readMonitorInitLogsAndClearLogcat() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    this.logs.monitorInitTime = device.readMonitorMessages()
    this.logs.instrumentationMsgs = device.readInstrumentationMessages()

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

    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs(/* allowAppToBeUnreachable */ false)

    assert successfullyRead(apiLogs)
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

  @Override
  void readAndClearApiLogsIfAny() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs(/* allowAppToBeUnreachable */ true)

    if (successfullyRead(apiLogs))
      addApiLogs(apiLogs)
  }

  @Override
  void assertNoApiLogsCanBeRead() throws DeviceException
  {
    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs(/* allowAppToBeUnreachable */ true)
    assert !successfullyRead(apiLogs)
  }


  private static final String uiThreadId = "1"

  @Override
  void readClearAndAssertOnlyBackgroundApiLogs() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs(/* allowAppToBeUnreachable */ false)
    assert successfullyRead(apiLogs)
    assert this.logs.apiLogs.every {it.threadId != uiThreadId}

    addApiLogs(apiLogs)
  }

  @Override
  void readClearAndAssertOnlyBackgroundApiLogsIfAny() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs(/* allowAppToBeUnreachable */ true)

    assert (successfullyRead(apiLogs)).implies(apiLogs.every {it.threadId != uiThreadId})

    if (successfullyRead(apiLogs))
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


  private boolean successfullyRead(List<IApiLogcatMessage> apiLogs)
  {
    return apiLogs != null
  }

  // KJA soon, this boolean param will be removed (it will be effectively always false)
  private List<IApiLogcatMessage> _readAndClearApiLogs(boolean allowAppToBeUnreachable) throws DeviceException
  {
    try
    {
      return device.getAndClearCurrentApiLogsFromMonitorTcpServer()
    } catch (TcpServerUnreachableException e)
    {
      if (allowAppToBeUnreachable)
      {
        log.debug("Caught ${TcpServerUnreachableException.simpleName} from " +
          "messagesReader.getAndClearCurrentApiLogsFromMonitorTcpServer(). " +
          "Because app is allowed to be unreachable: 1. ignoring the exception instead of rethrowing, 2. returning null api logs.")
        return null
      } else
      {
        log.warn("! Caught ${TcpServerUnreachableException.simpleName} from " +
          "messagesReader.getAndClearCurrentApiLogsFromMonitorTcpServer(). Rethrowing.")

        throw e
      }
    }
  }

}
