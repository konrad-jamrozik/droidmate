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
import org.droidmate.apis.IApi;
import org.droidmate.common.logcat.ApiLogcatMessage;

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
    /* KJA because of this call, getting:
    
    06-14 16:58:59.526 20557-20557/org.droidmate.fixtures.apks.monitored E/AndroidRuntime: FATAL EXCEPTION: main
      rocess: org.droidmate.fixtures.apks.monitored, PID: 20557
      ava.lang.NoClassDefFoundError: Failed resolution of: Lorg/droidmate/common/logcat/ApiLogcatMessage;
         at org.droidmate.plugin_hook.HookPlugin.hookAfterApiCall(HookPlugin.java:36)
         at org.droidmate.monitor_generator.generated.Monitor.redir_android_app_Activity_onResume0(Monitor.java:767)
         
    Basically the jars here: 
    dev\droidmate\projects\monitor-generator\monitor-apk-scaffolding\libs
    
    Are not visible from other jars in the same dir.
    
    Possible fix:
    https://docs.oracle.com/javase/tutorial/deployment/jar/downman.html
    
     */
    final IApi api = ApiLogcatMessage.from(apiLogcatMessagePayload);
    System.out.println("hookAfterApiCall/returnValue: " + returnValue);
    if (api.getMethodName().equals("getDeviceId"))
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

