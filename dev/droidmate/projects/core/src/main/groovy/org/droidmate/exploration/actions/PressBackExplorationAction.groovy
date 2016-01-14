// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.actions

import groovy.transform.Canonical
import groovy.transform.TupleConstructor

@Canonical
@TupleConstructor(includeSuperProperties = true)
class PressBackExplorationAction extends ExplorationAction
{

  private static final long serialVersionUID = 1

  @Override
  String toString()
  {
    return super.toString()
  }

  @Override
  String toShortString()
  {
    "Press back"
  }
}
