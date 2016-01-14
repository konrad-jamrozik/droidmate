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
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.logcat.IApiLogcatMessage

interface IApiLogsReader
{

  @Deprecated
  List<IApiLogcatMessage> getCurrentApiLogsFromLogcat(IDeviceTimeDiff deviceTimeDiff) throws DeviceException

  List<IApiLogcatMessage> getAndClearCurrentApiLogsFromMonitorTcpServer(IDeviceTimeDiff deviceTimeDiff) throws TcpServerUnreachableException, DeviceException
}
