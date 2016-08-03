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

package org.droidmate.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.filter.Filter
import org.slf4j.LoggerFactory

class LogbackAppenders
{
  static final String appender_stdout = "appender_STDOUT"
  static final String appender_stderr = "appender_STDERR"

  public static ArrayList<String> stdStreamsAppenders()
  {
    return [appender_stdout, appender_stderr]
  }

  public static void setThresholdLevelOfStdStreamsAppenders(Level level)
  {
    getStdStreamsAppenders().each {Appender<ILoggingEvent> it -> changeThresholdLevelOfFirstFilter(it, level)}
  }

  protected static List<Appender<ILoggingEvent>> getStdStreamsAppenders()
  {
    Logger log = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
    List<Appender<ILoggingEvent>> appenders = log.iteratorForAppenders().findAll {Appender<ILoggingEvent> appender ->
      appender.name in stdStreamsAppenders()
    }.asList()
    return appenders
  }

  // Adapted from http://groovy.codehaus.org/JN3515-Interception
  // WARNING: possibly (not sure) this doesn't work on log methods residing in @Memoized method. Dunno why, AST transformation magic interferes?
  protected static void changeThresholdLevelOfFirstFilter(Appender<ILoggingEvent> appender, Level newLevel)
  {

    List<Filter<ILoggingEvent>> filters = appender.copyOfAttachedFiltersList

    ThresholdFilter thresholdFilter = filters[0] as ThresholdFilter
    thresholdFilter.setLevel(newLevel.toString())

    appender.clearAllFilters()
    filters.each {Filter<ILoggingEvent> it -> appender.addFilter(it)}
  }
}
