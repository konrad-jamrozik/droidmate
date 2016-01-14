// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import groovy.transform.Canonical
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common_android.guimodel.GuiAction

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
