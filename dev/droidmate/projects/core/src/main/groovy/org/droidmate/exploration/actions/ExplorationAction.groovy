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
  protected Boolean runtimePermission = false

  @Override
  String toString()
  {
    use(TextUtilsCategory) {
      return "ExplAct ${toShortString()}".wrapWith("<>")
    }
  }

  Boolean isEndorseRuntimePermission()
  {
    return runtimePermission
  }

  abstract String toShortString()

  String toTabulatedString()
  {
    return toShortString()
  }

   static ResetAppExplorationAction newResetAppExplorationAction(boolean isFirst = false)
  {
    return new ResetAppExplorationAction(isFirst)
  }

  static TerminateExplorationAction newTerminateExplorationAction()
  {
    return new TerminateExplorationAction()
  }
  
  static WidgetExplorationAction newWidgetExplorationAction(Widget widget, int delay)
  {
    return new WidgetExplorationAction(widget: widget, runtimePermission: false, delay: delay)
  }

  static WidgetExplorationAction newWidgetExplorationAction(Widget widget, boolean longClick = false)
  {
    assert widget != null

    return new WidgetExplorationAction(widget: widget, longClick: longClick)
  }

  static WidgetExplorationAction newIgnoreActionForTerminationWidgetExplorationAction(Widget widget, boolean longClick = false)
  {
    assert widget != null

    return new WidgetExplorationAction(widget: widget, runtimePermission: true, longClick: longClick)
  }

  static EnterTextExplorationAction newEnterTextExplorationAction(String textToEnter, String resourceId)
  {
    return new EnterTextExplorationAction(textToEnter, new Widget(resourceId: resourceId))
  }

  static EnterTextExplorationAction newEnterTextExplorationAction(String textToEnter, Widget widget)
  {
    return new EnterTextExplorationAction(textToEnter, widget)
  }


  static PressBackExplorationAction newPressBackExplorationAction()
  {
    return new PressBackExplorationAction()
  }


}
