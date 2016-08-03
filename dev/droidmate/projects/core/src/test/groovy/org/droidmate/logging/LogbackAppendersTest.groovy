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
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import groovy.transform.TypeChecked
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.logging.LogbackAppenders.changeThresholdLevelOfFirstFilter
import static org.droidmate.logging.LogbackAppenders.getStdStreamsAppenders

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class LogbackAppendersTest
{

  @TypeChecked(SKIP)
  @Test
  void "Changes threshold level of first filter"()
  {
    List<Appender<ILoggingEvent>> appenders = getStdStreamsAppenders()

    // Copy filters to be restored after test
    Map filters = appenders.collectEntries {[it, it.copyOfAttachedFiltersList]}

    appenders.each {Appender<ILoggingEvent> appender ->
      assertFirstFilterIsThresholdAndRepliesToTraceWith(appender, FilterReply.DENY)

      // Act
      changeThresholdLevelOfFirstFilter(appender, Level.TRACE)
    }

    appenders.each {Appender<ILoggingEvent> appender ->
      assertFirstFilterIsThresholdAndRepliesToTraceWith(appender, FilterReply.NEUTRAL)
    }

    // Restore the filters
    appenders.each {
      it.clearAllFilters()
      filters[it].each {filter -> it.addFilter(filter)}
    }
  }

  //region Helper methods
  private
  static void assertFirstFilterIsThresholdAndRepliesToTraceWith(Appender<ILoggingEvent> appender, FilterReply expectedReply)
  {
    List<Filter<ILoggingEvent>> filters = appender.copyOfAttachedFiltersList

    assert filters[0] instanceof ThresholdFilter
    ThresholdFilter thresholdFilter = filters[0] as ThresholdFilter

    FilterReply filterReply = thresholdFilter.decide([getLevel: {Level.TRACE}] as ILoggingEvent)
    assert filterReply == expectedReply
  }
  //endregion

}
