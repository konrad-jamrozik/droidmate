// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.tools

import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.configuration.Configuration
import org.droidmate.device_simulation.AndroidDeviceSimulator

class DeviceToolsMock
{
  @SuppressWarnings("GrFinalVariableAccess")
  @Delegate
  public final IDeviceTools deviceTools

  DeviceToolsMock(Configuration cfg, IAaptWrapper aaptWrapper, AndroidDeviceSimulator simulator)
  {
    assert cfg.randomSeed == 0 as Long

    this.deviceTools = DeviceToolsTestHelper.buildForTesting(cfg, aaptWrapper, simulator)


  }

}
