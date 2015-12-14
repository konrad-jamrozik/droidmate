// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.exploration.actions.*

import java.time.LocalDateTime

@Deprecated
class TimestampedExplorationActionTestHelper
{
  public static TimestampedExplorationAction newTimestampedResetAppExplorationAction()
  {
    buildTimestampedExplorationAction(new ResetAppExplorationAction())
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  public static TimestampedExplorationAction newTimestampedTerminateExplorationAction()
  {
    buildTimestampedExplorationAction(new TerminateExplorationAction())
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  public static TimestampedExplorationAction newTimestampedWidgetExplorationAction(Widget widget)
  {
    buildTimestampedExplorationAction(new WidgetExplorationAction(widget))
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  public static TimestampedExplorationAction newTimestampedPressBackExplorationAction()
  {
    buildTimestampedExplorationAction(new PressBackExplorationAction())
  }

  private static TimestampedExplorationAction buildTimestampedExplorationAction(ExplorationAction explorationAction)
  {
    return TimestampedExplorationAction.from(explorationAction, LocalDateTime.now())
  }

}
