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

import groovy.transform.Canonical
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.uiautomator_daemon.guimodel.GuiAction

@Canonical
class ClickGuiAction extends AndroidDeviceAction
{
  GuiAction guiAction

  @Override
  public String toString()
  {
    return "${this.class.simpleName}{${guiAction.toString()}}"
  }

  Widget getSingleMatchingWidget(Set<Widget> widgets)
  {
    int x = this.guiAction.clickXCoor
    int y = this.guiAction.clickYCoor
    assert x != null
    assert y != null

    Set<Widget> matchedWidgets = widgets.findAll {it.bounds.contains(x, y)}

    assert matchedWidgets.size() >= 1: "Expected to match at least one widget to coordinates $x $y"
    assert matchedWidgets.size() <= 1: "Expected to match at most one widget to coordinates $x $y. " +
      "Instead matched ${matchedWidgets.size()} widgets. " +
      "The matched widgets bounds:\n" +
      matchedWidgets.collect {it.boundsString}.join("\n")

    return matchedWidgets[0]
  }

}
