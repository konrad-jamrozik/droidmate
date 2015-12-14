// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exceptions

@Deprecated
class UiautomatorWindowDumpValidationException extends DeviceException
{
  private static final long serialVersionUID = 1;

  public final String windowHierarchyDump

  UiautomatorWindowDumpValidationException(String windowHierarchyDump, String message)
  {
    super(message)
    this.windowHierarchyDump = windowHierarchyDump
  }
}
