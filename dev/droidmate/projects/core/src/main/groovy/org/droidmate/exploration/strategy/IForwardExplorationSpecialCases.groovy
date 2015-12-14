// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.strategy

import org.droidmate.device.datatypes.IGuiState

interface IForwardExplorationSpecialCases
{

  public List process(IGuiState guiState, String packageName)
}