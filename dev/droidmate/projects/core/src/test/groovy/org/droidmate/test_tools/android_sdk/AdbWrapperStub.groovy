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

package org.droidmate.test_tools.android_sdk

import org.droidmate.android_sdk.AdbWrapperException
import org.droidmate.android_sdk.AndroidDeviceDescriptor
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.android_sdk.IApk

import java.nio.file.Path

class AdbWrapperStub implements IAdbWrapper
{

  @Override
  void startAdbServer() throws AdbWrapperException
  {
    return
  }

  @Override
  void killAdbServer() throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  List<AndroidDeviceDescriptor> getAndroidDevicesDescriptors() throws AdbWrapperException
  {
    [new AndroidDeviceDescriptor("fake-serial-number", false)]
  }

  @Override
  List<String> waitForMessagesOnLogcat(String deviceSerialNumber, String messageTag, int minMessagesCount, int waitTimeout, int queryDelay) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }


  @Override
  void forwardPort(String deviceSerialNumber, int port) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void reverseForwardPort(String deviceSerialNumber, int port) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void pushJar(String deviceSerialNumber, Path jarFile, String targetFileName = null) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void removeJar(String deviceSerialNumber, Path jarFile) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void installApk(String deviceSerialNumber, IApk apkToInstall) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void installApk(String deviceSerialNumber, Path apkToInstall) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void uninstallApk(String deviceSerialNumber, String apkPackageName, boolean ignoreFailure) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void launchMainActivity(String deviceSerialNumber, String launchableActivityName) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void clearLogcat(String deviceSerialNumber) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }


  @Override
  boolean clearPackage(String deviceSerialNumber, String apkPackageName)
  {
    assert false: "Not yet implemented!"
  }

  @Override
  List<String> readMessagesFromLogcat(String deviceSerialNumber, String messagesTag)
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String listPackages(String deviceSerialNumber) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String listPackage(String deviceSerialNumber, String packageName) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String ps(String deviceSerialNumber) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void reboot(String deviceSerialNumber) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }


  @Override
  void startUiautomatorDaemon(String deviceSerialNumber, int port) throws AdbWrapperException
  {
  }

  @Override
  void removeFile_api23(String deviceSerialNumber, String fileName, String shellPackageName) throws AdbWrapperException
  {
  }
  
  @Override
  void pullFile_api23(String deviceSerialNumber, String pulledFileName, String destinationFilePath, String shellPackageName) throws AdbWrapperException
  {
  }

  @Override
  void takeScreenshot(String deviceSerialNumber, String targetPath) throws AdbWrapperException
  {
  }

  @Override
  String executeCommand(String deviceSerialNumber, String successfulOutput, String commandDescription, String... cmdLineParams) throws AdbWrapperException
  {
    return ""
  }

  @Override
  void reconnect(String deviceSerialNumber) throws AdbWrapperException
  {
  }
}
