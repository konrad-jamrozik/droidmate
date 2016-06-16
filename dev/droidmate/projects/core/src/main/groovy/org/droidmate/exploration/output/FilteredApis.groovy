// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

import org.droidmate.apis.IApi
import org.droidmate.common.logcat.Api
import org.droidmate.logcat.IApiLogcatMessage

/// !!! DUPLICATION WARNING !!! org.droidmate.deprecated_still_used.ExplorationOutputDataExtractor.filterApiLogs(java.util.List<java.util.List<org.droidmate.logcat.IApiLogcatMessage>>, java.lang.String, boolean)
class FilteredApis implements IFilteredApis
{
  private final List<IApiLogcatMessage> apiLogs

  FilteredApis(List<List<IApiLogcatMessage>> apiLogs, String packageName)
  {
    def flatApiLogs = apiLogs.flatten() as List<IApiLogcatMessage>

    flatApiLogs = flatApiLogs.findAll {
      assertNoMonitorSocketInitLogs(it.stackTraceFrames)
      return fromMonitoredApp(it.stackTraceFrames, packageName) &&
        !redundantApiCall(it.stackTraceFrames) &&
        !callToInternalActivity(it, packageName)
    }

    this.apiLogs = flatApiLogs
  }

  void assertNoMonitorSocketInitLogs(List<String> stackTraceFrames)
  {
    assert !isStackTraceOfMonitorTcpServerSocketInit(stackTraceFrames):
      "The Socket.<init> monitor logs were expected to be removed by monitor before being sent to host machine."
  }

  boolean fromMonitoredApp(List<String> stackTraceFrames, String remappedPackageName)
  {
    return stackTraceFrames.any {it.startsWith(remappedPackageName)}
  }

  boolean redundantApiCall(List<String> stackTraceFrames)
  {
    return isStackTraceOfRedundantApiCall(stackTraceFrames)
  }

  boolean callToInternalActivity(IApi api, String packageName)
  {
    return api.isCallToStartInternalActivity(packageName)
  }

  @Override
  Collection<List<IApiLogcatMessage>> groupByUniqueString()
  {
    apiLogs.groupBy {it.uniqueString}.values()
  }

  /**
   * <p>
   * Checks if given stack trace was obtained from a log to a call to socket &lt;init> made by Monitor TCP server
   * ({@code org.droidmate.uiautomator_daemon.MonitorJavaTemplate.MonitorTCPServer}).
   *
   * </p><p>
   * Here is an example of a log of monitored API call to such method (with line breaks added for clarity):
   *
   * </p><p>
   * <pre><code>
   * 2015-07-31 16:55:17.132 TRACE from monitor - 07-31 16:55:14.782 I/Adapted_Monitored_API_method_call(817):
   * TId: 1941
   * objCls: java.net.Socket
   * mthd: &lt;init>
   * retCls: void
   * params:
   *
   * stacktrace:
   * dalvik.system.VMStack.getThreadStackTrace(Native Method)->
   * java.lang.Thread.getStackTrace(Thread.java:579)->
   * org.droidmate.monitor.Monitor.getStackTrace(Monitor.java:303)->
   * org.droidmate.monitor.Monitor.redir_8_java_net_Socket_ctor0(Monitor.java:542)->
   * java.lang.reflect.Method.invokeNative(Native Method)->
   * java.lang.reflect.Method.invoke(Method.java:515)->
   * java.net.Socket.&lt;init>(Socket.java)->
   * java.net.ServerSocket.accept(ServerSocket.java:126)->
   * org.droidmate.monitor.Monitor$SerializableTCPServerBase$MonitorServerRunnable.run(Monitor.java:228)->
   * java.lang.Thread.run(Thread.java:841)
   * </code></pre>
   *
   * </p>
   */
  public static boolean isStackTraceOfMonitorTcpServerSocketInit(List<String> stackTrace)
  {
    def secondLastFrame = stackTrace.takeRight(2).first()
    if (secondLastFrame.startsWith("org.droidmate"))
    {
      assert secondLastFrame.startsWith("org.droidmate.monitor.Monitor")
      assert stackTrace.any {it.contains("Socket.<init>")}
      return true
    }

    // Assert made just to be extra-sure.
    assert !(stackTrace.any { it.startsWith("org.droidmate.monitor.Monitor") && it.contains("Socket.<init>") })

    return false
  }

  // !!! DUPLICATION WARNING !!! org.droidmate.deprecated_still_used.ExplorationOutputDataExtractor.manuallyConfirmedRedundantApis
  private static List<String> manuallyConfirmedRedundantApis = [
    "redir_4_android_webkit_WebView_ctor1",
    "redir_5_android_webkit_WebView_ctor2",
    "redir_6_android_webkit_WebView_ctor3",
    "redir_7_android_webkit_WebView_ctor4",
    "redir_android_app_ActivityManager_restartPackage1",
    "redir_android_content_ContentResolver_openFileDescriptor2",
    "redir_android_content_ContentResolver_query5",
    "redir_android_net_wifi_WifiManager_isWifiEnabled0",
    "redir_java_net_URL_getContent0",
    "redir_java_net_URL_openStream0",
    "redir_android_widget_VideoView_start0",
    "redir_android_widget_VideoView_setVideoURI1",
    "redir_android_widget_VideoView_stopPlayback0",
    "redir_android_widget_VideoView_release1",
    "redir_android_app_NotificationManager_notify2",
    "redir_android_os_PowerManager_WakeLock_release0",
    // This makes actually two methods redundant (as expected), both having one param, but of different type.
    "redir_android_content_ContextWrapper_setWallpaper1"
  ]

  /**
   * Checks if given stack trace was obtained from a log to a redundant API call. Redundant API calls are no longer logged: they
   * have been since removed from the API list and thus, the monitor.
   *
   * However, the redundant API calls might appear in data that was obtained before they have been removed from the API list.
   * In such cases, this method is useful for filtering them out.
   */
  public static boolean isStackTraceOfRedundantApiCall(List<String> stackTraceFrames)
  {
    String monitoredApiCallTrace = stackTraceFrames.findAll {it.startsWith(Api.monitorRedirectionPrefix)}.first()
    return manuallyConfirmedRedundantApis.any {monitoredApiCallTrace.contains(it)}
  }


}
