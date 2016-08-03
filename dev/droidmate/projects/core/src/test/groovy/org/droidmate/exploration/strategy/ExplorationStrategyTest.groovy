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

import groovy.transform.TypeChecked
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.configuration.Configuration
import org.droidmate.device.datatypes.GuiState
import org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper
import org.droidmate.exploration.actions.ExplorationAction
import org.droidmate.exploration.actions.IExplorationActionRunResult
import org.droidmate.exploration.actions.WidgetExplorationAction
import org.droidmate.exploration.data_aggregators.ExplorationOutput2Builder
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_base.FilesystemTestFixtures
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import static org.droidmate.device.datatypes.GuiStateTestHelper.*
import static org.droidmate.exploration.actions.ExplorationAction.*

/**
 * Untested behavior:
 * <ul>
 *   <li>Chooses only <i>clickable</i> widgets to click from the input GUI state.</li>
 * </ul>
 */
@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class ExplorationStrategyTest extends DroidmateGroovyTestCase
{
  private static IExplorationStrategy getStrategy(Integer actionsLimit = Configuration.defaultActionsLimit,
                                                  Integer resetEveryNthExplorationForward = Configuration.defaultResetEveryNthExplorationForward)
  {
    return ExplorationStrategyTestHelper.buildStrategy(actionsLimit, resetEveryNthExplorationForward)
  }

  @Test
  void "Given no clickable widgets after app was initialized or reset, requests termination"()
  {
    // Act 1 & Assert
    verifyProcessOnGuiStateReturnsTerminateExplorationAction(getStrategy(), newGuiStateWithTopLevelNodeOnly())

    // Act 2 & Assert
    verifyProcessOnGuiStateReturnsTerminateExplorationAction(getStrategy(), newGuiStateWithDisabledWidgets(1))
  }

  @Test
  void "Given no clickable widgets during normal exploration, requests app reset"()
  {
    def strategy = getStrategy()
    makeIntoNormalExplorationMode(strategy)
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, newGuiStateWithTopLevelNodeOnly())
  }

  @Test
  void "Given home screen, other app or 'app has stopped' screen during normal exploration, requests app reset"()
  {
    // ----- Test 1 -----

    def strategy = getStrategy()
    makeIntoNormalExplorationMode(strategy)

    // Act & Assert 1
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, newHomeScreenGuiState())

    // ----- Test 2 -----

    strategy = getStrategy()
    makeIntoNormalExplorationMode(strategy)

    // Act & Assert 2
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, newOutOfAppScopeGuiState())

    // ----- Test 3 -----

    strategy = getStrategy()
    makeIntoNormalExplorationMode(strategy)

    // Act & Assert 3
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, newAppHasStoppedGuiState())
  }

  @Test
  void "Given 'complete action using' dialog box, requests reset"()
  {
    def strategy = getStrategy()
    makeIntoNormalExplorationMode(strategy)

    // Act & Assert
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, newCompleteActionUsingGuiState())
  }


  @Test
  void "If normally would request second app reset in a row, instead terminates exploration, to avoid infinite loop"()
  {
    def strategy = getStrategy()
    makeIntoNormalExplorationMode(strategy)

    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, newAppHasStoppedGuiState())
    verifyProcessOnGuiStateReturnsTerminateExplorationAction(strategy, newGuiStateWithTopLevelNodeOnly())
  }

  @Test
  void "When exploring forward and configured so, resets exploration every time"()
  {
    def strategy = getStrategy(/* actionsLimit */ 3, /* resetEveryNthExplorationForward */ 1
    )
    def gs = newGuiStateWithWidgets(3, FilesystemTestFixtures.apkFixture_simple_packageName)

    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, gs)
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, gs)
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, gs)
    verifyProcessOnGuiStateReturnsTerminateExplorationAction(strategy, gs)
  }

  @Test
  void "When exploring forward and configured so, resets exploration every third time"()
  {
    def strategy = getStrategy(/* actionsLimit */ 8, /* resetEveryNthExplorationForward */ 3
    )
    def gs = newGuiStateWithWidgets(3, FilesystemTestFixtures.apkFixture_simple_packageName)
    def egs = newGuiStateWithTopLevelNodeOnly()

    verifyProcessOnGuiStateReturnsWidgetExplorationAction(strategy, gs) // 1st exploration forward: widget click
    verifyProcessOnGuiStateReturnsWidgetExplorationAction(strategy, gs) // 2nd exploration forward: widget click
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, gs) // 3rd exploration forward: reset

    verifyProcessOnGuiStateReturnsWidgetExplorationAction(strategy, gs) // 1st exploration forward: widget click
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, egs) // reset because cannot move forward
    verifyProcessOnGuiStateReturnsWidgetExplorationAction(strategy, gs) // 1st exploration forward: widget click
    verifyProcessOnGuiStateReturnsWidgetExplorationAction(strategy, gs) // 2nd exploration forward: widget click
    verifyProcessOnGuiStateReturnsResetExplorationAction(strategy, egs) // 3rd exploration forward: reset

    // At this point all 8 actions have been executed.

    verifyProcessOnGuiStateReturnsTerminateExplorationAction(strategy, gs)
  }

  /** After this method call the strategy should go from "before the first decision" to
   * "after the first decision, in the main decision loop" mode.
   * */
  private static ExplorationAction makeIntoNormalExplorationMode(IExplorationStrategy strategy)
  {
    return strategy.decide(newResultFromGuiState(newGuiStateWithWidgets(1)))
  }

  static IExplorationActionRunResult newResultFromGuiState(GuiState guiState)
  {
    def builder = new ExplorationOutput2Builder()
    return builder.buildActionResult([guiSnapshot: UiautomatorWindowDumpTestHelper.fromGuiState(guiState), packageName: FilesystemTestFixtures.apkFixture_simple_packageName])
  }

  private static void verifyProcessOnGuiStateReturnsWidgetExplorationAction(
    IExplorationStrategy strategy, GuiState gs, Widget w = null)
  {
    if (w == null)
      assert strategy.decide(newResultFromGuiState(gs)) instanceof WidgetExplorationAction
    else
      assert strategy.decide(newResultFromGuiState(gs)) == newWidgetExplorationAction(w)
  }

  private static void verifyProcessOnGuiStateReturnsTerminateExplorationAction(IExplorationStrategy strategy, GuiState gs)
  {
    assert strategy.decide(newResultFromGuiState(gs)) == newTerminateExplorationAction()
  }

  private static void verifyProcessOnGuiStateReturnsResetExplorationAction(IExplorationStrategy strategy, GuiState gs)
  {
    assert strategy.decide(newResultFromGuiState(gs)) == newResetAppExplorationAction()
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  private static void verifyProcessOnGuiStateReturnsPressBackExplorationAction(IExplorationStrategy strategy, GuiState gs)
  {
    assert strategy.decide(newResultFromGuiState(gs)) == newPressBackExplorationAction()
  }


}
