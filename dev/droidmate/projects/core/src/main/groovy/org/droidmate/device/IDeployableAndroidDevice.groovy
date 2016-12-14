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

import java.nio.file.Path

interface IDeployableAndroidDevice
{
  void pushJar(Path jar) throws DeviceException

  void pushJar(Path jar, String targetFileName) throws DeviceException

  void removeJar(Path jar) throws DeviceException

  void installApk(Path apk) throws DeviceException

  void installApk(IApk apk) throws DeviceException

  void uninstallApk(String apkPackageName, boolean ignoreFailure) throws DeviceException

  void closeMonitorServers() throws DeviceException

  void clearPackage(String apkPackageName) throws DeviceException

  boolean appProcessIsRunning(String appPackageName) throws DeviceException

  void clearLogcat() throws DeviceException

  void closeConnection() throws DeviceException

  void reboot() throws DeviceException

  void stopUiaDaemon(boolean uiaDaemonThreadIsNull) throws DeviceException

  boolean isAvailable() throws DeviceException

  boolean uiaDaemonClientThreadIsAlive()

  void restartUiaDaemon(boolean uiaDaemonThreadIsNull)
  
  void startUiaDaemon()

  void removeLogcatLogFile() throws DeviceException

  void pullLogcatLogFile() throws DeviceException

  void reinstallUiautomatorDaemon() throws DeviceException

  void pushMonitorJar() throws DeviceException

  void setupConnection() throws DeviceException

  void initModel() throws DeviceException
  
  void reconnectAdb() throws DeviceException
  
  void executeAdbCommand(String command, String successfulOutput, String commandDescription) throws DeviceException

  boolean uiaDaemonIsRunning()

  boolean isPackageInstalled(String packageName)

}
