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
package org.droidmate.test_tools.device_simulation

import org.droidmate.device.datatypes.Widget
import org.droidmate.errors.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.*
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class GuiScreensBuilderFromApkExplorationOutput2 implements IGuiScreensBuilder
{

  private final IApkExplorationOutput2 out

  GuiScreensBuilderFromApkExplorationOutput2(IApkExplorationOutput2 out)
  {
    this.out = out
  }

  @Override
   List<IGuiScreen> build()
  {
    return buildGuiScreens(out)
  }


  private List<IGuiScreen> buildGuiScreens(IApkExplorationOutput2 out)
  {
    out?.verify()

    List<IGuiScreen> guiScreens = out.guiSnapshots.collect {
      assert (it.id in GuiScreen.reservedIds) == !(it.guiState.belongsToApp(out.packageName))
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
    assert main.guiSnapshot.guiState.belongsToApp(out.packageName)

    guiScreens.each {
      it.addHomeScreenReference(home)
      it.addMainScreenReference(main)
    }

    out.actRess.eachWithIndex {RunnableExplorationActionWithResult action, int i ->

      ExplorationAction explAction = action.action.base

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
          assert i == out.actRess.size() - 1
          // Do not add any transition: all GuiScreens already know how to transition on device actions resulting from
          // this exploration action.
          break

        default:
          new UnexpectedIfElseFallthroughError(
            "Unsupported ExplorationAction class while extracting transitions from IApkExplorationOutput2. " +
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

