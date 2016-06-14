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
 * To see where this interface is used, search for "// hookPlugin" in org.droidmate.monitor_template_src.MonitorJavaTemplate 
 */
@SuppressWarnings("unused") // Used in org.droidmate.monitor_template_src.MonitorJavaTemplate 
public interface IHookPlugin
{
  void init(Context context);
  
  void hookBeforeApiCall(String apiLogcatMessagePayload);

  Object hookAfterApiCall(String apiLogcatMessagePayload, Object returnValue);

  /**
   * Called when org.droidmate.device.IDeployableAndroidDevice#closeMonitorServers() succeeds, which might not happen for some
   * crashes of the AUE.
   */
  void finalizeHookPlugin();
}
