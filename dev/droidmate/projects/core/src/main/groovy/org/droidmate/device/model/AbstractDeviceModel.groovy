// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.datatypes.GuiState

/**
 * Class with device specific methods common to multiple devices. Use Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: Product
 *
 * @author Nataniel Borges Jr.
 */
public abstract class AbstractDeviceModel implements IDeviceModel
{
  protected final String package_android = "android"

  protected abstract String getPackageAndroidLauncherName()

  @Override
  boolean isAppHasStoppedDialogBox(GuiState guiState)
  {
    String topNodePackageName = guiState.getTopNodePackageName()
    List<Widget> widgets = guiState.getWidgets()

    return topNodePackageName == package_android &&
      widgets.any {it.text == "OK"} &&
      !widgets.any {it.text == "Just once"}
  }

  @Override
  boolean isCompleteActionUsingDialogBox(GuiState guiState)
  {
    String topNodePackageName = guiState.getTopNodePackageName()
    List<Widget> widgets = guiState.getWidgets()

    return !isSelectAHomeAppDialogBox(guiState) &&
      topNodePackageName == package_android &&
      widgets.any {it.text == "Just once"}
  }

  @Override
  boolean isHomeScreen(GuiState guiState)
  {
    String topNodePackageName = guiState.getTopNodePackageName()
    List<Widget> widgets = guiState.getWidgets()
    return topNodePackageName == this.getPackageAndroidLauncherName() && !widgets.any {it.text == "Widgets"}
  }

  @Override
  boolean isSelectAHomeAppDialogBox(GuiState guiState)
  {
    String topNodePackageName = guiState.getTopNodePackageName()
    List<Widget> widgets = guiState.getWidgets()

    return topNodePackageName == package_android &&
      widgets.any {it.text == "Just once"} &&
      widgets.any {it.text == "Select a home app"}
  }
}
