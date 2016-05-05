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

interface IGuiState extends Serializable
{
  String getTopNodePackageName()

  List<Widget> getWidgets()

  String getId()

  List<Widget> getActionableWidgets()

  boolean isHomeScreen()

  boolean isAppHasStoppedDialogBox()

  boolean isRequestRuntimePermissionDialogBox()

  boolean isCompleteActionUsingDialogBox()

  boolean isSelectAHomeAppDialogBox()

  boolean belongsToApp(String appPackageName)

}