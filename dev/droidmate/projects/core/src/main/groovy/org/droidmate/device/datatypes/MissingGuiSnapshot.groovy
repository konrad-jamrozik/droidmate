// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import org.droidmate.exceptions.ForbiddenOperationError

class MissingGuiSnapshot implements IDeviceGuiSnapshot, Serializable
{
  private static final long serialVersionUID = 1

  @Override
  String getWindowHierarchyDump()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  String getPackageName()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  IGuiState getGuiState()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  ValidationResult getValidationResult()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  String getId()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  public String toString()
  {
    return "N/A (lack of ${IDeviceGuiSnapshot.class.simpleName})"
  }
}
