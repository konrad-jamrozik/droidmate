// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.strategy

import com.google.common.base.Stopwatch
import com.google.common.base.Ticker
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.configuration.Configuration
import org.droidmate.exploration.actions.ExplorationAction
import org.droidmate.exploration.actions.TerminateExplorationAction

import java.util.concurrent.TimeUnit

@TypeChecked
@Slf4j
class TerminationCriterion implements ITerminationCriterion
{
  private int startingActionsLeft
  private int actionsLeft

  private       boolean       timeLimited = false
  private       int           timeLimit
  private final Stopwatch     stopwatch

  /** Used when printing out current exploration progress. Expected to match current exploration action ordinal.
   * Starts at 2, because the first exploration action is issued before a request to log is issued.*/
  private int logRequestIndex = 2

  public TerminationCriterion(Configuration config, int timeLimit, Ticker ticker)
  {
    this.timeLimit = timeLimit

    if (timeLimit > 0)
    {
      timeLimited = true
      stopwatch = Stopwatch.createUnstarted(ticker)

    } else
    {
      this.startingActionsLeft = config.widgetIndexes?.size() > 0 ?
        config.widgetIndexes.size() :
        config.actionsLimit

      this.actionsLeft = this.startingActionsLeft

      stopwatch = null
    }
  }


  private long currentDecideElapsedSeconds

  @Override
  String getLogMessage()
  {
    if (timeLimited)
    {
      long m = (long) (this.currentDecideElapsedSeconds / 60)
      long s = this.currentDecideElapsedSeconds - m * 60
      long lm = (int) (this.timeLimit / 60)
      long ls = this.timeLimit % 60

      return String.format("%3dm %2ds / %3dm %2ds i: %4d", m, s, lm, ls, this.logRequestIndex++)
    } else
      return (this.startingActionsLeft - this.actionsLeft).toString() + "/" + "${this.startingActionsLeft}"
  }

  @Override
  void initDecideCall(boolean firstCall)
  {
    if (timeLimited)
    {
      if (firstCall)
        stopwatch.start()

      this.currentDecideElapsedSeconds = this.stopwatch.elapsed(TimeUnit.SECONDS)
    }

    if (!timeLimited)
      assert actionsLeft >= 0

  }

  @Override
  void assertPostDecide(ExplorationAction outExplAction)
  {
    if (timeLimited)
    {
      assert !met() || (met() && outExplAction instanceof TerminateExplorationAction)
    } else
      assert actionsLeft >= 0 || (actionsLeft == -1 && outExplAction instanceof TerminateExplorationAction)
  }

  @Override
  boolean met()
  {
    if (timeLimited)
    {
      return this.currentDecideElapsedSeconds >= timeLimit
    } else
      return actionsLeft == 0
  }

  @Override
  String metReason()
  {
    if (timeLimited)
    {
      return "Allocated exploration time exhausted."
    } else
      return "No actions left."
  }

  @Override
  void updateState()
  {
    if (timeLimited)
    {
      // Nothing to do here.
    } else
    {
      assert met() || actionsLeft > 0
      actionsLeft--
    }
  }

}
