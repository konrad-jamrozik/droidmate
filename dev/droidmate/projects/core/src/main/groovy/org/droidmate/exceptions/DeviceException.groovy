// Copyright (c) 2013-2015 Saarland University
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

  public DeviceException()
  {
    super()
  }

  public DeviceException(Throwable cause)
  {
    super(cause)
  }

  public DeviceException(String message, Throwable cause)
  {
    super(message, cause)
  }

  public DeviceException(String message)
  {
    super(message)
  }

}
