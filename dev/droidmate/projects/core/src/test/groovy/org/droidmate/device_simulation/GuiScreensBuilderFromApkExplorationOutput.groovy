// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.TimestampedExplorationAction
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.*

@Deprecated
class GuiScreensBuilderFromApkExplorationOutput implements IGuiScreensBuilder
{

  private final IApkExplorationOutput out

  GuiScreensBuilderFromApkExplorationOutput(IApkExplorationOutput out)
  {
    this.out = out
  }

  @Override
  public List<IGuiScreen> build()
  {
    return buildGuiScreens(out)
  }


  private List<IGuiScreen> buildGuiScreens(IApkExplorationOutput out)
  {
    out?.verifyCompletedDataIntegrity()

    List<IGuiScreen> guiScreens = out.guiSnapshots.collect {
      assert (it.id in GuiScreen.reservedIds) == !(it.guiState.belongsToApp(out.appPackageName))
      return new GuiScreen(it)
    }

    // Remove duplicate representations of the GuiScreens.
    guiScreens.unique(true) {it.id}

    // Ensure the set of GuiScreens contains home screen.
    if (!(guiScreens*.id.any {it == GuiScreen.idHome}))
    {
      def home = new GuiScreen(GuiScreen.idHome)
      home.buildInternals()
      guiScreens += home
    }

    // Ensure the set of GuiScreens contains chrome screen.
    if (!(guiScreens*.id.any {it == GuiScreen.idChrome}))
    {
      def chrome = new GuiScreen(GuiScreen.idChrome)
      chrome.buildInternals()
      guiScreens += chrome
    }

    // Obtain references to special Screens.
    IGuiScreen home = guiScreens.findSingle {it.id == GuiScreen.idHome}
    IGuiScreen main = guiScreens[0]
    assert !(main.id in GuiScreen.reservedIds)
    assert main.guiSnapshot.guiState.belongsToApp(out.appPackageName)

    guiScreens.each {
      it.addHomeScreenReference(home)
      it.addMainScreenReference(main)
    }

    out.actions.eachWithIndex {TimestampedExplorationAction action, int i ->

      ExplorationAction explAction = action.explorationAction

      switch (explAction.class)
      {
        case ResetAppExplorationAction:
          // Do not any transition: all GuiScreens already know how to transition on device actions resulting from
          // this exploration action.
          break

        case WidgetExplorationAction:
          addWidgetTransition(guiScreens, i, (explAction as WidgetExplorationAction).widget)
          break

        case EnterTextExplorationAction:
          addWidgetTransition(guiScreens, i, (explAction as EnterTextExplorationAction).widget)
          break

        case TerminateExplorationAction:
          assert i == out.actions.size() - 1
          // Do not add any transition: all GuiScreens already know how to transition on device actions resulting from
          // this exploration action.
          break

        default:
          new UnexpectedIfElseFallthroughError(
            "Unsupported ExplorationAction class while extracting transitions from IApkExplorationOutput. " +
              "The unsupported class: ${explAction.class}")
      }

    }

    guiScreens.each {it.verify()}
    return guiScreens

  }

  private void addWidgetTransition(List<IGuiScreen> guiScreens, int i, Widget widget)
  {
    assert i > 0
    IGuiScreen sourceScreen = guiScreens.findSingle {out.guiSnapshots[i - 1].id == it.id}
    IGuiScreen targetScreen = guiScreens.findSingle {out.guiSnapshots[i].id == it.id}
    sourceScreen.addWidgetTransition(widget.id, targetScreen, /* ignoreDuplicates */ true)
  }
}

