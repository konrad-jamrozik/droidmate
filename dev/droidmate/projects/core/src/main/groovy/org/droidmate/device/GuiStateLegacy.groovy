// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.device.datatypes

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.model.DeviceModel
import org.droidmate.device.model.IDeviceModel

/**
 * Wrapper between the old GuiState class and its new version.
 * It is only used by {@link org.droidmate.deprecated_still_used#DeprecatedClassesDeserializer} to open previous report versions
 *
 * @author Nataniel Borges Jr.
 * @deprecated This class is deprecated and should be using only when opening previous report versions trough {@link org.droidmate.deprecated_still_used#DeprecatedClassesDeserializer}
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
    IDeviceModel deviceModel = DeviceModel.buildDefault()
    return new GuiState(this.topNodePackageName, this.id, this.widgets, deviceModel.androidLauncherPackageName)
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
  boolean isRequestRuntimePermissionDialogBox()
  {
    return this.createNewGuiState().isRequestRuntimePermissionDialogBox()
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
