// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device

import org.droidmate.common.Boolean3
import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceNeedsRebootException
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

public interface IExplorableAndroidDevice
{
  boolean hasPackageInstalled(String packageName) throws DeviceException

  IDeviceGuiSnapshot getGuiSnapshot() throws DeviceNeedsRebootException, DeviceException

  void perform(IAndroidDeviceAction action) throws DeviceNeedsRebootException, DeviceException

  List<ITimeFormattedLogcatMessage> readLogcatMessages(String messageTag) throws DeviceException

  List<ITimeFormattedLogcatMessage> waitForLogcatMessages(String messageTag, int minMessagesCount, int waitTimeout, int queryDelay) throws DeviceException

  void clearLogcat() throws DeviceException

  List<List<String>> readAndClearMonitorTcpMessages() throws DeviceNeedsRebootException, DeviceException

  LocalDateTime getCurrentTime() throws DeviceNeedsRebootException, DeviceException

  Boolean anyMonitorIsReachable() throws DeviceNeedsRebootException, DeviceException

  Boolean3 launchMainActivity(String launchableActivityComponentName) throws DeviceException

  Boolean appIsRunning(String appPackageName) throws DeviceNeedsRebootException, DeviceException
}

