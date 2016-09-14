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
            it.checkIsNotStackTraceOfInternalMonitorCall()
            it.warnWhenPossiblyRedundant()
          }
        }
        .filterNot {it.warnAndReturnIsRedundant}
    }

    /**
     * Checks if given stack trace was obtained from a log to a call to socket &lt;init> made by Monitor TCP server
     * ({@code org.droidmate.uiautomator_daemon.MonitorJavaTemplate.MonitorTCPServer}).
     *
     * This is done by checking if in the stack trace there is a frame with "org.droidmate.monitor.Monitor" prefix that
     * is not a call to method with prefix "redir" and is not a call to method "getStackTrace".
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
    private fun IApi.checkIsNotStackTraceOfInternalMonitorCall() {
      val monitorFrames = this.stackTraceFrames.filter { it.startsWith("org.droidmate.monitor.Monitor") }
      check (monitorFrames.all { it.contains("Monitor.redir_") || it.contains("Monitor.getStackTrace") })
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
     * If the call was manually determined to be redundant, the org.droidmate.monitor.MonitorGeneratorResources.monitoredApis
     * file should have such call removed and DroidMate should be recompiled with the new monitor. Otherwise, a warning will be
     * issued that a redundant APIs are still being logged.
     */
    private fun IApi.warnWhenPossiblyRedundant() {
      
      val monitoredMethods = this.stackTraceFrames
        .filter { it.startsWith(Api.monitorRedirectionPrefix) }

      check(monitoredMethods.isNotEmpty())
      
      val possiblyRedundantMethods = monitoredMethods
        .filterNot { (apisManuallyCheckedForRedundancy.any { manuallyChecked -> it.contains(manuallyChecked) })}
        // We drop the first frame as it is the end of stack trace and thus not a candidate for redundancy in this particular
        // stack trace.
        .drop(1)
      
      if (possiblyRedundantMethods.isNotEmpty())
      {
        log.warn("Possibly redundant API call discovered!\n" +
        "The possibly redundant API calls (except the first one):\n" + monitoredMethods.joinToString(separator="\n") + "\n" +
        "All methods on the stack trace:\n" + this.stackTraceFrames.joinToString(separator="\n") ) 
      }
        
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

      val monitoredMethods = stackTraceFrames.filter { it.startsWith(Api.monitorRedirectionPrefix) }
      
      check(monitoredMethods.isNotEmpty())
      
      /* 
        We take only the first monitored call, as this is the bottom of the stack trace, i.e. this method doesn't call any other 
        monitored methods. All other monitored calls in the stack trace will be present again in the logs, at the bottom of their
        own stack traces. They will be checked for redundancy then, so they don't have to be checked here.
       */
      val monitoredCall = monitoredMethods.first()
      return if (apisManuallyConfirmedToBeRedundant.any { monitoredCall.contains(it) }) {
        log.warn("Redundant API call discovered. The reported data was obtained using monitor that was monitoring API methods which are redundant. The API call: " + monitoredCall)
        true
      } else
        false
    }

    // This list is to be updated as the warnings are observed, while comparing to the legacy lists 
    // (present in this file).
    private val apisManuallyConfirmedToBeRedundant: List<String> = listOf(
      // Android 6 source: https://android.googlesource.com/platform/frameworks/base/+/android-6.0.1_r46/core/java/android/os/PowerManager.java#1127
      "redir_android_os_PowerManager_WakeLock_release0"
    )
    
    private val apisManuallyConfirmedToBeNotRedundant: List<String> = listOf(
      
      /*
        This is a deprecated method (see the monitored apis list) which ultimately calls Socket.connect2().
        In 75 cases observed, the Socket.connect2 was always called. 
        
        However, the distance of the calls in the stack between these two methods is large, plus brief manual investigation has
        shown the intermediate methods have complex logic. Thus, we cannot lure out a case in which Socket.connect2 is not called,
        thus the API is not considered redundant.  
         
        Relevant stack trace: 
          org.droidmate.monitor.Monitor.redir_java_net_Socket_connect2(Monitor.java:1979)
          org.apache.http.conn.scheme.PlainSocketFactory.connectSocket(PlainSocketFactory.java:124)
          org.apache.http.impl.conn.DefaultClientConnectionOperator.openConnection(DefaultClientConnectionOperator.java:149)
          org.apache.http.impl.conn.AbstractPoolEntry.open(AbstractPoolEntry.java:169)
          org.apache.http.impl.conn.AbstractPooledConnAdapter.open(AbstractPooledConnAdapter.java:124)
          org.apache.http.impl.client.DefaultRequestDirector.execute(DefaultRequestDirector.java:366)
          org.apache.http.impl.client.AbstractHttpClient.execute(AbstractHttpClient.java:560)
          java.lang.reflect.Method.invoke(Native Method)
          de.larma.arthook.OriginalMethod.invoke(OriginalMethod.java:43)
          org.droidmate.monitor.Monitor.redir_org_apache_http_impl_client_AbstractHttpClient_execute3(Monitor.java:2068)
      
        Monitored methods from the stack trace above:
          org.droidmate.monitor.Monitor.redir_java_net_Socket_connect2(Monitor.java:1979)
          org.droidmate.monitor.Monitor.redir_org_apache_http_impl_client_AbstractHttpClient_execute3(Monitor.java:2068)
      
        Relevant source code:
          https://android.googlesource.com/platform/external/apache-http/+/android-4.4.4_r2.0.1/src/org/apache/http/impl/client/AbstractHttpClient.java#514
          https://android.googlesource.com/platform/external/apache-http/+/android-6.0.1_r63/src/org/apache/http/impl/client/AbstractHttpClient.java#519
       */
      "redir_org_apache_http_impl_client_AbstractHttpClient_execute3",

      /*
        Redundancy was being reported because the monitored method called itself down the stack trace. See the lines prefixed 
        with arrows in the the relevant stack trace:
       
          dalvik.system.VMStack.getThreadStackTrace(Native Method)
          java.lang.Thread.getStackTrace(Thread.java:580)
          org.droidmate.monitor.Monitor.getStackTrace(Monitor.java:502)
      --> org.droidmate.monitor.Monitor.redir_java_net_URL_openConnection0(Monitor.java:2027)
          libcore.net.url.JarURLConnectionImpl.<init>(JarURLConnectionImpl.java:73)
          libcore.net.url.JarHandler.openConnection(JarHandler.java:42)
          java.net.URL.openConnection(URL.java:479)
          java.lang.reflect.Method.invoke(Native Method)
          de.larma.arthook.OriginalMethod.invoke(OriginalMethod.java:43)
      --> org.droidmate.monitor.Monitor.redir_java_net_URL_openConnection0(Monitor.java:2032)
          java.net.URL.openStream(URL.java:470)

       */
      "redir_java_net_URL_openConnection0",
      
      /*
        Same story as with openConnection0 (see above).
        
        The relevant stack trace:
          dalvik.system.VMStack.getThreadStackTrace(Native Method)
          java.lang.Thread.getStackTrace(Thread.java:580)
          org.droidmate.monitor.Monitor.getStackTrace(Monitor.java:502)
      --> org.droidmate.monitor.Monitor.redir_10_java_net_URL_ctor3(Monitor.java:837)
          java.net.URL.<init>(URL.java:125)
          libcore.net.url.JarHandler.parseURL(JarHandler.java:82)
          java.net.URL.<init>(URL.java:188)
          java.lang.reflect.Method.invoke(Native Method)
          de.larma.arthook.OriginalMethod.invoke(OriginalMethod.java:43)
      --> org.droidmate.monitor.Monitor.redir_10_java_net_URL_ctor3(Monitor.java:842)
          java.net.URL.<init>(URL.java:125)        
       */
      "redir_10_java_net_URL_ctor3",
      
      /*
        This method calls other monitored method, openAssetFileDescriptor, if the parameter URI has file scheme:
          https://android.googlesource.com/platform/frameworks/base/+/android-6.0.1_r63/core/java/android/content/ContentResolver.java#662
        But this doesn't happen when the parameter URI has Android resource scheme:
          https://android.googlesource.com/platform/frameworks/base/+/android-6.0.1_r63/core/java/android/content/ContentResolver.java#647
        this this method is not redundant.
       */
      "redir_android_content_ContentResolver_openInputStream1",

      /*
        The redundancy suspicion happens because the ContentProvider.query() abstract method implementation of Spotify app 
        calls it. It might not be the case with other implementations of this abstract method.
        
        The relevant stack trace:
      --> org.droidmate.monitor.Monitor.redir_android_content_ContentResolver_registerContentObserver4(Monitor.java:2089)
          android.content.ContentResolver.registerContentObserver(ContentResolver.java:1596)
          com.spotify.mobile.android.provider.ad.<init>(SourceFile:219)
          com.spotify.music.internal.provider.SpotifyProvider.query(SourceFile:115)
          android.content.ContentProvider.query(ContentProvider.java:1017)
          android.content.ContentProvider$Transport.query(ContentProvider.java:238)
          android.content.ContentResolver.query(ContentResolver.java:491)
          java.lang.reflect.Method.invoke(Native Method)
          de.larma.arthook.OriginalMethod.invoke(OriginalMethod.java:43)
      --> org.droidmate.monitor.Monitor.redir_android_content_ContentResolver_query6(Monitor.java:2082)
          android.content.ContentResolver.query(ContentResolver.java:434)        
       */
      "redir_android_content_ContentResolver_query6"
    )
    /// !!! DUPLICATION WARNING !!! with org.droidmate.monitor.RedirectionsGenerator.redirMethodNamePrefix and related code.
    private val apisManuallyCheckedForRedundancy: List<String> = apisManuallyConfirmedToBeRedundant + apisManuallyConfirmedToBeNotRedundant
    
    // Updated 29 Jun 2016.
    //
    // Comments to elements of this list refer to the following resources:
    //
    // monitored_apis.txt: 
    //   located in repos\github\droidmate\dev\droidmate\projects\resources
    // After build, APIs from this file are generated into Monitor.java
    //
    // AppGuard MonitorInitalizer.java: 
    //   path given in repos\sechair\droidmate-private\resources\from_Philipp\monitored_apis_list_origin.txt
    //
    // jellybean_publishedapimapping_modified.txt: 
    //   located in repos\sechair\droidmate-private\resources\legacy_api_lists
    //
    // appguard_legacy_apis.txt:  
    //   located in repos\sechair\droidmate-private\resources\legacy_api_lists
    //
    @Suppress("unused")
    private val legacyApisManuallyConfirmedToBeNotRedundant: List<String> = listOf(
      
      // ----- Methods present in monitored_apis.txt -----
      // None left, all checked and moved to current list.

      // ----- Methods whose modified version is present in monitored_apis.txt -----
      // Now present as redir_5_java_net_Socket_ctor4  
      // It calls ctor0 but then it calls java.net.Socket#tryAllAddresses which has a lot of logic.
      // https://android.googlesource.com/platform/libcore/+/android-4.4.4_r2.0.1/luni/src/main/java/java/net/Socket.java
      // https://android.googlesource.com/platform/libcore/+/android-6.0.1_r63/luni/src/main/java/java/net/Socket.java
      "redir_13_java_net_Socket_ctor4",
      
      // ----- Methods not present in monitored_apis.txt, but which were present in jellybean_publishedapimapping_modified.txt ----- 
      "redir_android_bluetooth_BluetoothAdapter_enable0",
      // Actually this call is redundant, but it is a part of suite of API calls detecting Intent-requiring operations.
      "redir_android_content_ContextWrapper_startService1",
      // Same as call above.
      "redir_android_content_ContextWrapper_sendOrderedBroadcast2"
    )

    @Suppress("unused")
    private val legacyApisManuallyConfirmedToBeRedundant: List<String> = listOf(

      // ----- Methods present in monitored_apis.txt -----
      
      "redir_android_content_ContentResolver_openFileDescriptor2",
      "redir_android_content_ContentResolver_query5",
      
       // ----- Methods not present in monitored_apis.txt, but which were present in jellybean_publishedapimapping_modified.txt ----- 
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