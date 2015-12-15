// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import groovy.transform.TypeChecked
import org.droidmate.common.exploration.datatypes.Widget

import java.awt.*

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.test_base.DroidmateGroovyTestCase.fixtures
import static org.droidmate.test_base.FilesystemTestFixtures.apkFixture_simple_packageName

@TypeChecked(SKIP)
class UiautomatorWindowDumpTestHelper
{

  static final Dimension deviceDimensionsForTesting_Nexus7y2012vert = new Dimension(800, 1205)

  //region Fixture dumps

  public static UiautomatorWindowDump newNullWindowDump()
  {
    return new UiautomatorWindowDump(null, deviceDimensionsForTesting_Nexus7y2012vert)
  }

  public static UiautomatorWindowDump newEmptyWindowDump()
  {
    return new UiautomatorWindowDump("", deviceDimensionsForTesting_Nexus7y2012vert)
  }

  public static UiautomatorWindowDump newEmptyActivityWindowDump()
  {
    return new UiautomatorWindowDump(fixtures.windowDumps.f_tsa_emptyAct, deviceDimensionsForTesting_Nexus7y2012vert)
  }

  public static UiautomatorWindowDump newAppHasStoppedDialogWindowDump()
  {
    return new UiautomatorWindowDump(fixtures.windowDumps.f_app_stopped_dialogbox, deviceDimensionsForTesting_Nexus7y2012vert)
  }

  public static UiautomatorWindowDump newAppHasStoppedDialogOKDisabledWindowDump()
  {
    return new UiautomatorWindowDump(fixtures.windowDumps.f_app_stopped_OK_disabled, deviceDimensionsForTesting_Nexus7y2012vert)
  }


  public static UiautomatorWindowDump newCompleteActionUsingWindowDump()
  {
    return new UiautomatorWindowDump(fixtures.windowDumps.f_complActUsing_dialogbox, deviceDimensionsForTesting_Nexus7y2012vert)
  }


  public static UiautomatorWindowDump newHomeScreenWindowDump(String id = null)
  {
    return new UiautomatorWindowDump(fixtures.windowDumps.f_nexus7_home_screen, deviceDimensionsForTesting_Nexus7y2012vert, id)
  }


  public static UiautomatorWindowDump newAppOutOfScopeWindowDump(String id = null)
  {
    return new UiautomatorWindowDump(fixtures.windowDumps.f_chrome_offline, deviceDimensionsForTesting_Nexus7y2012vert, id)
  }

  //endregion Fixture dumps


  public static UiautomatorWindowDump newWindowDump(String windowHierarchyDump)
  {
    return new UiautomatorWindowDump(windowHierarchyDump, deviceDimensionsForTesting_Nexus7y2012vert)
  }


  @SuppressWarnings("GroovyUnusedDeclaration")
  public static UiautomatorWindowDump newEmptyActivityWithPackageWindowDump(String appPackageName)
  {
    def payload = ""
    skeletonWithPayload(topNode(appPackageName, payload))
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  public static UiautomatorWindowDump new1ButtonWithPackageWindowDump(String appPackageName)
  {
    skeletonWithPayload(defaultButtonDump(appPackageName))
  }

  private static UiautomatorWindowDump skeletonWithPayload(String payload, String id = null)
  {
    new UiautomatorWindowDump(createDumpSkeleton(payload), deviceDimensionsForTesting_Nexus7y2012vert, id)
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
  // WISH deprecated as well as calling methods. Instead, use org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper.dump
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
  public static String rectShortString(Rectangle r)
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
