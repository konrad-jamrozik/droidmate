// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.actions

import org.droidmate.common.Pair

class RunnableExplorationActionWithResult extends Pair<IRunnableExplorationAction, IExplorationActionRunResult>
{

  RunnableExplorationActionWithResult(IRunnableExplorationAction first, IExplorationActionRunResult second)
  {
    super(first, second)
  }

  IRunnableExplorationAction getAction()
  {
    return first
  }

  IExplorationActionRunResult getResult()
  {
    return second
  }

}
