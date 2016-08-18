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

package org.droidmate.exploration.actions

import org.droidmate.device.datatypes.Widget
import org.droidmate.misc.TextUtilsCategory

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
