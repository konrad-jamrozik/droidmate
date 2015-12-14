// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tools

import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.common_android.DeviceCommand
import org.droidmate.common_android.DeviceResponse
import org.droidmate.configuration.Configuration
import org.droidmate.device.AndroidDevice
import org.droidmate.device.IAndroidDevice
import org.droidmate.device.ISerializableTCPClient

class AndroidDeviceFactory implements IAndroidDeviceFactory
{

  private final Configuration                                         cfg
  private final ISerializableTCPClient<DeviceCommand, DeviceResponse> deviceTcpClient
  private final IAdbWrapper                                           adbWrapper

  AndroidDeviceFactory(
    Configuration cfg,
    ISerializableTCPClient<DeviceCommand, DeviceResponse> deviceTcpClient,
    IAdbWrapper adbWrapper)
  {
    this.cfg = cfg
    this.deviceTcpClient = deviceTcpClient
    this.adbWrapper = adbWrapper
  }

  @Override
  IAndroidDevice create(String serialNumber)
  {
    return new AndroidDevice(serialNumber, cfg, deviceTcpClient, adbWrapper)
  }
}
