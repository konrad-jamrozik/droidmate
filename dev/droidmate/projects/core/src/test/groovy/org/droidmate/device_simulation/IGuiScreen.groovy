// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot

interface IGuiScreen extends Serializable
{

  IScreenTransitionResult perform(IAndroidDeviceAction action)

  IDeviceGuiSnapshot getGuiSnapshot()

  String getId()

  void addHomeScreenReference(IGuiScreen home)

  void addMainScreenReference(IGuiScreen main)

  void addWidgetTransition(String widgetId, IGuiScreen targetScreen)
  void addWidgetTransition(String widgetId, IGuiScreen targetScreen, boolean ignoreDuplicates)

  void buildInternals()

  void verify()
}

