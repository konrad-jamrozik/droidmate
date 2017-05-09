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

package org.droidmate.android_sdk

import java.nio.file.Path

interface IAdbWrapper {

  void startAdbServer() throws AdbWrapperException

  void killAdbServer() throws AdbWrapperException

  List<AndroidDeviceDescriptor> getAndroidDevicesDescriptors() throws AdbWrapperException

  List<String> waitForMessagesOnLogcat(String deviceSerialNumber, String messageTag, int minMessagesCount, int waitTimeout, int queryDelay)
    throws AdbWrapperException

  void forwardPort(String deviceSerialNumber, int port) throws AdbWrapperException

  void reverseForwardPort(String deviceSerialNumber, int port) throws AdbWrapperException

  void pushJar(String deviceSerialNumber, Path jarFile) throws AdbWrapperException
  void pushJar(String deviceSerialNumber, Path jarFile, String targetFileName) throws AdbWrapperException

  void removeJar(String deviceSerialNumber, Path  jarFile) throws AdbWrapperException

  void installApk(String deviceSerialNumber, Path apkToInstall) throws AdbWrapperException

  void installApk(String deviceSerialNumber, IApk apkToInstall) throws AdbWrapperException

  void uninstallApk(String deviceSerialNumber, String apkPackageName, boolean ignoreFailure) throws AdbWrapperException

  void launchMainActivity(String deviceSerialNumber, String launchableActivityName) throws AdbWrapperException

  void clearLogcat(String deviceSerialNumber) throws AdbWrapperException

  boolean clearPackage(String deviceSerialNumber, String apkPackageName)

  List<String> readMessagesFromLogcat(String deviceSerialNumber, String messagesTag)

  String listPackages(String deviceSerialNumber) throws AdbWrapperException

  String listPackage(String deviceSerialNumber, String packageName) throws AdbWrapperException

  String ps(String deviceSerialNumber) throws AdbWrapperException

  void reboot(String deviceSerialNumber) throws AdbWrapperException

  void startUiautomatorDaemon(String deviceSerialNumber, int port) throws AdbWrapperException

  //void stopUiautomatorDaemon(String deviceSerialNumber) throws AdbWrapperException

  void removeFile_api23(String deviceSerialNumber, String fileName, String shellPackageName) throws AdbWrapperException

  void pullFile_api23(String deviceSerialNumber, String pulledFileName, String destinationFilePath, String shellPackageName) throws AdbWrapperException
  
  void takeScreenshot(String deviceSerialNumber, String targetPath) throws AdbWrapperException

  String executeCommand(String deviceSerialNumber, String successfulOutput, String commandDescription, String... cmdLineParams) throws AdbWrapperException

  void reconnect(String deviceSerialNumber) throws AdbWrapperException
}
