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
 * Describes {@link IDeviceModel} of Google Nexus 5X.
 */
class Nexus5X_Model implements IDeviceModel
{
  final String androidLauncherPackageName = "com.google.android.googlequicksearchbox"
  
  @Override
  Dimension getDeviceDisplayDimensionsForTesting()
  {
    throw new UnsupportedOperationException("This device model is not expected to be used for testing.")
  }
}
