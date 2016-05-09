// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

import java.awt.Dimension

class Nexus7_2013_AVD_API23_Model implements IDeviceModel
{

  final String androidLauncherPackageName = "com.android.launcher3"

  @Override
  Dimension getDeviceDisplayDimensionsForTesting()
  {
    return new Dimension(1200, 1920)
  }
}
