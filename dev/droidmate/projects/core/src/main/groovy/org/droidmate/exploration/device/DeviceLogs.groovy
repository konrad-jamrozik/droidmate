// Copyright (c) 2012-2016 Saarland University
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
import org.droidmate.logcat.IApiLogcatMessage

@Slf4j
class DeviceLogs implements IDeviceLogs, Serializable
{
  private static final long serialVersionUID = 1

  List<IApiLogcatMessage> apiLogs = null

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
      .toString()
  }

}

