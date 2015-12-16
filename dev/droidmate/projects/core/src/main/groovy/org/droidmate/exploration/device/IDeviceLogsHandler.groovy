// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import org.droidmate.exceptions.DeviceException
import org.droidmate.logcat.ITimeFormattedLogcatMessage

interface IDeviceLogsHandler
{

  boolean monitorInitLogcatMessagesArePresent(List<ITimeFormattedLogcatMessage> outMessages) throws DeviceException

  void assertNoApiLogsCanBeRead() throws DeviceException

  void readClearAndAssertOnlyBackgroundApiLogs() throws DeviceException

  void readClearAndAssertOnlyBackgroundApiLogsIfAny() throws DeviceException

  void logUiaDaemonLogsFromLogcat() throws DeviceException

  void clearLogcat() throws DeviceException

  void readMonitorInitLogsAndClearLogcat() throws DeviceException

  void readAndClearApiLogs() throws DeviceException

  void readAndClearApiLogsIfAny() throws DeviceException

  IDeviceLogs sealReadingAndReturnDeviceLogs()

  void throwIfMonitorInitLogcatLogsArePresent() throws DeviceException
}
