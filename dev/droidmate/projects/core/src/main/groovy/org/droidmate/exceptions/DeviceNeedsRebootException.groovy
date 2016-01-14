// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

class DeviceNeedsRebootException extends TcpServerUnreachableException
{
  private static final long serialVersionUID = 1

  DeviceNeedsRebootException()
  {
    super()
  }

  DeviceNeedsRebootException(Throwable cause)
  {
    super(cause)
  }

  DeviceNeedsRebootException(String message)
  {
    super(message)
  }

  DeviceNeedsRebootException(String message, Throwable cause)
  {
    super(message, cause)
  }
}
