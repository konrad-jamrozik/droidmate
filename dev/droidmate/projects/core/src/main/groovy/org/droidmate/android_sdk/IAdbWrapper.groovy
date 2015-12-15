// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.android_sdk

import org.droidmate.exceptions.AdbWrapperException

public interface IAdbWrapper {

  void startAdbServer() throws AdbWrapperException

  void killAdbServer() throws AdbWrapperException

  List<AndroidDeviceDescriptor> getAndroidDevicesDescriptors() throws AdbWrapperException

  List<String> waitForMessagesOnLogcat(String deviceSerialNumber, String messageTag, int minMessagesCount, int waitTimeout, int queryInterval)
    throws AdbWrapperException

  void waitForUiaDaemonToClose() throws AdbWrapperException

  void forwardPort(String deviceSerialNumber, int port) throws AdbWrapperException

  void reverseForwardPort(String deviceSerialNumber, int port) throws AdbWrapperException

  void pushJar(String deviceSerialNumber, File jarFile) throws AdbWrapperException

  void removeJar(String deviceSerialNumber, File jarFile) throws AdbWrapperException

  void installApk(String deviceSerialNumber, IApk instrumentedApk) throws AdbWrapperException

  void uninstallApk(String deviceSerialNumber, String apkPackageName, boolean warnAboutFailure) throws AdbWrapperException

  void launchMainActivity(String deviceSerialNumber, String launchableActivityName) throws AdbWrapperException

  void clearLogcat(String deviceSerialNumber) throws AdbWrapperException


  void startUiaDaemon(String deviceSerialNumber) throws AdbWrapperException

  boolean clearPackage(String deviceSerialNumber, String apkPackageName)

  List<String> readMessagesFromLogcat(String deviceSerialNumber, String messagesTag)

  String listPackages(String deviceSerialNumber) throws AdbWrapperException
}
