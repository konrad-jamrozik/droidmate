// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exceptions;

import org.droidmate.common.DroidmateException;

public class LaunchableActivityNameProblemException extends DroidmateException
{

  private static final long    serialVersionUID = 1

  final                boolean isFatal

  public LaunchableActivityNameProblemException()
  {
    super()
    this.isFatal = false
  }

  public LaunchableActivityNameProblemException(String message, boolean isFatal = false)
  {
    super(message)
    this.isFatal = isFatal
  }

}
