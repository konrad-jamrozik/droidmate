// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.konradjamrozik.Resource
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exploration.actions.ResetAppExplorationAction
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.logcat.IApiLogcatMessage
import java.time.Duration

class ApkSummary() {

  companion object {

    fun build(data: IApkExplorationOutput2): String {
      return build(Payload(data))
    }

    fun build(payload: Payload): String {

      return with(payload) {
        // @formatter:off
      StringBuilder(template)
        .replaceVariable("exploration_title"            , "droidmate-run:" + appPackageName)
        .replaceVariable("total_run_time"               , totalRunTime.minutesAndSeconds)
        .replaceVariable("total_actions_count"          , totalActionsCount.toString().padStart(4, ' '))
        .replaceVariable("total_resets_count"           , totalResetsCount.toString().padStart(4, ' '))
        .replaceVariable("exception"                    , exception.messageIfAny())
        .replaceVariable("unique_apis_count"            , uniqueApisCount.toString())
        .replaceVariable("api_entries"                  , apiEntries.joinToString(separator = "\n"))
        .replaceVariable("unique_api_event_pairs_count" , uniqueApiEventPairsCount.toString())
        .replaceVariable("api_event_entries"            , apiEventEntries.joinToString(separator = "\n"))
        .toString()
        }
      // @formatter:on
    }

    val template: String by lazy {
      Resource("apk_exploration_summary_template.txt").text
    }

    private fun DeviceException.messageIfAny(): String {
      return if (this is DeviceExceptionMissing)
        ""
      else {
        "\n* * * * * * * * * *\n" +
          "WARNING! This exploration threw an exception.\n\n" +
          "Exception message: '${this.message}'.\n\n" +
          LogbackConstants.err_log_msg + "\n" +
          "* * * * * * * * * *\n"
      }
    }
  }

  data class Payload(
    val appPackageName: String,
    val totalRunTime: Duration,
    val totalActionsCount: Int,
    val totalResetsCount: Int,
    val exception: DeviceException,
    val uniqueApisCount: Int,
    val apiEntries: List<ApiEntry>,
    val uniqueApiEventPairsCount: Int,
    val apiEventEntries: List<ApiEventEntry>
  ) {

    // KJA 2
    constructor(data: IApkExplorationOutput2) : this(
      appPackageName = data.packageName,
      totalRunTime = data.explorationDuration,
      totalActionsCount = data.actRess.size,
      totalResetsCount = data.actions.count { it.base is ResetAppExplorationAction },
      exception = data.exception,
      uniqueApisCount = 0,
      apiEntries = data.uniqueApiLogsWithFirstTriggeringActionIndex.map {
        ApiEntry(
          time = Duration.between(data.explorationStartTime, it.key.time),
          actionIndex = it.value,
          threadId = it.key.threadId.toInt(),
          apiSignature = it.key.uniqueString
        )
      },
      uniqueApiEventPairsCount = 0,
      apiEventEntries = emptyList()
    )

    companion object {
      val IApkExplorationOutput2.uniqueApiLogsWithFirstTriggeringActionIndex: Map<IApiLogcatMessage, Int> get() {
        return this.actRess.uniqueItemsWithFirstOccurrenceIndex(
          extractItems = { it.result.deviceLogs.apiLogsOrEmpty },
          extractUniqueString = { it.uniqueString }
        )
      }
    }
  }

  data class ApiEntry(val time: Duration, val actionIndex: Int, val threadId: Int, val apiSignature: String) {
    companion object {
      private val actionIndexPad: Int = 7
      private val threadIdPad: Int = 7
    }

    override fun toString(): String {
      val actionIndexFormatted = "$actionIndex".padStart(actionIndexPad)
      val threadIdFormatted = "$threadId".padStart(threadIdPad)
      return "${time.minutesAndSeconds} $actionIndexFormatted $threadIdFormatted  $apiSignature"
    }
  }

  data class ApiEventEntry(val apiEntry: ApiEntry, val event: String) {
    companion object {
      private val actionIndexPad: Int = 7
      private val threadIdPad: Int = 7
      private val eventPadEnd: Int = 59
    }

    override fun toString(): String {
      val actionIndexFormatted = "${apiEntry.actionIndex}".padStart(actionIndexPad)
      val eventFormatted = event.padEnd(eventPadEnd)
      val threadIdFormatted = "${apiEntry.threadId}".padStart(threadIdPad)

      return "${apiEntry.time.minutesAndSeconds} $actionIndexFormatted  $eventFormatted $threadIdFormatted  ${apiEntry.apiSignature}"
    }
  }
}
