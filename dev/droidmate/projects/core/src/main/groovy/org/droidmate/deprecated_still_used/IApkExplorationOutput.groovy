// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import org.droidmate.logcat.IApiLogcatMessage

import java.time.LocalDateTime

@Deprecated
interface IApkExplorationOutput extends Serializable
{
  String getAppPackageName()

  List<TimestampedExplorationAction> getActions()

  Exception getCaughtException()
  void setCaughtException(Exception e)

  List<List<IApiLogcatMessage>> getApiLogs()

  LocalDateTime getMonitorInitTime()

  void setExplorationEndTime(LocalDateTime time)
}
