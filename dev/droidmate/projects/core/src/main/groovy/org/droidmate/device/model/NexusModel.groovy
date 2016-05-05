// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

/**
 * Provides device specific methods for a Google Nexus device using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: ConcreteProduct <br/>
 *
 * @author Nataniel Borges Jr.
 */
public abstract class NexusModel extends AbstractDeviceModel
{
  public static final String package_android_launcher = "com.android.launcher"

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
}
