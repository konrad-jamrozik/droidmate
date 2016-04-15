// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.strategy

import org.droidmate.exploration.actions.ExplorationAction

interface ITerminationCriterion
{

  String getLogMessage()

  void initDecideCall(boolean firstCallToDecideFinished)

  void updateState(ExplorationAction performedAction)

  boolean met()

  String metReason()

  void assertPostDecide(ExplorationAction outExplAction)

}