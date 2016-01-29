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
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.configuration.device.IDeviceSpecificConfiguration

@Canonical
class AppHasStoppedDialogBoxGuiState extends GuiState implements Serializable
{
  private static final long serialVersionUID = 1

  AppHasStoppedDialogBoxGuiState(String topNodePackageName, List<Widget> widgets, IDeviceSpecificConfiguration deviceConfiguration)
  {
    super(topNodePackageName, widgets, deviceConfiguration)
  }

  Widget getOKWidget() {
    return this.widgets.find { it.text == "OK" }
  }

}
