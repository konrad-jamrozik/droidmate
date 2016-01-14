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

class UiautomatorDaemonClient implements IUiautomatorDaemonClient
{

  private final ISerializableTCPClient<DeviceCommand, DeviceResponse> client
  private final int                                                   port
  private final String                                                deviceSerialNumber
  private final IAdbWrapper                                           adbWrapper

  UiautomatorDaemonClient(int socketTimeout, int port, String deviceSerialNumber, IAdbWrapper adbWrapper)
  {
    this.port = port
    this.adbWrapper = adbWrapper
    this.deviceSerialNumber = deviceSerialNumber
    this.client = new SerializableTCPClient<DeviceCommand, DeviceResponse>(socketTimeout)
  }

  @Override
  DeviceResponse sendCommandToUiautomatorDaemon(DeviceCommand deviceCommand) throws TcpServerUnreachableException, DeviceException, ConnectException
  {
    this.client.queryServer(deviceCommand, this.port)
  }

  @Override
  void forwardPorts() throws DeviceException
  {
    this.adbWrapper.forwardPort(this.deviceSerialNumber, this.port)
  }
}
