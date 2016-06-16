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
 * To see where this interface is used, search for "// monitorHook" in org.droidmate.monitor_template_src.MonitorJavaTemplate 
 */
@SuppressWarnings("unused") // Used in org.droidmate.monitor_template_src.MonitorJavaTemplate 
public interface IMonitorHook
{
  /**
   * Called in org.droidmate.monitor_template_src.MonitorJavaTemplate#init(android.content.Context)
   */
  void init(Context context);
  
  void hookBeforeApiCall(String apiLogcatMessagePayload);

  Object hookAfterApiCall(String apiLogcatMessagePayload, Object returnValue);

  /**
   * Called when a call to org.droidmate.device.IDeployableAndroidDevice#closeMonitorServers() is made, 
   * which might not happen for some crashes of the AUE.
   */
  void finalizeMonitorHook();
}
