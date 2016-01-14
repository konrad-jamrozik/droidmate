// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

public class TcpServerUnreachableException extends DeviceException
{
  private static final long serialVersionUID = 1L

  public TcpServerUnreachableException()
  {
    super()
  }

  public TcpServerUnreachableException(Throwable cause)
  {
    super(cause)
  }

  public TcpServerUnreachableException(String message, Throwable cause)
  {
    super(message, cause)
  }

  public TcpServerUnreachableException(String message)
  {
    super(message)
  }
}
