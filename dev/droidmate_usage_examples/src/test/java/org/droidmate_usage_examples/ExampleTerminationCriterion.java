// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate_usage_examples;

import org.droidmate.exploration.actions.ExplorationAction;
import org.droidmate.exploration.strategy.ITerminationCriterion;

/**
 * @see ExampleTerminationCriterion#ExampleTerminationCriterion()
 */
class ExampleTerminationCriterion implements ITerminationCriterion
{

  /**
   * @see ExampleExplorationStrategy#ExampleExplorationStrategy(ITerminationCriterion)
   */
  ExampleTerminationCriterion()
  {
  }

  private int callCounter = 2;

  @Override
  public String getLogMessage()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void initDecideCall(boolean firstCallToDecideFinished)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void updateState()
  {
    this.callCounter--;
  }

  @Override
  public boolean met()
  {
    return this.callCounter == 0;
  }

  @Override
  public String metReason()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void assertPostDecide(ExplorationAction outExplAction)
  {
    throw new UnsupportedOperationException();
  }
}
