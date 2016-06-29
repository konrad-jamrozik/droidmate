// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.apis.ExcludedApis
import org.droidmate.apis.IApi
import org.droidmate.common.logcat.Api
import org.droidmate.exploration.device.DeviceLogs
import org.droidmate.exploration.device.IDeviceLogs
import org.droidmate.exploration.output.FilteredApis
import org.droidmate.logcat.IApiLogcatMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FilteredDeviceLogs private constructor(logs: IDeviceLogs) : IDeviceLogs by logs {

  constructor(apiLogs: List<IApiLogcatMessage>, packageName: String) :
  this(DeviceLogs(filterApiLogs(apiLogs, packageName)))

  companion object {

    private val log: Logger = LoggerFactory.getLogger(FilteredDeviceLogs::class.java)

    private fun filterApiLogs(apiLogs: List<IApiLogcatMessage>, packageName: String): List<IApiLogcatMessage> {

      return apiLogs
        .apply {
          forEach {
            it.checkIsInternalMonitorLog()
            it.warnWhenPossiblyRedundant()
          }
        }
        .filterNot {
          it.warnAndReturnIsRedundant
            || it.isExcluded
            || it.isCallToStartInternalActivity(packageName
          )
        }
    }

    private fun IApi.checkIsInternalMonitorLog() {
      // KJA2 migrate to new code
      check(!FilteredApis.isStackTraceOfMonitorTcpServerSocketInit(this.stackTraceFrames),
        { "The Socket.<init> monitor logs were expected to be removed by monitor before being sent to the host machine." })
    }

    /**
     * <p>
     * Logs warning about presence of possibly redundant API calls. An API call is redundant if it always calls
     * (delegates to) another API call which is also monitored. Thus, the redundant monitored API call shouldn't be monitored.
     * </p><p>
     *
     * This is checked by examining stack traces. Consider stack trace of a monitored API call of method C, looking
     * like that: A->B->C. In this stack trace A calls B, B calls C. If C always calls D, which is also monitored, we will have
     * another log with a stack trace of A->B->C->D. In such case C is redundant. We have to monitor only D.
     * </p><p>
     *
     * To determine monitored API calls which are possibly redundant, we look at the internal calls (i.e. all but the last one)
     * in the stack trace which are monitored. In the given example, this is C. Such method calls are logged,
     * to be assessed manually for redundancy and added to
     * org.droidmate.report.FilteredDeviceLogs.Companion.apisManuallyConfirmedToBeRedundant
     * or org.droidmate.report.FilteredDeviceLogs.Companion.apisManuallyConfirmedToBeNotRedundant.
     * </p><p>
     *
     * If the call was manually determined to be redundant, the org.droidmate.monitor.MonitorGeneratorResources.appguardApis
     * file should have such call removed and DroidMate should be recompiled with the new monitor. Otherwise, a warning will be
     * issued that a redundant APIs are still being logged.
     * </p>
     */
    private fun IApi.warnWhenPossiblyRedundant() {
      // KJA 2 write a test for it.
      this.stackTraceFrames
        .filter { it.startsWith(Api.monitorRedirectionPrefix) && (it !in apisManuallyCheckedForRedundancy) }
        .forEach { log.warn("Possibly redundant API call discovered: " + it) }
    }

    /**
     * <p>
     * Checks if given stack trace was obtained from a log to a redundant API call and issues a warning if so.
     * Redundant API calls should be no longer logged: they should have been since removed from the API list and thus, the
     * monitor. Thus, if such call is encountered, a warning is issued.
     * </p><p>
     *
     * Note that the redundant API calls might appear in data that was obtained before they have been removed from the API list.
     * </p>
     */
    private val IApi.warnAndReturnIsRedundant: Boolean get() {

      val monitoredFrames = stackTraceFrames.filter {
        it.startsWith(Api.monitorRedirectionPrefix) || it.startsWith(Api.monitorRedirectionPrefixLegacy)
      }
      check(monitoredFrames.isNotEmpty())
      /* 
        We take only first monitored call, as this is the bottom of stack trace, i.e. this method doesn't call any other 
        monitored methods. All other monitored calls in the stack trace will be present again in the logs, at the bottom of their
        own stack trace. They will be checked for redundancy then, so they don't have to be checked here.
       */
      val monitoredCall = monitoredFrames.first()
      return if (monitoredCall in apisManuallyConfirmedToBeRedundant) {
        log.warn("Redundant API call discovered: " + monitoredCall)
        true
      } else
        false
    }

    private val IApi.isExcluded: Boolean get() {
      return ExcludedApis().contains(this.methodName)
    }

    private val apisManuallyConfirmedToBeRedundant: List<String> = emptyList()
    private val apisManuallyConfirmedToBeNotRedundant: List<String> = emptyList()
    private val apisManuallyCheckedForRedundancy: List<String> = apisManuallyConfirmedToBeRedundant + apisManuallyConfirmedToBeNotRedundant
    
    // Updated 29 Jun 2016.
    //
    // Comments to elements of this list refer to the following resources:
    //
    // appguard_apis.txt: 
    //   located in repos\github\droidmate\dev\droidmate\projects\resources
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
    private val legacyApisManuallyConfirmedToBeNotRedundant: List<String> = listOf(
      // Still present in appguard_apis.txt
      "redir_java_net_URL_openConnection0",
      // Still present in appguard_apis.txt
      "redir_org_apache_http_impl_client_AbstractHttpClient_execute3",
      // Not present in appguard_apis.txt. Was present in jellybean_publishedapimapping_modified.txt
      "redir_android_bluetooth_BluetoothAdapter_enable0",
      // Not present in appguard_apis.txt. Was present in jellybean_publishedapimapping_modified.txt
      // Actually this call is redundant, but it is a part of suite of API calls detecting Intent-requiring operations.
      "redir_android_content_ContextWrapper_startService1",
      // Not present in appguard_apis.txt. Was present in jellybean_publishedapimapping_modified.txt
      // Actually this call is redundant, but it is a part of suite of API calls detecting Intent-requiring operations.
      "redir_android_content_ContextWrapper_sendOrderedBroadcast2",
      // Modified version present in appguard_apis.txt, now generated as redir_5_java_net_Socket_ctor4 
      // It calls ctor0 but then it calls java.net.Socket#tryAllAddresses which has a lot of logic.
      // Android 6 source: https://android.googlesource.com/platform/libcore/+/android-6.0.1_r46/luni/src/main/java/java/net/Socket.java
      "redir_13_java_net_Socket_ctor4"      
    )
    
  }
}