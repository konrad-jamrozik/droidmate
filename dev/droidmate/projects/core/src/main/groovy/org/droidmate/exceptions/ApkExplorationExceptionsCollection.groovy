// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

import org.droidmate.common.DroidmateException

public class ApkExplorationExceptionsCollection extends DroidmateException
{
  private static final long serialVersionUID = 1

  @SuppressWarnings("GrFinalVariableAccess")
  final List<ApkExplorationException> exceptions

  public ApkExplorationExceptionsCollection(List<ApkExplorationException> exceptions)
  {
    super("Aggregating exception holding a collection of ${ApkExplorationException.simpleName}s.")
    assert exceptions.size() > 0
    this.exceptions = exceptions
  }
}


