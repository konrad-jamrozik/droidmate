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
    assert this.logs.apiLogs.every {it.threadId != uiThreadId}

    addApiLogs(apiLogs)
  }

  private void addApiLogs(List<IApiLogcatMessage> apiLogs)
  {
    assert apiLogs != null

    if (this.logs.apiLogs == null)
      this.logs.apiLogs = []

    if (!this.logs.apiLogs.empty && !apiLogs.empty)
      assert this.logs.apiLogs.last().time <= apiLogs.first().time

    this.logs.apiLogs.addAll(apiLogs)
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
