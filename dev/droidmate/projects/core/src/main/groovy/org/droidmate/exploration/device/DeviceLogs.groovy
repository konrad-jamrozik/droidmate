// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
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

  // KJA to remove soon
  @Deprecated
  LocalDateTime monitorInitTime = null

  // KJA to remove soon
  List<ITimeFormattedLogcatMessage> instrumentationMsgs = null

  List<IApiLogcatMessage> apiLogs = null

  @Override
  boolean getContainsMonitorInitTime()
  {
    return monitorInitTime != null
  }

  @Override
  LocalDateTime getMonitorInitTime()
  {
    if (!containsMonitorInitTime)
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
    if (!containsMonitorInitTime)
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
    assert (!containsMonitorInitTime).implies(monitorInitTime == null)

    return monitorInitTime
  }

  @Override
  List<ITimeFormattedLogcatMessage> getInstrumentationMsgsOrNull()
  {
    assert (!containsMonitorInitTime).implies(instrumentationMsgs == null)
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
      .add("containsMonitorInitMsgs", containsMonitorInitTime)
      .toString()
  }

}

