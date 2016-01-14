// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exceptions

import groovy.util.logging.Slf4j
import org.droidmate.common.DroidmateException

@Slf4j
public class TestSaboteurException extends DroidmateException
{
  public TestSaboteurException()
  {
    super();
  }

  public TestSaboteurException(String message)
  {
    super(message);
  }

}
