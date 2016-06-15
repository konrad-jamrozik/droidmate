// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.plugin_hook;

import android.content.Context;

/**
 * Please see {@link HookPluginTemplate} to see how to implement your custom local (not in vcs) ookPlugin.
 */
class HookPluginExample implements IHookPlugin
{
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
    // KJA (hook/destructuring) make before/after hooks accept not string, but ApiLogcatMessage or similar. Depends on hook/libmonitor
    System.out.println("hookAfterApiCall/returnValue: " + returnValue);
    if (apiLogcatMessagePayload.contains("mthd: getDeviceId"))
    {
      String mockedDevId = "DEV-ID-MOCKED-BY-AFTER-HOOK";
      System.out.println("hookAfterApiCall: replacing deviceId=" + returnValue + " with mocked value: " + mockedDevId);

      return mockedDevId;
    } else
      return returnValue;
  }

  public void finalizeHookPlugin()
  {
    System.out.println("finalizeHookPlugin. Context = " + context);
  }

}

