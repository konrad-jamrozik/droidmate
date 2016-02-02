// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration.model

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.datatypes.GuiState

/**
 * Provides device specific methods for a Google Nexus 7 using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: ConcreteProduct
 *
 * @author Nataniel Borges Jr.
 */
public class Nexus7Model extends AbstractDeviceModel
{
  public static final String package_android_launcher = "com.android.launcher"

  @Override
  boolean isHomeScreen(GuiState guiState)
  {
    String topNodePackageName = guiState.getTopNodePackageName()
    List<Widget> widgets = guiState.getWidgets()
    return topNodePackageName == package_android_launcher && !widgets.any {it.text == "Widgets"}
  }

  @Override
  String getPackageAndroidLauncher()
  {
    return packageAndroidLauncher;
  }
}
