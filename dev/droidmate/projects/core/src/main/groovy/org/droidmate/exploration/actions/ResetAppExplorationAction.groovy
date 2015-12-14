// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.actions

import groovy.transform.Canonical

@Canonical
class ResetAppExplorationAction extends ExplorationAction
{
  private static final long serialVersionUID = 1

  final isFirst = false

  @Override
  String toString()
  {
    return super.toString()
  }

  @Override
  String toShortString()
  {
    "Reset app"
  }
}
