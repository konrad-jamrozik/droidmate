// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tools

import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.configuration.Configuration
import org.droidmate.device.AndroidDevice
import org.droidmate.device.IAndroidDevice

class AndroidDeviceFactory implements IAndroidDeviceFactory
{

  private final Configuration                                         cfg
  private final IAdbWrapper                                           adbWrapper

  AndroidDeviceFactory(
    Configuration cfg,
    IAdbWrapper adbWrapper)
  {
    this.cfg = cfg
    this.adbWrapper = adbWrapper
  }

  @Override
  IAndroidDevice create(String serialNumber)
  {
    return new AndroidDevice(serialNumber, cfg, adbWrapper)
  }
}
