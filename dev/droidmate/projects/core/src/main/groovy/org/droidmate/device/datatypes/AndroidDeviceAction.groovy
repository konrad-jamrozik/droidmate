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

import org.droidmate.uiautomator_daemon.guimodel.GuiAction

import java.awt.*

abstract class AndroidDeviceAction implements IAndroidDeviceAction
{

  static ClickGuiAction newClickGuiDeviceAction(Widget clickedWidget, boolean longClick = false)
  {
    newClickGuiDeviceAction(clickedWidget.clickPoint, longClick)
  }

   static ClickGuiAction newClickGuiDeviceAction(Point p, boolean longClick = false)
  {
    newClickGuiDeviceAction(p.x as int, p.y as int, longClick)
  }

   static ClickGuiAction newClickGuiDeviceAction(int clickX, int clickY, boolean longClick = false)
  {
    return new ClickGuiAction(new GuiAction(clickX, clickY, longClick))
  }

   static ClickGuiAction newEnterTextDeviceAction(String resourceId, String textToEnter)
  {
    return new ClickGuiAction(GuiAction.createEnterTextGuiAction(resourceId, textToEnter))
  }


   static ClickGuiAction newPressBackDeviceAction()
  {
    return new ClickGuiAction(GuiAction.createPressBackGuiAction())
  }

   static ClickGuiAction newPressHomeDeviceAction()
  {
    return new ClickGuiAction(GuiAction.createPressHomeGuiAction())
  }

   static ClickGuiAction newTurnWifiOnDeviceAction()
  {
    return new ClickGuiAction(GuiAction.createTurnWifiOnGuiAction())
  }

   static ClickGuiAction newLaunchAppDeviceAction(String iconLabel)
  {
    return new ClickGuiAction(GuiAction.createLaunchAppGuiAction(iconLabel))
  }


}
