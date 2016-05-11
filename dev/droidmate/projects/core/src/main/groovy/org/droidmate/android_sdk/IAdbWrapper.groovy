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

public interface IAdbWrapper {

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

  void removeFile_api19(String deviceSerialNumber, String fileName) throws AdbWrapperException

  void removeFile_api23(String deviceSerialNumber, String fileName, String shellPackageName) throws AdbWrapperException

  void pullFile_api19(String deviceSerialNumber, String pulledFileName, String destinationFilePath) throws AdbWrapperException

  void pullFile_api23(String deviceSerialNumber, String pulledFileName, String destinationFilePath, String shellPackageName) throws AdbWrapperException
}
