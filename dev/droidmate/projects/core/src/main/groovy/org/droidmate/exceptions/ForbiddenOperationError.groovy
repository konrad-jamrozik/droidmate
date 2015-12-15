// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

class ForbiddenOperationError extends DroidmateError
{

  public ForbiddenOperationError()
  {
    super()
  }

  public ForbiddenOperationError(String message) {super(message)}
}
