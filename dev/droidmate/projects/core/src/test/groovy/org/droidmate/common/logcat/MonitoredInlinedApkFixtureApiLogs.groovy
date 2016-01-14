// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.common.logcat

import org.droidmate.logcat.IApiLogcatMessage

class MonitoredInlinedApkFixtureApiLogs
{

  private final List<List<IApiLogcatMessage>> apiLogs

  MonitoredInlinedApkFixtureApiLogs(List<List<IApiLogcatMessage>> apiLogs)
  {
    this.apiLogs = apiLogs
  }


  void assertCheck()
  {
    assert apiLogs.size() == 3

    //noinspection GroovyUnusedAssignment
    def resetAppApiLogs = apiLogs[0]
    def clickApiLogs = apiLogs[1]
    def terminateAppApiLogs = apiLogs[2]

    // In the legacy API set using PScout APIs the
    // <java.net.URLConnection: void <init>(java.net.URL)>
    // was monitored, now it isn't. The commented out asserts are from the legacy montored set of pscout APIs:
//    assert clickApiLogs.size() == 2
//    assert clickApiLogs*.methodName == ["openConnection", "<init>"]

    assert clickApiLogs.size() == 1
    assert clickApiLogs*.methodName == ["openConnection"]

    assert terminateAppApiLogs.size() == 0
  }
}
