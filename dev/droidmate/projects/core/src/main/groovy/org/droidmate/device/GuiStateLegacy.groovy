// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.datatypes

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common_android.Constants
import org.droidmate.configuration.model.DeviceModelHelper
import org.droidmate.configuration.model.IDeviceModel

/**
 * Wrapper between the old GuiState class and its new version.
 * It is only used by {link org.droidmate.deprecated_still_used#DeprecatedClassesDeserializer} to open previous report versions
 *
 * @author Nataniel Borges Jr.
 * @deprecated This class is deprecated and should be using only when opening previous report versions trough {link org.droidmate.deprecated_still_used#DeprecatedClassesDeserializer}
 */
@Deprecated
class GuiStateLegacy implements Serializable, IGuiState
{
  final String       topNodePackageName
  final List<Widget> widgets

  /** Id is used only for tests, for easy determination by human which instance is which when looking at widget string
   * representation. */
  final String id

  private GuiState createNewGuiState()
  {
    IDeviceModel deviceModel = DeviceModelHelper.build(Constants.DEVICE_DEFAULT)
    return new GuiState(this.topNodePackageName, this.id, this.widgets, deviceModel)
  }

  @Override
  public List<Widget> getActionableWidgets()
  {
    return this.createNewGuiState().getActionableWidgets()
  }

  public String toString()
  {
    return this.createNewGuiState().toString()
  }

  @Override
  boolean isHomeScreen()
  {
    return this.createNewGuiState().isHomeScreen()
  }

  @Override
  boolean isAppHasStoppedDialogBox()
  {
    return this.createNewGuiState().isAppHasStoppedDialogBox()
  }

  @Override
  boolean isCompleteActionUsingDialogBox()
  {
    return this.createNewGuiState().isCompleteActionUsingDialogBox()
  }

  @Override
  boolean isSelectAHomeAppDialogBox()
  {
    return this.createNewGuiState().isSelectAHomeAppDialogBox()
  }

  @Override
  boolean belongsToApp(String appPackageName)
  {
    return this.createNewGuiState().belongsToApp(appPackageName)
  }
}
