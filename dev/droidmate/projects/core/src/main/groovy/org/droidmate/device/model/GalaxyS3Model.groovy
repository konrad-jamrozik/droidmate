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
 * Provides device specific methods for a Samsung Galaxy S3 GT-I9300 using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: ConcreteProduct
 *
 * @author Nataniel Borges Jr.
 */
class GalaxyS3Model extends AbstractDeviceModel
{
  public static final String package_android_launcher = "com.sec.android.app.launcher"

  @Override
  protected String getPackageAndroidLauncherName()
  {
    return package_android_launcher
  }

  @Override
  String getPackageAndroidLauncher()
  {
    return packageAndroidLauncher;
  }

  @Override
  Dimension getDeviceDisplayDimensionsForTesting()
  {
    return new Dimension(720, 1205)
  }
}
