// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.android_sdk

import org.droidmate.common.DroidmateException

public class ExplorationException extends DroidmateException
{

  private static final long serialVersionUID = 1

  public ExplorationException()
  {
    super()
  }

  public ExplorationException(Throwable cause)
  {
    super(cause)
  }

  public ExplorationException(String message, Throwable cause)
  {
    super(message, cause)
  }

  public ExplorationException(String message)
  {
    super(message)
  }
}
