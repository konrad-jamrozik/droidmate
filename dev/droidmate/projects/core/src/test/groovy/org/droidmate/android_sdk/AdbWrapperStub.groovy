// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.android_sdk

import org.droidmate.exceptions.AdbWrapperException

class AdbWrapperStub implements IAdbWrapper {

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
  List<String> waitForMessagesOnLogcat(String deviceSerialNumber, String messageTag, int minMessagesCount, int waitTimeout, int queryInterval) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }


  @Override
  void waitForUiaDaemonToClose() throws AdbWrapperException
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
  void pushJar(String deviceSerialNumber, File jarFile) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void removeJar(String deviceSerialNumber, File jarFile) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void installApk(String deviceSerialNumber, IApk instrumentedApk) throws AdbWrapperException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void uninstallApk(String deviceSerialNumber, String apkPackageName, boolean warnAboutFailure) throws AdbWrapperException
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
  void startUiaDaemon(String deviceSerialNumber) throws AdbWrapperException
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
}
