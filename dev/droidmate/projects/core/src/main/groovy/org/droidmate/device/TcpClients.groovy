// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device

import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.common_android.DeviceCommand
import org.droidmate.common_android.DeviceResponse
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.TcpServerUnreachableException

class TcpClients implements ITcpClients
{
  @Delegate
  private final IMonitorsClient                                       monitorsClient

  private final ISerializableTCPClient<DeviceCommand, DeviceResponse> uiautomatorClient
  private final int                                                   uiautomatorDaemonTcpPort
  private final IAdbWrapper                                           adbWrapper
  private final String                                                deviceSerialNumber

  TcpClients(IAdbWrapper adbWrapper, String deviceSerialNumber,int socketTimeout, int uiautomatorDaemonTcpPort)
  {
    this.deviceSerialNumber = deviceSerialNumber
    this.adbWrapper = adbWrapper
    this.uiautomatorDaemonTcpPort = uiautomatorDaemonTcpPort
    // KJA dependency cycle. devicereboot -> tcpclients -> devicereboot
    IDeviceReboot deviceReboot = new DeviceReboot(this.adbWrapper, this.deviceSerialNumber, this)
    this.uiautomatorClient = new SerializableTCPClient<DeviceCommand, DeviceResponse>(socketTimeout, deviceReboot)
    this.monitorsClient = new MonitorsClient(socketTimeout, deviceSerialNumber, adbWrapper, deviceReboot)
  }

  @Override
  DeviceResponse sendCommandToUiautomatorDaemon(DeviceCommand deviceCommand)throws TcpServerUnreachableException, DeviceException
  {
    this.uiautomatorClient.queryServer(deviceCommand, this.uiautomatorDaemonTcpPort)
  }

  @Override
  void forwardPorts()
  {
    this.adbWrapper.forwardPort(this.deviceSerialNumber, this.uiautomatorDaemonTcpPort)
    this.monitorsClient.forwardPorts()
  }
}
