// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import groovy.transform.Canonical
import org.droidmate.common.TextUtilsCategory
import org.droidmate.common.exploration.datatypes.Widget

@Canonical(excludes = "id")
class GuiState implements Serializable, IGuiState
{

  private static final long serialVersionUID = 1

  public static final  String package_android_launcher                     = "com.android.launcher"
  private static final String PACKAGE_ANDROID                              = "android"

  final String       topNodePackageName
  final List<Widget> widgets

  /** Id is used only for tests, for easy determination by human which instance is which when looking at widget string
   * representation. */
  final String id


  GuiState(String topNodePackageName, List<Widget> widgets)
  {
    this(topNodePackageName, null, widgets)
  }

  GuiState(String topNodePackageName, String id, List<Widget> widgets)
  {
    this.topNodePackageName = topNodePackageName
    this.widgets = widgets
    this.id = id

    assert !this.topNodePackageName?.empty
    assert widgets != null
  }

  GuiState(IGuiState guiState, String id)
  {
    this.topNodePackageName = guiState.topNodePackageName
    this.widgets = guiState.widgets
    this.id = id
  }


  @Override
  public List<Widget> getActionableWidgets()
  {
    widgets.findAll {it.canBeActedUpon()}
  }

  @Override
  public String toString()
  {
    use(TextUtilsCategory) {
      if (this.isHomeScreen())
        return "GUI state: home screen".wrapWith("<>")

      if (this instanceof AppHasStoppedDialogBoxGuiState)
        return "GUI state of \"App has stopped\" dialog box. OK widget enabled: ${(this as AppHasStoppedDialogBoxGuiState).OKWidget.enabled}".wrapWith("<>")

      return "GuiState " + (id != null ? "id=$id " : "") + "pkg=$topNodePackageName Widgets count = ${widgets.size()}".wrapWith("<>")
    }
  }

  @Override
  boolean isHomeScreen()
  {
    return this.topNodePackageName == package_android_launcher && !this.widgets.any { it.text == "Widgets" }
  }

  @Override
  boolean isAppHasStoppedDialogBox()
  {
    return topNodePackageName == PACKAGE_ANDROID &&
      widgets.any {it.text == "OK"} &&
      !widgets.any {it.text == "Just once"}
  }

  @Override
  boolean isCompleteActionUsingDialogBox()
  {
    return !isSelectAHomeAppDialogBox() && topNodePackageName == PACKAGE_ANDROID &&
      widgets.any {it.text == "Just once"}
  }

  @Override
  boolean isSelectAHomeAppDialogBox()
  {
    return topNodePackageName == PACKAGE_ANDROID &&
      widgets.any {it.text == "Just once"} && widgets.any {it.text == "Select a home app"}
  }


  @Override
  boolean belongsToApp(String appPackageName)
  {
    return this.topNodePackageName == appPackageName
  }

}
