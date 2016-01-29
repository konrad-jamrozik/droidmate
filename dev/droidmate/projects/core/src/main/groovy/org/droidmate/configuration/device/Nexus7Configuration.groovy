// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration.device

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.datatypes.GuiState

/**
 * Created by Nataniel Borges Jr. on 26/01/2016.
 */
public class Nexus7Configuration implements IDeviceSpecificConfiguration
{
  private static final  String package_android_launcher = "com.android.launcher"

  @Override
  boolean isHomeScreen(GuiState guiState)
  {
    String topNodePackageName = guiState.getTopNodePackageName()
    List<Widget> widgets = guiState.getWidgets()
    return topNodePackageName == package_android_launcher && !widgets.any {it.text == "Widgets"}
  }
}
