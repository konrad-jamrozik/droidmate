// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.strategy

import com.google.common.base.Ticker
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common.logging.Markers
import org.droidmate.configuration.Configuration
import org.droidmate.configuration.ConfigurationBuilder
import org.droidmate.device.datatypes.IGuiState
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.*

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.exploration.actions.ExplorationAction.*

@TypeChecked
@Slf4j
class ExplorationStrategy implements IExplorationStrategy
{

  private final IWidgetStrategy                 widgetStrategy
  private final ITerminationCriterion           terminationCriterion
  private final IForwardExplorationSpecialCases specialCases

  /** Package name of the explored app */
  private final String packageName
  private final int    resetEveryNthExplorationForward

  /** Determines if last call to {@link #decide} returned {@link ResetAppExplorationAction}. */
  private boolean lastActionWasToReset = false

  /** Determines if during execution of any method in this class, at least one call to {@link #decide} has already fully finished.
   * This is set to false until the first call to {@link #decide} will set it to true near the end of its execution. */
  private boolean firstCallToDecideFinished = false

  private int forwardExplorationResetCounter

  // WISH super ugly, taken from widgetStrategy. Instead, it should be incorporated in
  // org.droidmate.exploration.strategy.ExplorationStrategy.explorationCanMoveForwardOn,
  // which also takes WidgetStrategy as input, and then is asked.
  private boolean allWidgetsBlackListed = false

  //SE TEAM Hook 1
  ///Holds all guiStates seen so far ehile exploring
  private List<IGuiState> guiStatesSeen = new LinkedList<>();
  //--------------

  @Deprecated
  ExplorationStrategy(IWidgetStrategy widgetStrategy,
                      String packageName,
                      Configuration config,
                      ITerminationCriterion terminationCriterion,
                      IForwardExplorationSpecialCases specialCases)
  {

    assertConfigurationDenotesNoMoreThanOneWidgetClickingMethod(config)

    this.widgetStrategy = widgetStrategy
    this.terminationCriterion = terminationCriterion
    this.packageName = packageName
    this.specialCases = specialCases

    this.resetEveryNthExplorationForward = config.resetEveryNthExplorationForward
    assert this.resetEveryNthExplorationForward >= 0
    this.forwardExplorationResetCounter = resetEveryNthExplorationForward

  }

  ExplorationStrategy(
    String packageName,
    int resetEveryNthExplorationForward,
    IWidgetStrategy widgetStrategy,
    ITerminationCriterion terminationCriterion,
    IForwardExplorationSpecialCases specialCases)
  {
    this.widgetStrategy = widgetStrategy
    this.terminationCriterion = terminationCriterion
    this.packageName = packageName
    this.specialCases = specialCases

    this.resetEveryNthExplorationForward = resetEveryNthExplorationForward
    assert this.resetEveryNthExplorationForward >= 0
    this.forwardExplorationResetCounter = resetEveryNthExplorationForward

  }

  private static void assertConfigurationDenotesNoMoreThanOneWidgetClickingMethod(Configuration cfg)
  {
    int settingsCount = ConfigurationBuilder.widgetClickingStrategySettingsCount(cfg)
    assert settingsCount <= 1
  }

  // KJA make it private. Fix/delete callers as necessary.
  @Override
  ExplorationAction decide(IGuiState guiState)
  {
    assert guiState != null
    terminationCriterion.initDecideCall(!firstCallToDecideFinished)


    ExplorationAction outExplAction

    allWidgetsBlackListed = widgetStrategy.updateState(guiState)

    boolean exploredForward = false
    if (terminateExploration(guiState))
      outExplAction = newTerminateExplorationAction()
    else if (resetExploration(guiState))
      outExplAction = newResetAppExplorationAction()
    else if (backtrack(guiState))
      outExplAction = newPressBackExplorationAction()
    else
    {
      outExplAction = exploreForward(guiState)
      exploredForward = true
    }

    updateState(outExplAction, exploredForward)

    logExplorationProgress(outExplAction)
    /* WISH Log clicked widgets indexes for manual repro preparation. It can be displayed in "run_data.txt" in a format that can
    be copy-pasted as input arg. This might require an upgrade to be able to also handle special actions like reset, etc.,
    not only widget indexes. It has to work for all kinds of exploration actions.
     */


    assert outExplAction != null
    terminationCriterion.assertPostDecide(outExplAction)

    frontendHook(outExplAction)

    return outExplAction

  }

  /**
   * Allows to hook into the the next ExplorationAction to be executed on the device.
   */
  void frontendHook(ExplorationAction explorationAction)
  {
    // To-do for SE team

    switch (explorationAction)
    {
      case WidgetExplorationAction:
        Widget w = (explorationAction as WidgetExplorationAction).widget

        String text = w.text // For other properties, see org.droidmate.common.exploration.datatypes.Widget

        log.trace(Markers.gui,"<widget_explored>" + text + "</widget_explored>")

        // Otherwise the widget is not interesting (DroidMate will never do anything with it)
        boolean canBeActedUpon = w.canBeActedUpon()

        break
      case ResetAppExplorationAction:
        // No interesting properties, but just knowing the class is useful.
        break
      case TerminateExplorationAction:
        // No interesting properties, but just knowing the class is useful.
        break
      default:
        throw new UnexpectedIfElseFallthroughError()
    }
  }

  @Override
  ExplorationAction decide(IExplorationActionRunResult result)
  {
    log.debug("decide($result)")
    assert result?.successful

    //KJA2-clean to remove
    //SE Team Hook 1
    def lastGuiScreen = guiStatesSeen.find({
      it == result.guiSnapshot.guiState
    })
    if (lastGuiScreen == null) {
      lastGuiScreen = result.guiSnapshot.guiState
      guiStatesSeen.add(lastGuiScreen)
      log.trace(Markers.gui,"<elements_seen>" + lastGuiScreen.widgets.size() + "</elements_seen>")
      log.trace(Markers.gui,"<gui_screens_seen>1</gui_screens_seen>")
    }
    //--------------

    return this.decide(result.guiSnapshot.guiState)
  }

  private logExplorationProgress(ExplorationAction outExplAction)
  {
    if (outExplAction instanceof TerminateExplorationAction)
      log.info(outExplAction.toString())
    else
      log.info(terminationCriterion.getLogMessage() + " " + outExplAction.toString())
  }


  @TypeChecked(SKIP)
  private ExplorationAction exploreForward(IGuiState guiState)
  {
    assert guiState != null
    assert !terminateExploration(guiState)
    assert !resetExploration(guiState)
    assert !backtrack(guiState)
    assert explorationCanMoveForwardOn(guiState)

    ExplorationAction outExplAction

    if (decideToDoForwardExplorationReset())
      outExplAction = newResetAppExplorationAction()
    else
    {
      boolean specialCaseApplied
      (specialCaseApplied, outExplAction) = specialCases.process(guiState, packageName)
      assert specialCaseApplied == (outExplAction != null)

      if (!specialCaseApplied)
      {
        outExplAction = widgetStrategy.decide(guiState)
      } else
      {
        // WISH hackish, should happen in org.droidmate.exploration.strategy.ExplorationStrategy.updateStrategyState
        // We do not include special exploration case in the reset counter
        forwardExplorationResetCounter++
      }
    }

    return outExplAction
  }


  private boolean decideToDoForwardExplorationReset()
  {
    if (forwardExplorationResetCounter == 1)
    {
      log.info("Forward exploration reset.")
      return true
    }
    return false
  }

  private boolean terminateExploration(IGuiState guiState)
  {
    assert guiState != null
    assert !(!firstCallToDecideFinished && lastActionWasToReset)

    if (terminationCriterion.met())
    {
      log.info("Terminating exploration: " + terminationCriterion.metReason())
      return true
    }

    // WISH if !explorationCanMoveForwardOn(guiState) after launch main activity, try again, but with longer wait delay.

    if (!explorationCanMoveForwardOn(guiState) && (!firstCallToDecideFinished || lastActionWasToReset))
    {
      String guiStateMsgPart = !firstCallToDecideFinished ? "Initial GUI state" : "GUI state after reset"

      // This case is observed when e.g. the app shows empty screen at startup.
      if (!guiState.belongsToApp(packageName))
        log.info("Terminating exploration: $guiStateMsgPart doesn't belong to the app. The GUI state: $guiState")

      // This case is observed when e.g. the app has nonstandard GUI, e.g. game native interface.
      // Also when all widgets have been blacklisted because they e.g. crash the app.
      else if (!hasActionableWidgets(guiState))
        log.info("Terminating exploration: $guiStateMsgPart doesn't contain actionable widgets. The GUI state: $guiState")

      else
        throw new UnexpectedIfElseFallthroughError()

      return true
    }

    return false
  }

  private boolean resetExploration(IGuiState guiState)
  {
    assert guiState != null
    assert !terminateExploration(guiState)
    assert (!firstCallToDecideFinished).implies(explorationCanMoveForwardOn(guiState))
    assert lastActionWasToReset.implies(explorationCanMoveForwardOn(guiState))

    if (explorationCanMoveForwardOn(guiState))
    {
      return false
    } else
    {
      assert firstCallToDecideFinished
      assert !lastActionWasToReset
      assert !explorationCanMoveForwardOn(guiState)
      return true
    }
  }

  private boolean backtrack(IGuiState guiState)
  {
    assert guiState != null
    assert !terminateExploration(guiState)
    assert !resetExploration(guiState)
    /* As  right now we never backtrack and backtracking is the last possibility to do something if exploration cannot move
    forward, thus we have this precondition. If backtracking will have some implementation, then it will handle some cases which
    are right now handled by terminateExploration and resetExploration, and this precondition will no longer hold.
     */
    assert explorationCanMoveForwardOn(guiState)

    // Placeholder for possible future functionality.

    assert explorationCanMoveForwardOn(guiState)
    return false
  }

  private boolean explorationCanMoveForwardOn(IGuiState guiState)
  {
    return guiState.belongsToApp(packageName) && hasActionableWidgets(guiState)
  }

  private boolean hasActionableWidgets(IGuiState guiState)
  {
    return (guiState.widgets.size() > 0) &&
      guiState.widgets.any {
        it.canBeActedUpon() && !allWidgetsBlackListed
      }
  }

  private void updateState(ExplorationAction action, boolean exploredForward)
  {
    assert action != null

    if (!firstCallToDecideFinished)
      firstCallToDecideFinished = true

    terminationCriterion.updateState()

    boolean currentActionIsToReset = action instanceof ResetAppExplorationAction

    if (exploredForward)
    {
      if (resetEveryNthExplorationForward > 0)
      {

        forwardExplorationResetCounter--
        assert forwardExplorationResetCounter >= 0

        if (forwardExplorationResetCounter == 0)
        {
          assert currentActionIsToReset
          forwardExplorationResetCounter = resetEveryNthExplorationForward
        }

        assert forwardExplorationResetCounter >= 1
      }
    } else if (currentActionIsToReset)
      forwardExplorationResetCounter = resetEveryNthExplorationForward

    lastActionWasToReset = currentActionIsToReset
  }

  public static ExplorationStrategy build(String appPackageName, Configuration cfg)
  {
    IWidgetStrategy widgetStrategy = new WidgetStrategy(appPackageName, cfg.randomSeed, cfg.alwaysClickFirstWidget, cfg.widgetIndexes)
    ITerminationCriterion terminationCriterion = new TerminationCriterion(cfg, cfg.timeLimit, Ticker.systemTicker())
    IForwardExplorationSpecialCases specialCases = new ForwardExplorationSpecialCases()
    return new ExplorationStrategy(appPackageName, cfg.resetEveryNthExplorationForward, widgetStrategy, terminationCriterion, specialCases)
  }
}
