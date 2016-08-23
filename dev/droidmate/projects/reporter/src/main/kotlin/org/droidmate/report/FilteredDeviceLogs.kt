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
package org.droidmate.report

import org.droidmate.apis.Api
import org.droidmate.apis.IApi
import org.droidmate.apis.IApiLogcatMessage
import org.droidmate.exploration.device.DeviceLogs
import org.droidmate.exploration.device.IDeviceLogs
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FilteredDeviceLogs private constructor(logs: IDeviceLogs) : IDeviceLogs by logs {

  constructor(apiLogs: List<IApiLogcatMessage>) : this(DeviceLogs(filterApiLogs(apiLogs)))

  companion object {

    private val log: Logger = LoggerFactory.getLogger(FilteredDeviceLogs::class.java)

    private fun filterApiLogs(apiLogs: List<IApiLogcatMessage>): List<IApiLogcatMessage> {

      return apiLogs
        .apply {
          forEach {
            it.checkIsInternalMonitorLog()
            it.warnWhenPossiblyRedundant()
          }
        }
        .filterNot {it.warnAndReturnIsRedundant}
    }

    private fun IApi.checkIsInternalMonitorLog() {
      check(!isStackTraceOfMonitorTcpServerSocketInit(this.stackTraceFrames),
        { "The Socket.<init> monitor logs were expected to be removed by monitor before being sent to the host machine." })
    }

    /**
     * Checks if given stack trace was obtained from a log to a call to socket &lt;init> made by Monitor TCP server
     * ({@code org.droidmate.uiautomator_daemon.MonitorJavaTemplate.MonitorTCPServer}).
     *
     * Here is an example of a log of monitored API call to such method (with line breaks added for clarity):
     *
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
     *
     */
    fun isStackTraceOfMonitorTcpServerSocketInit(stackTrace: List<String>): Boolean {
      val secondLastFrame = stackTrace.takeLast(2).first()
      if (secondLastFrame.startsWith("org.droidmate")) {
        check(secondLastFrame.startsWith("org.droidmate.monitor.Monitor"))
        check(stackTrace.any { it.contains("Socket.<init>") })
        return true
      }

      // Assert made just to be extra-sure.
      check(!(stackTrace.any { it.startsWith("org.droidmate.monitor.Monitor") && it.contains("Socket.<init>") }))

      return false
    }    
    
    /**
     * Logs warnings about presence of possibly redundant API calls. 
     * An API call is redundant if it always calls another API call which is also monitored.
     * Monitoring redundant API calls results in pairs of API calls being logged, skewing the results. 
     * One call should be sufficient. Thus, the redundant monitored API call shouldn't be monitored.
     *
     * API call redundancy is checked by examining stack traces. If given API call does appear inside a stack trace, i.e. not
     * at its end, then given API call is possibly redundant.
     *
     * Consider stack trace of a monitored API call of method C, looking like that: A->B->C.
     * In this stack trace A calls B and B calls C. If API call B is also monitored, the monitoring might be redundant. 
     * It indeed is redundant if B always calls C, which is monitored. In such case monitoring B in addition to C will result in  
     * two API calls always being logged, with stack traces: A->B (for B) and A->B->C (for C). This is redundant. 
     * It is not redundant if B does not always call C. In such cases sometimes there will be only log for B 
     * (with stack trace A->B), without log for C.
     *
     * To determine monitored API calls which are possibly redundant, we look at the internal calls (i.e. all but the last one)
     * in the stack trace which are monitored. In the given example, we look at A and B (C is the last one).
     * Such method calls are logged as warning, to be assessed manually for redundancy and added to
     * org.droidmate.report.FilteredDeviceLogs.Companion.apisManuallyConfirmedToBeRedundant
     * or org.droidmate.report.FilteredDeviceLogs.Companion.apisManuallyConfirmedToBeNotRedundant.
     *
     * If the call was manually determined to be redundant, the org.droidmate.monitor.MonitorGeneratorResources.appguardApis
     * file should have such call removed and DroidMate should be recompiled with the new monitor. Otherwise, a warning will be
     * issued that a redundant APIs are still being logged.
     */
    private fun IApi.warnWhenPossiblyRedundant() {
      this.stackTraceFrames
        .filter { it.startsWith(Api.monitorRedirectionPrefix) && (it !in apisManuallyCheckedForRedundancy) }
        // We drop the first frame as it is the end of stack trace and thus not a candidate for redundancy in this particular
        // stack trace.
        .drop(1) 
        .forEach { log.warn("Possibly redundant API call discovered: " + it) }
    }

    /**
     * <p>
     * Checks if given stack trace was obtained from a log to a redundant API call and issues a warning if so.
     * Redundant API calls should be no longer logged: they should have been since removed from the API list and thus, the
     * monitor. Thus, if such call is encountered, a warning is issued.
     * </p><p>
     *
     * Note that the redundant API calls might appear in data that was obtained before they have been removed from the API list
     * that was used to obtain that data.
     * </p>
     */
    private val IApi.warnAndReturnIsRedundant: Boolean get() {

      val monitoredFrames = stackTraceFrames.filter { it.startsWith(Api.monitorRedirectionPrefix) }
      check(monitoredFrames.isNotEmpty())
      /* 
        We take only the first monitored call, as this is the bottom of the stack trace, i.e. this method doesn't call any other 
        monitored methods. All other monitored calls in the stack trace will be present again in the logs, at the bottom of their
        own stack traces. They will be checked for redundancy then, so they don't have to be checked here.
       */
      val monitoredCall = monitoredFrames.first()
      return if (monitoredCall in apisManuallyConfirmedToBeRedundant) {
        log.warn("Redundant API call discovered: " + monitoredCall)
        true
      } else
        false
    }

    // For now empty lists. Will update them as the warnings are observed, while comparing to the legacy lists 
    // (present in this file).
    private val apisManuallyConfirmedToBeRedundant: List<String> = emptyList()
    private val apisManuallyConfirmedToBeNotRedundant: List<String> = listOf(
      // WISH Observed possible redundancy once. Waiting to see it again, to investigate the stack trace.
      // https://android.googlesource.com/platform/external/apache-http/+/android-4.4.4_r2.0.1/src/org/apache/http/impl/client/AbstractHttpClient.java#514
      // https://android.googlesource.com/platform/external/apache-http/+/android-6.0.1_r63/src/org/apache/http/impl/client/AbstractHttpClient.java#519
//      "redir_org_apache_http_impl_client_AbstractHttpClient_execute3"
    )
    /// !!! DUPLICATION WARNING !!! with org.droidmate.monitor.RedirectionsGenerator.redirMethodNamePrefix and related code.
    private val apisManuallyCheckedForRedundancy: List<String> = apisManuallyConfirmedToBeRedundant + apisManuallyConfirmedToBeNotRedundant
    
    // Updated 29 Jun 2016.
    //
    // Comments to elements of this list refer to the following resources:
    //
    // appguard_apis.txt: 
    //   located in repos\github\droidmate\dev\droidmate\projects\resources
    // After build, APIs from this file are generated into Monitor.java
    //
    // AppGuard MonitorInitalizer.java: 
    //   path given in repos\sechair\droidmate-private\resources\from_Philipp\appguard_apis_list_origin.txt
    //
    // jellybean_publishedapimapping_modified.txt: 
    //   located in repos\sechair\droidmate-private\resources\legacy_api_lists
    //
    // appguard_legacy_apis.txt:  
    //   located in repos\sechair\droidmate-private\resources\legacy_api_lists
    //
    @Suppress("unused")
    private val legacyApisManuallyConfirmedToBeNotRedundant: List<String> = listOf(
      
      // ----- Methods present in appguard_apis.txt -----
      "redir_java_net_URL_openConnection0",
      "redir_org_apache_http_impl_client_AbstractHttpClient_execute3",

      // ----- Methods whose modified version is present in appguard_apis.txt -----
      // Now present as redir_5_java_net_Socket_ctor4  
      // It calls ctor0 but then it calls java.net.Socket#tryAllAddresses which has a lot of logic.
      // https://android.googlesource.com/platform/libcore/+/android-4.4.4_r2.0.1/luni/src/main/java/java/net/Socket.java
      // https://android.googlesource.com/platform/libcore/+/android-6.0.1_r63/luni/src/main/java/java/net/Socket.java
      "redir_13_java_net_Socket_ctor4",
      
      // ----- Methods not present in appguard_apis.txt, but which were present in jellybean_publishedapimapping_modified.txt ----- 
      "redir_android_bluetooth_BluetoothAdapter_enable0",
      // Actually this call is redundant, but it is a part of suite of API calls detecting Intent-requiring operations.
      "redir_android_content_ContextWrapper_startService1",
      // Same as call above.
      "redir_android_content_ContextWrapper_sendOrderedBroadcast2"
    )

    @Suppress("unused")
    private val legacyApisManuallyConfirmedToBeRedundant: List<String> = listOf(

      // ----- Methods present in appguard_apis.txt -----
      // Android 6 source: https://android.googlesource.com/platform/frameworks/base/+/android-6.0.1_r46/core/java/android/os/PowerManager.java#1127
      "redir_android_os_PowerManager_WakeLock_release0",
      "redir_android_content_ContentResolver_openFileDescriptor2",
      "redir_android_content_ContentResolver_query5",
      
       // ----- Methods not present in appguard_apis.txt, but which were present in jellybean_publishedapimapping_modified.txt ----- 
      "redir_4_android_webkit_WebView_ctor1",
      "redir_5_android_webkit_WebView_ctor2",
      "redir_6_android_webkit_WebView_ctor3",
      "redir_7_android_webkit_WebView_ctor4",
      "redir_android_app_ActivityManager_restartPackage1",
      "redir_android_net_wifi_WifiManager_isWifiEnabled0",
      "redir_java_net_URL_getContent0",
      "redir_java_net_URL_openStream0",
      "redir_android_widget_VideoView_start0",
      "redir_android_widget_VideoView_setVideoURI1",
      "redir_android_widget_VideoView_stopPlayback0",
      "redir_android_widget_VideoView_release1",
      "redir_android_app_NotificationManager_notify2",
      // This makes actually two methods redundant in jellybean_publishedapimapping_modified.txt, 
      // both having one param, but of different type.
      "redir_android_content_ContextWrapper_setWallpaper1"      
    )
  }
}