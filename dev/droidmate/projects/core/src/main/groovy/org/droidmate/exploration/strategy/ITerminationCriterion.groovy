// Copyright (c) 2013-2015 Saarland University
// All right reserved.
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

  void assertPreDecide()

  void init()

  void updateState()

  boolean met()

  String metReason()

  void assertPostDecide(ExplorationAction outExplAction)

}