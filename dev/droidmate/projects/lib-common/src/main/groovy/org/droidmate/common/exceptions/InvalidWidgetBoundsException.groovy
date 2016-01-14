// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common.exceptions

import org.droidmate.common.DroidmateException;

public class InvalidWidgetBoundsException extends DroidmateException
{
  private static final long serialVersionUID = 1;

  public InvalidWidgetBoundsException(String message)
  {
    super(message);
  }
}
