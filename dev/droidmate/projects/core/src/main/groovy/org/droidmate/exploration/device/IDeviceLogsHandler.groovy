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

interface IDeviceLogsHandler
{
  void readClearAndAssertOnlyBackgroundApiLogs() throws DeviceException

  void logUiaDaemonLogsFromLogcat() throws DeviceException

  void clearLogcat() throws DeviceException

  void readAndClearApiLogs() throws DeviceException

  IDeviceLogs sealReadingAndReturnDeviceLogs()
}
