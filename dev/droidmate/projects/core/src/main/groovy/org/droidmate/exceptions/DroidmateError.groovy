// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exceptions

class DroidmateError extends Error
{

  public DroidmateError(String message, Throwable cause)
  {
    super(message, cause)
  }

  DroidmateError(String message)
  {
    super(message)
  }

  public DroidmateError(Throwable cause)
  {
    super(cause)
  }

  DroidmateError()
  {
    super()
  }
}
