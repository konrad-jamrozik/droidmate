// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

import org.droidmate.common.DroidmateException

/**
 * Exception produced by {@link org.droidmate.configuration.model.IDeviceModelCreator}
 * when attempting to create an unknown device.<br/>
 *
 * @author Nataniel Borges Jr.
 */
public class UnknownDeviceException extends DroidmateException
{
  private static final long serialVersionUID = 1

  public UnknownDeviceException()
  {
    super()
  }

  public UnknownDeviceException(Throwable cause)
  {
    super(cause)
  }

  public UnknownDeviceException(String message, Throwable cause)
  {
    super(message, cause)
  }

  public UnknownDeviceException(String message)
  {
    super(message)
  }
}
