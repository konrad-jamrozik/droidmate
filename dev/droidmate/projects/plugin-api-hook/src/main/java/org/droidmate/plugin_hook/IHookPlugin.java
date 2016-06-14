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

@SuppressWarnings("unused") // Used in org.droidmate.monitor_template_src.MonitorJavaTemplate 
public interface IHookPlugin
{
  void init(Context context);
  
  void hookBeforeApiCall(String apiLogcatMessagePayload);

  Object hookAfterApiCall(String apiLogcatMessagePayload, Object returnValue);
  
  void finalizeHookPlugin();
}
