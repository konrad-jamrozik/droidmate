// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;


import org.droidmate.common.DroidmateException;

public class RobotException extends DroidmateException
{
  private static final long serialVersionUID = 1L;

  public RobotException() {
    super();
  }

  public RobotException(String message, Throwable cause) {
    super(message, cause);
  }

  public RobotException(String message) {
    super(message);
  }

  public RobotException(Throwable cause) {
    super(cause);
  }
}
