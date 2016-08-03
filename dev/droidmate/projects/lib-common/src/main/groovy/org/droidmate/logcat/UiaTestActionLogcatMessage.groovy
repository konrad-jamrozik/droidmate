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

package org.droidmate.logcat

import groovy.transform.Canonical
import org.droidmate.common.exploration.datatypes.Widget

import java.util.regex.Matcher

import static org.droidmate.common.Assert.assertRegexMatches

@Canonical
class UiaTestActionLogcatMessage implements IUiaTestActionLogcatMessage
{
  @Delegate
  ITimeFormattedLogcatMessage message

  String actionType

  String comment

  Widget widget

  public static IUiaTestActionLogcatMessage from(ITimeFormattedLogcatMessage logcatMessage)
  {
    assert logcatMessage?.messagePayload?.size() > 0

    def (String actionType, String comment, Widget widget) = actionTypeCommentAndWidgetFrom(logcatMessage.messagePayload)

    return new UiaTestActionLogcatMessage(logcatMessage, actionType, comment, widget)
  }

  // !!! DUPLICATION WARNING !!! with org.droidmate.uia_manual_test_cases.TestCases#logActionInfo
  static List actionTypeCommentAndWidgetFrom(String logcatMessagePayload)
  {
    assert logcatMessagePayload?.size() > 0

    Matcher exploratoryMatcher = logcatMessagePayload =~ "actionType: (.*) comment: (.*) index: (.*)"
    if (!exploratoryMatcher.matches())
      return matchNonWidgetAction(logcatMessagePayload)
    else
      return matchWidgetAction(logcatMessagePayload)
  }

  private static List matchWidgetAction(String logcatMessagePayload)
  {
    Matcher m = logcatMessagePayload =~
      "actionType: (.*) " +
      "comment: (.*) " +
      "index: (.*) " +
      "text: (.*) " +
      "resourceId: (.*) " +
      "className: (.*) " +
      "packageName: (.*) " +
      "contentDesc: (.*) " +
      "checkable: (.*) " +
      "checked: (.*) " +
      "clickable: (.*) " +
      "enabled: (.*) " +
      "focusable: (.*) " +
      "focused: (.*) " +
      "scrollable: (.*) " +
      "longClickable: (.*) " +
      "password: (.*) " +
      "selected: (.*) " +
      "bounds: (.*)"

    assertRegexMatches(logcatMessagePayload, m)
    List<String> matchedParts = (m[0] as List)
    assert matchedParts.size() == 20
    matchedParts.remove(0) // drops the field with the entire string matched
    String actionType = matchedParts[0]
    matchedParts.remove(0)
    String comment = matchedParts[0]
    matchedParts.remove(0)

    Widget widget = new Widget(
      // @formatter:off
      index         : matchedParts[0] as int,
      text          : matchedParts[1],
      resourceId    : matchedParts[2],
      className     : matchedParts[3],
      packageName   : matchedParts[4],
      contentDesc   : matchedParts[5],
      checkable     : matchedParts[6] == "true",
      checked       : matchedParts[7] == "true",
      clickable     : matchedParts[8] == "true",
      enabled       : matchedParts[9] == "true",
      focusable     : matchedParts[10] == "true",
      focused       : matchedParts[11] == "true",
      scrollable    : matchedParts[12] == "true",
      longClickable : matchedParts[13] == "true",
      password      : matchedParts[14] == "true",
      selected      : matchedParts[15] == "true",
      bounds        : Widget.parseBounds(matchedParts[16]),
      // @formatter:on
    )

    return [actionType, comment, widget]
  }

  private static ArrayList<String> matchNonWidgetAction(String logcatMessagePayload)
  {
    Matcher m = logcatMessagePayload =~ "actionType: (.*) comment: (.*)"
    assertRegexMatches(logcatMessagePayload, m)
    List<String> matchedParts = (m[0] as List)
    assert matchedParts.size() == 3
    matchedParts.remove(0) // drops the field with the entire string matched
    String actionType = matchedParts[0]
    matchedParts.remove(0)
    String comment = matchedParts[0]
    return [actionType, comment, null]
  }
}
