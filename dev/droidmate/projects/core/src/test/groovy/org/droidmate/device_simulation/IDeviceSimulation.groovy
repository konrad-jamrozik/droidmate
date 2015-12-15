// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import com.google.common.annotations.VisibleForTesting
import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.logcat.ITimeFormattedLogcatMessage

interface IDeviceSimulation
{
  void updateState(IAndroidDeviceAction deviceAction)

  IDeviceGuiSnapshot getCurrentGuiSnapshot()

  List<ITimeFormattedLogcatMessage> getCurrentLogs()

  String getPackageName()

  @VisibleForTesting
  List<IGuiScreen> getGuiScreens()

  void assertEqual(IDeviceSimulation other)

  boolean getAppIsRunning()
}