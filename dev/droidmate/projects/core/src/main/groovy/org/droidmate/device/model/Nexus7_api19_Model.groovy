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
 * Describes {@link IDeviceModel} of Google Nexus 7.
 *
 * @author Nataniel Borges Jr. (inception)
 * @author Konrad Jamrozik (refactoring)
 */
class Nexus7_API19_Model implements IDeviceModel
{
  final String androidLauncherPackageName = "com.android.launcher"
  
  @Override
  Dimension getDeviceDisplayDimensionsForTesting()
  {
    return new Dimension(800, 1205)
  }
}
