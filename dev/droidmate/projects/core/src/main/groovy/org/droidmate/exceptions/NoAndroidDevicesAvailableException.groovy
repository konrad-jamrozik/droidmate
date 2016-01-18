// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exceptions;

public class NoAndroidDevicesAvailableException extends AdbWrapperException
{
  private static final long serialVersionUID = 1;

  private static String message = "No android devices available, i.e. command \"<android sdk>/platform-tools/adb devices\" returns no devices."

  public NoAndroidDevicesAvailableException()
  {
    super(message)
  }

  public NoAndroidDevicesAvailableException(Throwable cause)
  {
    super(message, cause);
  }
}
