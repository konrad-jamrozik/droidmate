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
 * <p>You can implement this interface to provide your own custom logic before and after calls to the monitored methods.</p>
 * 
 * <p>To understand how implement this interface, please consult {@link MonitorHookTemplate}.</p>
 * 
 * <p>For an example implementation of this interface, see {@link MonitorHookExample}.</p>
 * 
 * <p>
 * To see where this interface is used:<br/>
 * - search for "// monitorHook" in org.droidmate.monitor.MonitorJavaTemplate<br/>
 * - look at usages of org.droidmate.monitor.RedirectionsGenerator#monitorHookInstanceName
 * </p>
 * 
 * <p>If your use case is advanced mocking of return values of method calls, consider using a powerful mocking framework, 
 * <a href="https://github.com/M66B/XPrivacy">XPrivacy</a></p>.
 */
@SuppressWarnings("unused") // Used in org.droidmate.monitor.MonitorJavaTemplate 
public interface IMonitorHook
{
  /**
   * Called in org.droidmate.monitor.MonitorJavaTemplate#init(android.content.Context)
   */
  void init(Context context);

  /**
   * <p>Override this method to inspect what will be logged about given monitored method call just before the monitored method call 
   * takes place. </p>
   *
   * @param apiLogcatMessagePayload
   *   <p>Example values can be seen in org.droidmate.logcat.ApiLogcatMessageTest.</p>
   *   <p>Refer to org.droidmate.common.logcat.ApiLogcatMessage#from(java.lang.String) for an idea how to parse this string.</p>
   *   <p>Unfortunately, the referenced method cannot be used when implementing this interface. This interface implementation 
   *   will run on Android device. The Android device doesn't support Groovy, which is required by the referenced method.</p>
   */
  void hookBeforeApiCall(String apiLogcatMessagePayload);

  /**
   * <p>Similar to {@link #hookBeforeApiCall(String)}, but this method is called after, not before, the call to the monitored 
   * method.</p>
   * 
   * <p>This method also enables replacing the value returned by the monitored method call. This method is passed
   * in {@code returnValue} the monitored method call return value. The return value returned by this method will be treated 
   * by the AUE as the value returned by the monitored method call. For example, for default behavior, just return from this 
   * method {@code returnValue}.</p>
   * @param apiLogcatMessagePayload See {@link #hookBeforeApiCall(String)}.
   * @param returnValue The monitored method call return value.
   * @return The value substituted for returnValue.
   */
  Object hookAfterApiCall(String apiLogcatMessagePayload, Object returnValue);

  /**
   * <p>Called when a call to org.droidmate.device.IDeployableAndroidDevice#closeMonitorServers() is made, 
   * which might not happen for some crashes of the AUE.</p>
   */
  void finalizeMonitorHook();
}
