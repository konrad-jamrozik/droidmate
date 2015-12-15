// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

public class DeviceExceptionMissing extends DeviceException
{

  public DeviceExceptionMissing()
  {
  }

  public DeviceExceptionMissing(Throwable cause)
  {
    super(cause)
  }

  public DeviceExceptionMissing(String message, Throwable cause)
  {
    super(message, cause)
  }

  public DeviceExceptionMissing(String message)
  {
    super(message)
  }

  @Override
  public String toString()
  {
    return "N/A (lack of ${DeviceException.class.simpleName})"
  }

}
