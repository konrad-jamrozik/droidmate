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
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.extractedPath
import org.droidmate.text
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

class Summary(val data: ExplorationOutput2, file: Path): DataFile(file) {

  override fun writeOut() {
    Files.write(file, summaryString.toByteArray())
  }

  val summaryString: String by lazy {
    if (data.isEmpty())
      "Exploration output was empty (no apks), so this summary is empty."
    else
      Resource("apk_exploration_summary_header.txt").extractedPath.text + buildSummary(data)
  }
  
  companion object {

    fun buildSummary(data: ExplorationOutput2): String
    {
      // KJA 2
      return "TODO"
    }
    
    fun buildString(
      appPackageName: String,
      totalRunTime: Duration,
      totalActionsCount: Int,
      totalResetsCount: Int,
      exception: DeviceException,
      uniqueApisCount: Int,
      apiEntries: List<ApiEntry>,
      uniqueApiEventPairsCount: Int,
      apiEventEntries: List<String>
    ): String {

      // KJA 3 next. See "P" bookmark. 

      // @formatter:off
      return StringBuilder(template)
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

}