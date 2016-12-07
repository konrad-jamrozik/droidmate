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
package org.droidmate.test_tools.device_simulation

import org.droidmate.apis.ITimeFormattedLogcatMessage
import org.droidmate.device.datatypes.AdbClearPackageAction
import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class DeviceSimulation implements IDeviceSimulation
{

  final         String           packageName
  final         List<IGuiScreen> guiScreens
  private final IGuiScreen       initialScreen

  private IScreenTransitionResult currentTransitionResult = null

  private IAndroidDeviceAction lastAction = null


  DeviceSimulation(ITimeGenerator timeGenerator, String packageName, String specString)
  {
    this(new GuiScreensBuilderFromSpec(timeGenerator, specString, packageName), packageName)
  }

  DeviceSimulation(IApkExplorationOutput2 out)
  {
    this(new GuiScreensBuilderFromApkExplorationOutput2(out), out.packageName)
  }


  private DeviceSimulation(IGuiScreensBuilder guiScreensBuilder, String packageName)
  {
    this.packageName = packageName
    this.guiScreens = guiScreensBuilder.build()
    this.initialScreen = guiScreens.findSingle {it.id == GuiScreen.idHome}
  }


  @Override
  void updateState(IAndroidDeviceAction action)
  {
    this.currentTransitionResult = this.currentScreen.perform(action)
    this.lastAction = action
  }

  boolean getAppIsRunning()
  {
    if ((this.lastAction == null) || (this.lastAction instanceof AdbClearPackageAction))
      return false

    if (this.currentGuiSnapshot.guiState.belongsToApp(this.packageName))
    {
      assert !(this.lastAction instanceof AdbClearPackageAction)
      return true
    }

    return false
  }

  @Override
  IDeviceGuiSnapshot getCurrentGuiSnapshot()
  {
    if (this.currentTransitionResult == null)
      return this.initialScreen.guiSnapshot

    return this.currentScreen.guiSnapshot
  }

  @Override
  List<ITimeFormattedLogcatMessage> getCurrentLogs()
  {
    if (this.currentTransitionResult == null)
      return []

    return this.currentTransitionResult.logs
  }

  private IGuiScreen getCurrentScreen()
  {
    if (currentTransitionResult == null)
      return this.initialScreen

    return this.currentTransitionResult.screen
  }


  @Override
  void assertEqual(IDeviceSimulation other)
  {
    assert this.guiScreens*.id.sort() == other.guiScreens*.id.sort()

    this.guiScreens.each {IGuiScreen thisScreen ->
      IGuiScreen otherScreen = other.guiScreens.findSingle {thisScreen.id == it.id}
      assert thisScreen.id == otherScreen.id
      assert thisScreen.guiSnapshot.id == otherScreen.guiSnapshot.id
      assert thisScreen.guiSnapshot.guiState.widgets*.id.sort() == otherScreen.guiSnapshot.guiState.widgets*.id.sort()
    }
  }
}
