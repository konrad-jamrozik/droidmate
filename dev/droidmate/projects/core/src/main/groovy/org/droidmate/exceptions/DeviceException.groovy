// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exceptions

import org.droidmate.common.DroidmateException

public class DeviceException extends DroidmateException
{
  private static final long serialVersionUID = 1

  final boolean stopFurtherApkExplorations

  public DeviceException()
  {
    super()
    stopFurtherApkExplorations = false
  }

  public DeviceException(Throwable cause)
  {
    super(cause)
    stopFurtherApkExplorations = false
  }

  public DeviceException(String message)
  {
    super(message)
    stopFurtherApkExplorations = false
  }

  public DeviceException(String message, boolean stopFurtherApkExplorations)
  {
    super(message)
    this.stopFurtherApkExplorations = stopFurtherApkExplorations
  }

  public DeviceException(String message, Throwable cause, boolean stopFurtherApkExplorations = false)
  {
    super(message, cause)
    this.stopFurtherApkExplorations = stopFurtherApkExplorations
  }

}
