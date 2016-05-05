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
 * Describes {@link IDeviceModel} of Samsung Galaxy S3 GT-I9300.
 *
 * @author Nataniel Borges Jr. (inception)
 * @author Konrad Jamrozik (refactoring)
 */
class GalaxyS3Model implements IDeviceModel
{
  final String androidLauncherPackageName = "com.sec.android.app.launcher"

  @Override
  Dimension getDeviceDisplayDimensionsForTesting()
  {
    return new Dimension(720, 1205)
  }
}
