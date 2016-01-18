// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import org.droidmate.common.exploration.datatypes.Widget

import static WidgetTestHelper.newTopLevelWidget
import static org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper.*
import static org.droidmate.test_base.FilesystemTestFixtures.apkFixture_simple_packageName

class GuiStateTestHelper
{

  public static GuiState newEmptyGuiState(String appPackageName = apkFixture_simple_packageName, String id = null)
  {
    return new GuiState(appPackageName, id, [] as List<Widget>)
  }

  public static GuiState newGuiStateWithTopLevelNodeOnly(String appPackageName = apkFixture_simple_packageName, String id = null)
  {
    return new GuiState(appPackageName, id, [newTopLevelWidget(appPackageName)] as List<Widget>)
  }


  public static GuiState newGuiStateWithDisabledWidgets(int widgetCount)
  {
    return newGuiStateWithWidgets(widgetCount, apkFixture_simple_packageName, false)
  }

  public static GuiState newGuiStateWithWidgets(
    int widgetCount,
    String packageName = apkFixture_simple_packageName,
    boolean enabled = true,
    String guiStateId = null,
    List<String> widgetIds = null)
  {
    assert widgetCount >= 0
    assert !(widgetCount == 0): "Widget count cannot be zero. To create GUI state without widgets, call newEmptyGuiState()"
    assert widgetIds == null || widgetIds.size() == widgetCount

    def gs = new GuiState(packageName, guiStateId, WidgetTestHelper.newWidgets(
      widgetCount,
      packageName,
      [
        idsList    : widgetIds,
        enabledList: [enabled] * widgetCount
      ],
      /* widgetIdPrefix */ guiStateId ?: getNextGuiStateName()))
    assert gs.widgets.every {it.packageName == gs.topNodePackageName}
    return gs
  }

  public static GuiState newAppHasStoppedGuiState()
  {
    return newAppHasStoppedDialogWindowDump().guiState
  }

  public static GuiState newCompleteActionUsingGuiState()
  {
    return newCompleteActionUsingWindowDump().guiState
  }


  public static GuiState newHomeScreenGuiState()
  {
    return newHomeScreenWindowDump().guiState

  }

  public static GuiState newOutOfAppScopeGuiState()
  {
    return newAppOutOfScopeWindowDump().guiState
  }

  static int nextGuiStateIndex = 0

  public static getNextGuiStateName()
  {
    nextGuiStateIndex++
    return "GS$nextGuiStateIndex"
  }
}
