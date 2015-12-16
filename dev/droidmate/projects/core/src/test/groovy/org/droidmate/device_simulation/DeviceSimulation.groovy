// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.device.datatypes.AdbClearPackageAction
import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.IGuiState
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.droidmate.misc.ITimeGenerator

class DeviceSimulation implements IDeviceSimulation
{

  final         String           packageName
  final         List<IGuiScreen> guiScreens
  private final IGuiScreen       initialScreen

  boolean appIsRunning
  private IScreenTransitionResult currentTransitionResult = null


  DeviceSimulation(ITimeGenerator timeGenerator, String packageName, String specString)
  {
    this(new GuiScreensBuilderFromSpec(timeGenerator, specString, packageName), packageName)
  }

  @Deprecated
  DeviceSimulation(IApkExplorationOutput out)
  {
    this(new GuiScreensBuilderFromApkExplorationOutput(out), out.appPackageName)
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
    this.appIsRunning = false
  }


  @Override
  void updateState(IAndroidDeviceAction action)
  {
    this.currentTransitionResult = this.currentScreen.perform(action)

    this.appIsRunning = determineIfAppIsRunning(this.appIsRunning, action, this.currentGuiSnapshot.guiState, this.packageName)
  }

  private static boolean determineIfAppIsRunning(boolean appWasRunning, IAndroidDeviceAction action, IGuiState guiState, String packageName)
  {
    if (guiState.belongsToApp(packageName))
    {
      assert !(action instanceof AdbClearPackageAction)
      return true
    }

    if (action instanceof AdbClearPackageAction)
      return  false

    return appWasRunning
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
