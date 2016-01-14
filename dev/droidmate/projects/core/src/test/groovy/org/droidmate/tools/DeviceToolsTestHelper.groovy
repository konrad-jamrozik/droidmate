// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.tools

import org.droidmate.android_sdk.AdbWrapperStub
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.configuration.Configuration
import org.droidmate.device_simulation.AndroidDeviceSimulator

class DeviceToolsTestHelper
{

  static IDeviceTools buildForTesting(
    Configuration deviceToolsCfg = Configuration.default,
    IAaptWrapper aaptWrapper,
    AndroidDeviceSimulator simulator)
  {
    Map substitutes = [
      (IAdbWrapper)          : new AdbWrapperStub(),
      (IAndroidDeviceFactory): {serialNumber -> simulator} as IAndroidDeviceFactory
    ]

    if (aaptWrapper != null)
      substitutes[(IAaptWrapper)] = aaptWrapper

    return new DeviceTools(deviceToolsCfg, substitutes)
  }

}
