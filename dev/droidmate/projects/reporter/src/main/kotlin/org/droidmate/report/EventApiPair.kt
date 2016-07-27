// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.*
import org.droidmate.logcat.IApiLogcatMessage

class EventApiPair(actRes: RunnableExplorationActionWithResult, apiLog: IApiLogcatMessage) {

  val pair: Pair<String, IApiLogcatMessage>
  operator fun component1() = pair.first
  operator fun component2() = pair.second
  
  init { pair = build(actRes, apiLog) }

  private fun build(actRes: RunnableExplorationActionWithResult, apiLog: IApiLogcatMessage): Pair<String, IApiLogcatMessage> {

    fun extractEvent(action: ExplorationAction, thread: Int): String {

      fun extractWidgetEventString(action: ExplorationAction): String {
        require(action is WidgetExplorationAction || action is EnterTextExplorationAction)
        val w: Widget
        val prefix: String
        when (action) {
          is WidgetExplorationAction -> {
            w = action.widget
            prefix = (if (action.isLongClick) "l-click:" else "click") + ":"
          }
          is EnterTextExplorationAction -> {
            w = action.widget
            prefix = "enterText:"
          }
          else -> throw UnexpectedIfElseFallthroughError()
        }
        checkNotNull(w)
        val widgetString =
          if (w.resourceId?.length ?: 0 > 0)
            "[res:${w.strippedResourceId}]"
          else if (w?.contentDesc?.length ?: 0 > 0)
            "[dsc:${w.contentDesc}]"
          else if (w?.text?.length ?: 0 > 0)
            "[txt:${w.text}]"
          else ""

        if (widgetString.isEmpty())
          return "unlabeled"
        else
          return prefix + widgetString
      }

      return when (action) {
        is ResetAppExplorationAction, is TerminateExplorationAction -> "<reset>"
        is WidgetExplorationAction, is EnterTextExplorationAction ->
          if (thread == 1) extractWidgetEventString(action) else "background"
        is PressBackExplorationAction -> "<press back>"
      // Kotlin BUG: this else should not be necessary. Groovy's fault?
      // Maybe this is the reason: https://discuss.kotlinlang.org/t/algebraic-data-types-are-not-exhaustive/1857/4
        else -> throw UnexpectedIfElseFallthroughError()
      }
    }

    return Pair(
      extractEvent(action = actRes.action.base, thread = apiLog.threadId.toInt()),
      apiLog)
  }

  val uniqueString: String get() = pair.first + pair.second.uniqueString

  
}