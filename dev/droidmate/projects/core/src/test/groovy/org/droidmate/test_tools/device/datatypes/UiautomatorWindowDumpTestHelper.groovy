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

package org.droidmate.test_tools.device.datatypes

import groovy.transform.TypeChecked
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.IGuiState
import org.droidmate.device.datatypes.UiautomatorWindowDump
import org.droidmate.device.datatypes.Widget
import org.droidmate.device.model.DeviceModel
import org.droidmate.device.model.IDeviceModel
import org.droidmate.tests.FixturesKt

import java.awt.*

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.test_tools.ApkFixtures.apkFixture_simple_packageName

@TypeChecked(SKIP)
class UiautomatorWindowDumpTestHelper
{
  private static final IDeviceModel deviceModel = DeviceModel.buildDefault()

  //region Fixture dumps

  static UiautomatorWindowDump newNullWindowDump()
  {
    return new UiautomatorWindowDump(null, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }

   static UiautomatorWindowDump newEmptyWindowDump()
  {
    return new UiautomatorWindowDump("", deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }

   static UiautomatorWindowDump newEmptyActivityWindowDump()
  {
    return new UiautomatorWindowDump(FixturesKt.windowDump_tsa_emptyAct, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }

   static UiautomatorWindowDump newAppHasStoppedDialogWindowDump()
  {
    return new UiautomatorWindowDump(FixturesKt.windowDump_app_stopped_dialogbox, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }

   static UiautomatorWindowDump newAppHasStoppedDialogOKDisabledWindowDump()
  {
    return new UiautomatorWindowDump(FixturesKt.windowDump_app_stopped_OK_disabled, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }

   static UiautomatorWindowDump newSelectAHomeAppWindowDump()
  {
    return new UiautomatorWindowDump(FixturesKt.windowDump_selectAHomeApp, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }



   static UiautomatorWindowDump newCompleteActionUsingWindowDump()
  {
    return new UiautomatorWindowDump(FixturesKt.windowDump_complActUsing_dialogbox, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }


   static UiautomatorWindowDump newHomeScreenWindowDump(String id = null)
  {
    return new UiautomatorWindowDump(FixturesKt.windowDump_nexus7_home_screen, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName, id)
  }


   static UiautomatorWindowDump newAppOutOfScopeWindowDump(String id = null)
  {
    return new UiautomatorWindowDump(FixturesKt.windowDump_chrome_offline, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName, id)
  }

  //endregion Fixture dumps


  static UiautomatorWindowDump newWindowDump(String windowHierarchyDump)
  {
    return new UiautomatorWindowDump(windowHierarchyDump, deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName)
  }


  @SuppressWarnings("GroovyUnusedDeclaration")
   static UiautomatorWindowDump newEmptyActivityWithPackageWindowDump(String appPackageName)
  {
    def payload = ""
    skeletonWithPayload(topNode(appPackageName, payload))
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
   static UiautomatorWindowDump new1ButtonWithPackageWindowDump(String appPackageName)
  {
    skeletonWithPayload(defaultButtonDump(appPackageName))
  }

  private static UiautomatorWindowDump skeletonWithPayload(String payload, String id = null)
  {
    new UiautomatorWindowDump(createDumpSkeleton(payload), deviceModel.getDeviceDisplayDimensionsForTesting(), deviceModel.androidLauncherPackageName, id)
  }

  static String createDumpSkeleton(String payload)
  {
    $/<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><hierarchy rotation="0">$payload</hierarchy>/$ as String
  }

  static String topNode(String appPackageName = apkFixture_simple_packageName, String payload)
  {
    $/<node index="0" text="" resource-id="" class="android.widget.FrameLayout" package="$appPackageName"
content-desc="" checkable="false" checked="false" clickable="false" enabled="true" focusable="false" focused="false"
scrollable="false" long-clickable="false" password="false" selected="false" bounds="[0,0][800,1205]">$payload</node>/$ as String
  }

  private static String defaultButtonDump(String packageName = apkFixture_simple_packageName)
  {
    String buttonBounds = createConstantButtonBounds()
    String buttonDump = createButtonDump(0, "dummyText", buttonBounds, packageName)
    return buttonDump
  }

  private static String createConstantButtonBounds()
  {
    int x = 10, y = 20, width = 100, height = 200
    String bounds = "[$x,$y][${x + width},${y + height}]"
    return bounds.toString()
  }

  static String dump(Widget w)
  {
    String idString = w.id != null ? "id=\"$w.id\"" : ""
    String out = $/<node
 index="$w.index"
 text="$w.text"
 resource-id="$w.resourceId"
 class="$w.className"
 package="$w.packageName"
 content-desc="$w.contentDesc"
 checkable="$w.checkable"
 checked="$w.checked"
 clickable="$w.clickable"
 enabled="$w.enabled"
 focusable="$w.focusable"
 focused="$w.focused"
 scrollable="$w.scrollable"
 long-clickable="$w.longClickable"
 password="$w.password"
 selected="$w.selected"
 bounds="${rectShortString(w.bounds)}"
 $idString
 />/$ as String
    return out
  }
  // WISH deprecated as well as calling methods. Instead, use org.droidmate.test.device.datatypes.UiautomatorWindowDumpTestHelper.dump
  static String createButtonDump(int index, String text, String bounds, String packageName = apkFixture_simple_packageName)
  {
    $/<node index="$index" text="$text" resource-id="dummy.package.ExampleApp:id/button_$text"
class="android.widget.Button" package="$packageName" content-desc="" checkable="false" checked="false"
clickable="true" enabled="true" focusable="true" focused="false" scrollable="false" long-clickable="false" password="false"
selected="false" bounds="$bounds"/>/$ as String
  }

  /**
   * Returns the same value as {@code android.graphics.Rect.toShortString (java.lang.StringBuilder)}
   */
  static String rectShortString(Rectangle r)
  {
    r.with {
      return "[${minX as int},${minY as int}][${maxX as int},${maxY as int}]"
    }
  }

  static IDeviceGuiSnapshot fromGuiState(IGuiState guiState)
  {
    return skeletonWithPayload(
      guiState.widgets.collect {dump(it)}.join("\n"), guiState.id)
  }
}
