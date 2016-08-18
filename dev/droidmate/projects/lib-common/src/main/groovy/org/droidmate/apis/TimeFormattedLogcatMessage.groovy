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

package org.droidmate.apis

import groovy.transform.Canonical

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**

 *
 * <p>
 * Represents a string that was obtained by reading a line of logcat output formatted with "-v time"
 * </p><p>
 *
 * Reference: http://developer.android.com/tools/debugging/debugging-log.html#outputFormat
 * </p>
 */
@Canonical
class TimeFormattedLogcatMessage implements ITimeFormattedLogcatMessage, Serializable
{
  private static final long serialVersionUID = 1

  public static LocalDateTime assumedDate = LocalDateTime.now()

  private static DateTimeFormatter rawMessageTimeFormatter      = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS")
  private static DateTimeFormatter withYearMessageTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

  final LocalDateTime time
  final String        level
  final String        tag
  final String        pidString
  final String        messagePayload

  static ITimeFormattedLogcatMessage from(LocalDateTime time, String level, String tag, String pidString, String messagePayload)
  {
    return new TimeFormattedLogcatMessage(time, level, tag, pidString, messagePayload)
  }

  /**
   * <p>
   * Parses logcat message in format of IntelliJ's "Android" logcat window.
   *
   * </p><p>
   * Example logs taken from IntelliJ IDEA "Android" logcat window:
   *
   * <pre>
   * <code>02-04 21:54:52.600  19183-19183/? V/UIEventsToLogcat﹕ text: Video qualityAutomatic package: com.snapchat.android class: android.widget.LinearLayout type: TYPE_VIEW_CLICKED time: 10479703
   * 02-05 12:39:39.261  26443-26443/? I/Instrumentation﹕ Redirected org.apache.http.impl.client.AbstractHttpClient-><init>
   * 02-05 12:39:39.261  26443-26443/? I/Monitor﹕ Monitor initialized for package com.snapchat.android
   * 02-05 12:39:39.511  26443-26443/? I/Monitored_API_method_call﹕ objCls: android.net.ConnectivityManager mthd: getActiveNetworkInfo retCls: android.net.NetworkInfo params:  stacktrace: long_stack_trace

   * </code></pre>
   * </p>
   */
  static ITimeFormattedLogcatMessage fromIntelliJ(String logcatMessage)
  {
    assert false: "Not yet implemented!"
    return null
  }

  /**
   * <p>
   * Parses {@code logcatMessage} being in standard Android format.
   *
   * </p><p>
   * Example logs taken from adb logcat output:
   *
   * <pre>
   * <code>12-22 20:30:01.440 I/PMBA    (  483): Previous metadata 937116 mismatch vs 1227136 - rewriting
   * 12-22 20:30:01.500 D/BackupManagerService(  483): Now staging backup of com.android.vending
   * 12-22 20:30:34.190 D/dalvikvm( 1537): GC_CONCURRENT freed 390K, 5% free 9132K/9572K, paused 17ms+5ms, total 65ms</code></pre>
   *
   * </p><p>
   * Reference: http://developer.android.com/tools/debugging/debugging-log.html#outputFormat
   *
   * </p>
   */
  static ITimeFormattedLogcatMessage from(String logcatMessage)
  {
    assert logcatMessage?.size() > 0
    assert (logcatMessage =~ /\d\d-\d\d /).find():
      "Failed parsing logcat message. Was expecting to see \"MM-DD \" at the beginning, " +
        "where M denotes Month digit and D denotes day-of-month digit.\n" +
        "The offending logcat message:\n\n$logcatMessage\n\n"

    String monthAndDay, hourMinutesSecondsMillis, notYetParsedMessagePart
    (monthAndDay, hourMinutesSecondsMillis, notYetParsedMessagePart) = logcatMessage.split(" ", 3)

    String year = String.valueOf(assumedDate.year)

    LocalDateTime time = LocalDateTime.parse("$year-$monthAndDay $hourMinutesSecondsMillis", withYearMessageTimeFormatter)

    String logLevel
    (logLevel, notYetParsedMessagePart) = notYetParsedMessagePart.split("/", 2)

    String logTag
    // On this split we make an implicit assumption that the '(' character (i.e. left parenthesis) doesn't appear in logTag.
    (logTag, notYetParsedMessagePart) = notYetParsedMessagePart.split("\\(", 2)

    String pidString
    (pidString, notYetParsedMessagePart) = notYetParsedMessagePart.split("\\)", 2)

    assert notYetParsedMessagePart.startsWith(": ")
    String messagePayload = notYetParsedMessagePart.drop(2)

    [logLevel, logTag, pidString].each { assert it?.size() > 0 }
    return new TimeFormattedLogcatMessage(time, logLevel, logTag, pidString, messagePayload)
  }

  @Override
  String toLogcatMessageString()
  {
    return "${time.format(rawMessageTimeFormatter)} $level/$tag($pidString): $messagePayload"
  }


  @Override
  public String toString()
  {
    String out = toLogcatMessageString()
    if (out.size() <= 256)
      return out
    else
      return out.substring(0, 256) + "... (truncated to 256 chars)"
  }
}
