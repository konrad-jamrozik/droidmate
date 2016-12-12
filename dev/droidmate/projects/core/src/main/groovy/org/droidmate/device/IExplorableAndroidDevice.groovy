// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

package org.droidmate.device

import org.droidmate.android_sdk.DeviceException
import org.droidmate.android_sdk.IApk
import org.droidmate.apis.ITimeFormattedLogcatMessage
import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot

import java.time.LocalDateTime

interface IExplorableAndroidDevice
{
  boolean hasPackageInstalled(String packageName) throws DeviceException

  IDeviceGuiSnapshot getGuiSnapshot() throws DeviceException

  void perform(IAndroidDeviceAction action) throws DeviceException

  List<ITimeFormattedLogcatMessage> readLogcatMessages(String messageTag) throws DeviceException

  List<ITimeFormattedLogcatMessage> waitForLogcatMessages(String messageTag, int minMessagesCount, int waitTimeout, int queryDelay) throws DeviceException

  void clearLogcat() throws DeviceException

  List<List<String>> readAndClearMonitorTcpMessages() throws DeviceException

  LocalDateTime getCurrentTime() throws DeviceException

  Boolean anyMonitorIsReachable() throws DeviceException

  void launchMainActivity(String launchableActivityComponentName) throws DeviceException

  Boolean appIsRunning(String appPackageName) throws DeviceException

  void clickAppIcon(String iconLabel) throws DeviceException
  
  void takeScreenshot(IApk app, String suffix) throws DeviceException
}

