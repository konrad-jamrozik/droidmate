// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device

import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

public interface IExplorableAndroidDevice
{
  boolean hasPackageInstalled(String packageName) throws DeviceException

  IDeviceGuiSnapshot getGuiSnapshot() throws DeviceException

  void perform(IAndroidDeviceAction action) throws DeviceException

  List<ITimeFormattedLogcatMessage> readLogcatMessages(String messageTag) throws DeviceException

  List<ITimeFormattedLogcatMessage> waitForLogcatMessages(String messageTag, int minMessagesCount, int waitTimeout, int queryInterval) throws DeviceException

  void clearLogcat() throws DeviceException

  List<List<String>> readAndClearMonitorTcpMessages() throws TcpServerUnreachableException, DeviceException

  LocalDateTime getCurrentTime() throws TcpServerUnreachableException, DeviceException

}

