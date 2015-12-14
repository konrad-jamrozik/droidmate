// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import com.google.common.base.MoreObjects
import groovy.util.logging.Slf4j
import org.droidmate.exceptions.ForbiddenOperationError
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

@Slf4j
class DeviceLogs implements IDeviceLogs, Serializable
{
  private static final long serialVersionUID = 1

  LocalDateTime monitorInitTime = null

  List<ITimeFormattedLogcatMessage> instrumentationMsgs = null

  List<IApiLogcatMessage> apiLogs = null

  @Override
  boolean getContainsMonitorInitMsgs()
  {
    return monitorInitTime != null && instrumentationMsgs != null
  }

  @Override
  LocalDateTime getMonitorInitTime()
  {
    if (!containsMonitorInitMsgs)
    {
      assert monitorInitTime == null
      throw new ForbiddenOperationError()
    }

    assert monitorInitTime != null
    return monitorInitTime
  }

  @Override
  List<ITimeFormattedLogcatMessage> getInstrumentationMsgs()
  {
    if (!containsMonitorInitMsgs)
    {
      assert instrumentationMsgs == null
      throw new ForbiddenOperationError()
    }

    assert instrumentationMsgs != null
    return instrumentationMsgs
  }


  @Override
  LocalDateTime getMonitorInitTimeOrNull()
  {
    assert (!containsMonitorInitMsgs).implies(monitorInitTime == null)

    return monitorInitTime
  }

  @Override
  List<ITimeFormattedLogcatMessage> getInstrumentationMsgsOrNull()
  {
    assert (!containsMonitorInitMsgs).implies(instrumentationMsgs == null)
    return instrumentationMsgs
  }

  /**
   * Might return null! For safe variant, use {@link #getApiLogsOrEmpty}
   * @return
   */
  @Override
  List<IApiLogcatMessage> getApiLogs()
  {
    return apiLogs
  }

  @Override
  List<IApiLogcatMessage> getApiLogsOrEmpty()
  {
    if (!readAnyApiLogsSuccessfully)
      return []

    return apiLogs
  }

  @Override
  boolean getReadAnyApiLogsSuccessfully()
  {
    return apiLogs != null
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
      .add("apiLogs#", this.readAnyApiLogsSuccessfully ? apiLogs.size() : "N/A")
      .add("containsMonitorInitMsgs", containsMonitorInitMsgs)
      .toString()
  }

}

