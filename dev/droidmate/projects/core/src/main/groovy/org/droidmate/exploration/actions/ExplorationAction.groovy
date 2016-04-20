// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.actions

import org.droidmate.common.TextUtilsCategory
import org.droidmate.common.exploration.datatypes.Widget

abstract class ExplorationAction implements Serializable
{

  private static final long serialVersionUID = 1
  protected Boolean runtimePermission = false;

  @Override
  String toString()
  {
    use(TextUtilsCategory) {
      return "ExplAct ${toShortString()}".wrapWith("<>")
    }
  }

  Boolean isEndorseRuntimePermission()
  {
    return runtimePermission;
  }

  abstract String toShortString()

  String toTabulatedString()
  {
    return toShortString()
  }

  public static ResetAppExplorationAction newResetAppExplorationAction(boolean isFirst = false)
  {
    return new ResetAppExplorationAction(isFirst)
  }

  public static TerminateExplorationAction newTerminateExplorationAction(HashMap cfg = [:])
  {
    assertConfig(cfg)
    return new TerminateExplorationAction(cfg)
  }

  static void assertConfig(HashMap cfg, List<String> keys = [])
  {
    // WISH this can be uncommented after the tests for the old code have been deleted
//    assert cfg.timestampFunc != null
    keys.each {assert cfg[it] != null}
    // WISH this can be uncommented after the tests for the old code have been deleted
//    assert cfg.keySet().size() == 1 + keys.size()
  }

  public static WidgetExplorationAction newWidgetExplorationAction(Widget widget, int delay)
  {
    return new WidgetExplorationAction(widget: widget, runtimePermission: false, delay: delay)
  }

  public static WidgetExplorationAction newWidgetExplorationAction(Widget widget, boolean longClick = false)
  {
    assert widget != null

    return new WidgetExplorationAction(widget: widget, longClick: longClick)
  }

  public static WidgetExplorationAction newIgnoreActionForTerminationWidgetExplorationAction(Widget widget, boolean longClick = false)
  {
    assert widget != null

    return new WidgetExplorationAction(widget: widget, runtimePermission: true, longClick: longClick)
  }

  public static WidgetExplorationAction newWidgetExplorationAction(HashMap cfg)
  {
    assertConfig(cfg, ["widget", "longClick"])
    return new WidgetExplorationAction(cfg)
  }

  public static EnterTextExplorationAction newEnterTextExplorationAction(String textToEnter, String resourceId)
  {
    return new EnterTextExplorationAction(textToEnter, new Widget(resourceId: resourceId))
  }

  public static EnterTextExplorationAction newEnterTextExplorationAction(String textToEnter, Widget widget)
  {
    return new EnterTextExplorationAction(textToEnter, widget)
  }


  public static PressBackExplorationAction newPressBackExplorationAction()
  {
    return new PressBackExplorationAction()
  }


}
