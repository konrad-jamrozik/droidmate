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
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.droidmate.common_android.Constants.uiaDaemon_logcatTag

@Slf4j
class DeviceLogsHandler implements IDeviceLogsHandler
{

  IRobustDevice device

  boolean readingSealed = false

  IDeviceLogs logs = new DeviceLogs()

  DeviceLogsHandler(IRobustDevice device)
  {
    this.device = device
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
  void readClearAndAssertOnlyBackgroundApiLogsIfAny() throws DeviceException
  {
    if (readingSealed)
      throw new ForbiddenOperationError()

    List<IApiLogcatMessage> apiLogs = _readAndClearApiLogs()
    assert this.logs.apiLogs.every {it.threadId != uiThreadId}

    addApiLogs(apiLogs)
  }

  private Logger uiadLogger = LoggerFactory.getLogger(LogbackConstants.logger_name_uiad)

  // KJA to remove
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

  private List<IApiLogcatMessage> _readAndClearApiLogs() throws DeviceException
  {
    def logs = this.device.getAndClearCurrentApiLogsFromMonitorTcpServer()
    assert logs != null
    return logs
  }

}
