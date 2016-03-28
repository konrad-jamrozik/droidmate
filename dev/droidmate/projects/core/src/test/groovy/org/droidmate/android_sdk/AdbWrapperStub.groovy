// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.android_sdk

import org.droidmate.exceptions.AdbWrapperException

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
  void pushJar(String deviceSerialNumber, Path jarFile) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void removeJar(String deviceSerialNumber, Path jarFile) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void installApk(String deviceSerialNumber, IApk instrumentedApk) throws AdbWrapperException
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
  void removeFile(String deviceSerialNumber, String fileName) throws AdbWrapperException
  {
  }

  @Override
  void pullFile(String deviceSerialNumber, String pulledFileName, String destinationFilePath) throws AdbWrapperException
  {
  }
}
