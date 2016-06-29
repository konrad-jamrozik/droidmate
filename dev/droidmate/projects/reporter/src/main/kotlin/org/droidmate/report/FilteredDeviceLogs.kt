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
      // KJA migrate to new code
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
      // KJA write a test for it.
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
      // KJA (reporting / filtering apis) investigate if this can be simplified into oblivion. Maybe pull the excluded APIs list from a file? Will not require recompilation.
      return ExcludedApis().contains(this.methodName)
    }

    private val apisManuallyConfirmedToBeRedundant: List<String> = emptyList()
    private val apisManuallyConfirmedToBeNotRedundant: List<String> = emptyList()
    private val apisManuallyCheckedForRedundancy: List<String> = apisManuallyConfirmedToBeRedundant + apisManuallyConfirmedToBeNotRedundant

  }
}