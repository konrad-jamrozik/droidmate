// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import org.droidmate.exceptions.ForbiddenOperationError
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

class MissingDeviceLogs implements IDeviceLogs, Serializable
{
  private static final long serialVersionUID = 1

  @Override
  boolean getContainsMonitorInitMsgs()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  LocalDateTime getMonitorInitTime()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  LocalDateTime getMonitorInitTimeOrNull()
  {
    return null
  }

  @Override
  List<ITimeFormattedLogcatMessage> getInstrumentationMsgs()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  List<ITimeFormattedLogcatMessage> getInstrumentationMsgsOrNull()
  {
    return null
  }


  @Override
  List<IApiLogcatMessage> getApiLogs()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  List<IApiLogcatMessage> getApiLogsOrEmpty()
  {
    return []
  }

  @Override
  boolean getReadAnyApiLogsSuccessfully()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  void setMonitorInitTime(LocalDateTime time)
  {
    throw new ForbiddenOperationError()
  }

  @Override
  void setInstrumentationMsgs(List<ITimeFormattedLogcatMessage> messages)
  {
    throw new ForbiddenOperationError()
  }

  @Override
  void setApiLogs(List<IApiLogcatMessage> apiLogs)
  {
    throw new ForbiddenOperationError()
  }

  @Override
  public String toString()
  {
    return "N/A (lack of ${IDeviceLogs.class.simpleName})"
  }

}
