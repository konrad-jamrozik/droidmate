// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

@Deprecated
interface IApkExplorationOutput extends Serializable
{
  String getAppPackageName()

  List<ITimeFormattedLogcatMessage> getInstrumentationMsgs()

  List<TimestampedExplorationAction> getActions()

  List<IDeviceGuiSnapshot> getGuiSnapshots()

  Exception getCaughtException()
  void setCaughtException(Exception e)

  List<List<IApiLogcatMessage>> getApiLogs()

  LocalDateTime getMonitorInitTime()
  void setMonitorInitTime(LocalDateTime time)

  LocalDateTime getExplorationEndTime()
  void setExplorationEndTime(LocalDateTime time)

  boolean getIsUiaTestCase()

  String getHeader()

  List<String> getComments()

  interface IUiaTestCaseAnnotations extends Serializable
  {
    String getTestCaseName()

    List<String> getComments()
  }

  void verifyCompletedDataIntegrity()
}
