// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

import java.awt.*

/**
 * Describes {@link IDeviceModel} of Google Nexus 10.
 *
 * @author Nataniel Borges Jr. (inception)
 * @author Konrad Jamrozik (refactoring)
 */
class Nexus10Model implements IDeviceModel
{
  final String androidLauncherPackageName = "com.android.launcher"
  
  @Override
  Dimension getDeviceDisplayDimensionsForTesting()
  {
    return new Dimension(1600, 2485)
  }
}
