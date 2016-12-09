// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

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

  TerminationCriterion(Configuration config, int timeLimit, Ticker ticker)
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
  void initDecideCall(boolean firstCallToDecide)
  {
    if (timeLimited)
    {
      if (firstCallToDecide)
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
