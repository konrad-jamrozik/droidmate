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

package org.droidmate.monitor;

import android.content.Context;

/**
 * <p>Example implementation of {@link IMonitorHook}. Please see {@link MonitorHookTemplate} to see how to implement your 
 * custom local (not in vcs) MonitorHook.</p>
 */
class MonitorHookExample implements IMonitorHook
{
  // Warnings suppressed because this is just an example implementation stub.
  @SuppressWarnings({"FieldCanBeLocal", "unused"}) 
  private Context context;

  public void init(Context context)
  {
    this.context = context;
  }

  public void hookBeforeApiCall(String apiLogcatMessagePayload)
  {
    System.out.println("hookBeforeApiCall/apiLogcatMessagePayload: " + apiLogcatMessagePayload);
  }

  public Object hookAfterApiCall(String apiLogcatMessagePayload, Object returnValue)
  {
    System.out.println("hookAfterApiCall/returnValue: " + returnValue);
    if (apiLogcatMessagePayload.contains("mthd: getDeviceId"))
    {
      String mockedDevId = "DEV-ID-MOCKED-BY-AFTER-HOOK";
      System.out.println("hookAfterApiCall: replacing deviceId=" + returnValue + " with mocked value: " + mockedDevId);

      return mockedDevId;
    } else
      return returnValue;
  }

  public void finalizeMonitorHook()
  {
    System.out.println("finalizeMonitorHook");
  }
}

