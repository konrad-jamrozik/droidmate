// Copyright (c) 2012-2016 Saarland University
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
import org.droidmate.configuration.model.IDeviceModel

// KJA2-clean untangle cycle between GuiState and IDeviceModel
@Canonical(excludes = "id")
class GuiState implements Serializable, IGuiState
{
  private static final long serialVersionUID = 1

  final IDeviceModel deviceModel
  final String       topNodePackageName
  final List<Widget> widgets

  /** Id is used only for tests, for easy determination by human which instance is which when looking at widget string
   * representation. */
  final String id

  GuiState(String topNodePackageName, List<Widget> widgets, IDeviceModel deviceModel)
  {
    this(topNodePackageName, null, widgets, deviceModel)
  }

  GuiState(String topNodePackageName, String id, List<Widget> widgets, IDeviceModel deviceModel)
  {
    this.topNodePackageName = topNodePackageName
    this.widgets = widgets
    this.id = id
    this.deviceModel = deviceModel

    assert this.deviceModel != null
    assert !this.topNodePackageName?.empty
    assert widgets != null
  }

  GuiState(IGuiState guiState, String id, IDeviceModel deviceModel)
  {
    this(guiState.topNodePackageName, id, guiState.widgets, deviceModel)
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
    return this.deviceModel.isHomeScreen(this)
  }

  @Override
  boolean isAppHasStoppedDialogBox()
  {
    return this.deviceModel.isAppHasStoppedDialogBox(this)
  }

  @Override
  boolean isCompleteActionUsingDialogBox()
  {
    return this.deviceModel.isCompleteActionUsingDialogBox(this)
  }

  @Override
  boolean isSelectAHomeAppDialogBox()
  {
    return this.deviceModel.isSelectAHomeAppDialogBox(this)
  }

  @Override
  boolean belongsToApp(String appPackageName)
  {
    return this.topNodePackageName == appPackageName
  }

}
