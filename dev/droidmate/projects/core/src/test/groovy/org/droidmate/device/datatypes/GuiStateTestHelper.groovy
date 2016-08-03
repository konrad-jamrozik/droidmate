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

package org.droidmate.device.datatypes

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.model.DeviceModel

import static WidgetTestHelper.newTopLevelWidget
import static org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper.*
import static org.droidmate.test_base.FilesystemTestFixtures.apkFixture_simple_packageName

class GuiStateTestHelper
{

  public static GuiState newEmptyGuiState(String appPackageName = apkFixture_simple_packageName, String id = null)
  {
    return new GuiState(appPackageName, id, [] as List<Widget>, DeviceModel.buildDefault().androidLauncherPackageName)
  }

  public static GuiState newGuiStateWithTopLevelNodeOnly(String appPackageName = apkFixture_simple_packageName, String id = null)
  {
    return new GuiState(appPackageName, id, [newTopLevelWidget(appPackageName)] as List<Widget>, DeviceModel.buildDefault().androidLauncherPackageName)
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
      /* widgetIdPrefix */ guiStateId ?: getNextGuiStateName()),
      DeviceModel.buildDefault().androidLauncherPackageName)
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
