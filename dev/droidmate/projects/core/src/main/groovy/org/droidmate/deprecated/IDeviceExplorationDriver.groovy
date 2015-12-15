// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import org.droidmate.device.datatypes.GuiState
import org.droidmate.exploration.actions.ExplorationAction

@Deprecated
public interface IDeviceExplorationDriver
{
  GuiState execute(ExplorationAction action)
}