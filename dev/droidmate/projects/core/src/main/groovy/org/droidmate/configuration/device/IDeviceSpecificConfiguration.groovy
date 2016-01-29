// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration.device

import org.droidmate.device.datatypes.GuiState

/**
 * Created by Nataniel Borges Jr. on 26/01/2016.
 */
public interface IDeviceSpecificConfiguration extends Serializable
{
  boolean isHomeScreen(GuiState guiState)
}
