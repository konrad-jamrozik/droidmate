// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

public interface IDeviceGuiSnapshot
{
  String getWindowHierarchyDump()

  String getPackageName()

  IGuiState getGuiState()

  ValidationResult getValidationResult()

  String getId()
}

