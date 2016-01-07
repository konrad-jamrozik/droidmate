// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.common.logcat

import org.droidmate.logcat.IApiLogcatMessage

class ApiLogcatMessageListExtensions
{
  public static Boolean sortedByTimePerPID(List<IApiLogcatMessage> self)
  {
    return self.groupBy {it.pidString}.every {pid, logsByPid -> logsByPid*.time == logsByPid*.time.collect().sort() }
  }
}
