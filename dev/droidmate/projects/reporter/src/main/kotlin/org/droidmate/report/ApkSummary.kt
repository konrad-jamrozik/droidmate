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

import com.konradjamrozik.Resource
import com.konradjamrozik.uniqueItemsWithFirstOccurrenceIndex
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exploration.actions.ResetAppExplorationAction
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
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
        .replaceVariable("unique_api_event_pairs_count" , uniqueEventApiPairsCount.toString())
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

  @Suppress("unused") // Kotlin BUG on private constructor(data: IApkExplorationOutput2, uniqueApiLogsWithFirstTriggeringActionIndex: Map<IApiLogcatMessage, Int>)
  data class Payload(
    val appPackageName: String,
    val totalRunTime: Duration,
    val totalActionsCount: Int,
    val totalResetsCount: Int,
    val exception: DeviceException,
    val uniqueApisCount: Int,
    val apiEntries: List<ApiEntry>,
    val uniqueEventApiPairsCount: Int,
    val apiEventEntries: List<ApiEventEntry>
  ) {

    constructor(data: IApkExplorationOutput2) : this(
      data,
      data.uniqueApiLogsWithFirstTriggeringActionIndex,
      data.uniqueEventApiPairsWithFirstTriggeringActionIndex
    )

    private constructor(
      data: IApkExplorationOutput2,
      uniqueApiLogsWithFirstTriggeringActionIndex: Map<IApiLogcatMessage, Int>,
      uniqueEventApiPairsWithFirstTriggeringActionIndex: Map<EventApiPair, Int>
    ) : this(
      appPackageName = data.packageName,
      totalRunTime = data.explorationDuration,
      totalActionsCount = data.actRess.size,
      totalResetsCount = data.actions.count { it.base is ResetAppExplorationAction },
      exception = data.exception,
      uniqueApisCount = uniqueApiLogsWithFirstTriggeringActionIndex.keys.size,
      apiEntries = uniqueApiLogsWithFirstTriggeringActionIndex.map {
        val (apiLog: IApiLogcatMessage, firstIndex: Int) = it
        ApiEntry(
          time = Duration.between(data.explorationStartTime, apiLog.time),
          actionIndex = firstIndex,
          threadId = apiLog.threadId.toInt(),
          apiSignature = apiLog.uniqueString
        )
      },
      uniqueEventApiPairsCount = uniqueEventApiPairsWithFirstTriggeringActionIndex.keys.size,
      apiEventEntries = uniqueEventApiPairsWithFirstTriggeringActionIndex.map {
        val (eventApiPair, firstIndex: Int) = it
        val (event: String, apiLog: IApiLogcatMessage) = eventApiPair
        ApiEventEntry(
          ApiEntry(
            time = Duration.between(data.explorationStartTime, apiLog.time),
            actionIndex = firstIndex,
            threadId = apiLog.threadId.toInt(),
            apiSignature = apiLog.uniqueString
          ),
          event = event
        )
      }
    )

    companion object {
      val IApkExplorationOutput2.uniqueApiLogsWithFirstTriggeringActionIndex: Map<IApiLogcatMessage, Int> get() {
        return this.actRess.uniqueItemsWithFirstOccurrenceIndex(
          extractItems = { it.result.deviceLogs.apiLogsOrEmpty },
          extractUniqueString = { it.uniqueString }
        )
      }

      val IApkExplorationOutput2.uniqueEventApiPairsWithFirstTriggeringActionIndex: Map<EventApiPair, Int> get() {
        
        return this.actRess.uniqueItemsWithFirstOccurrenceIndex(
          extractItems = RunnableExplorationActionWithResult::extractEventApiPairs,
          extractUniqueString = EventApiPair::uniqueString
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
      private val eventPadEnd: Int = 69
    }

    override fun toString(): String {
      val actionIndexFormatted = "${apiEntry.actionIndex}".padStart(actionIndexPad)
      val eventFormatted = event.padEnd(eventPadEnd)
      val threadIdFormatted = "${apiEntry.threadId}".padStart(threadIdPad)

      return "${apiEntry.time.minutesAndSeconds} $actionIndexFormatted  $eventFormatted $threadIdFormatted  ${apiEntry.apiSignature}"
    }
  }
}

