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
