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
import org.droidmate.exceptions.DeviceException

class DeviceReboot implements IDeviceReboot
{

  // KJA probably introduces cycle. Do Analyze Dependency Matrix...
  private final IAdbWrapper adbWrapper
  private final String      deviceSerialNumber
  private final ITcpClients tcpClients

  DeviceReboot(IAdbWrapper adbWrapper, String deviceSerialNumber, ITcpClients tcpClients)
  {
    this.adbWrapper = adbWrapper
    this.deviceSerialNumber = deviceSerialNumber
    this.tcpClients = tcpClients
  }

  @Override
  void tryRun() throws DeviceException
  {
    this.adbWrapper.reboot(this.deviceSerialNumber)
    this.tcpClients.forwardPorts()
    this.adbWrapper.startUiaDaemon(this.deviceSerialNumber)
  }
}
