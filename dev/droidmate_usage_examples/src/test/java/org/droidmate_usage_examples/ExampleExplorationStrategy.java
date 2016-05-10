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
import org.droidmate.exploration.actions.IExplorationActionRunResult;
import org.droidmate.exploration.strategy.IExplorationStrategy;
import org.droidmate.exploration.strategy.ITerminationCriterion;

/**
  @see ExampleExplorationStrategy#ExampleExplorationStrategy(ITerminationCriterion)
 */
class ExampleExplorationStrategy implements IExplorationStrategy
{
  private final ITerminationCriterion terminationCriterion;

  /**
   * <p>
   * Constructs {@link ExampleExplorationStrategy}. This is a very minimalistic custom exploration strategy used to show you
   * how to inject into DroidMate your own custom strategy.
   *
   * </p><p>
   * For an example of how to actually write an exploration strategy, see:
   * <a href="https://github.com/konrad-jamrozik/droidmate/blob/master/dev/droidmate/projects/core/src/main/groovy/org/droidmate/exploration/strategy/ExplorationStrategy.groovy">
   *   ExplorationStrategy in master branch on GitHub</a>
   *
   * </p><p>
   * Note you do not have to use {@link ITerminationCriterion} interface.
   * Just provide yourself your custom logic instead.
   *
   * </p>
   */
  ExampleExplorationStrategy(ITerminationCriterion terminationCriterion)
  {
    this.terminationCriterion = terminationCriterion;
  }

  @Override
  public ExplorationAction decide(IExplorationActionRunResult result)
  {
    terminationCriterion.updateState();

    if (terminationCriterion.met())
      return ExplorationAction.newTerminateExplorationAction();
    else
      return ExplorationAction.newResetAppExplorationAction();
  }
}
