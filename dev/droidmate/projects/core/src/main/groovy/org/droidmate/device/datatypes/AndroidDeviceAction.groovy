// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common_android.guimodel.GuiAction

import java.awt.*

abstract class AndroidDeviceAction implements IAndroidDeviceAction
{
  public static AdbClearPackageAction newClearPackageDeviceAction(String appPackageName)
  {
    return new AdbClearPackageAction(appPackageName)
  }

  public static LaunchMainActivityDeviceAction newLaunchActivityDeviceAction(String appLaunchableActivityComponentName)
  {
    return new LaunchMainActivityDeviceAction(appLaunchableActivityComponentName)
  }


  public static ClickGuiAction newClickGuiDeviceAction(Widget clickedWidget, boolean longClick = false)
  {
    newClickGuiDeviceAction(clickedWidget.clickPoint, longClick)
  }

  public static ClickGuiAction newClickGuiDeviceAction(Point p, boolean longClick = false)
  {
    newClickGuiDeviceAction(p.x as int, p.y as int, longClick)
  }

  public static ClickGuiAction newClickGuiDeviceAction(int clickX, int clickY, boolean longClick = false)
  {
    return new ClickGuiAction(new GuiAction(clickX, clickY, longClick))
  }

  public static ClickGuiAction newEnterTextDeviceAction(String resourceId, String textToEnter)
  {
    return new ClickGuiAction(GuiAction.createEnterTextGuiAction(resourceId, textToEnter))
  }


  public static ClickGuiAction newPressBackDeviceAction()
  {
    return new ClickGuiAction(GuiAction.createPressBackGuiAction())
  }

  public static ClickGuiAction newPressHomeDeviceAction()
  {
    return new ClickGuiAction(GuiAction.createPressHomeGuiAction())
  }

  public static ClickGuiAction newTurnWifiOnDeviceAction()
  {
    return new ClickGuiAction(GuiAction.createTurnWifiOnGuiAction())
  }


}
