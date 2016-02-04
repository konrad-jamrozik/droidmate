// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import org.droidmate.logcat.IApiLogcatMessage

interface IDeviceLogs
{
  List<IApiLogcatMessage> getApiLogs()

  List<IApiLogcatMessage> getApiLogsOrEmpty()

  boolean getReadAnyApiLogsSuccessfully()

  void setApiLogs(List<IApiLogcatMessage> apiLogs)
}