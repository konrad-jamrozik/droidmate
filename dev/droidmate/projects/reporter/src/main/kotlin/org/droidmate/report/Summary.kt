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
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.extractedPath
import org.droidmate.text
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

class Summary(val data: ExplorationOutput2, file: Path): DataFile(file) {

  companion object {
    val template: String by lazy {
      Resource("apk_exploration_summary_template.txt").text
    }

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
      uniqueApisCount: Int,
      apiEntries: List<String>,
      uniqueApiEventPairsCount: Int,
      apiEventEntries: List<String>
    ): String {

      // KJA 3 next. See "P" bookmark. 
      val explorationTitle = "droidmate-run:" + appPackageName
      return replaceVariables(
        explorationTitle,
        totalRunTime.minutesAndSeconds,
        totalActionsCount.toString().padStart(4, ' '),
        totalResetsCount.toString().padStart(4, ' '),
        uniqueApisCount.toString(),
        apiEntries.joinToString(separator = "\n"),
        uniqueApiEventPairsCount.toString(), apiEventEntries.joinToString(separator = "\n"))
    }

    private fun replaceVariables(
      explorationTitle: String,
      totalRunTime: String,
      totalActionsCount: String,
      totalResetsCount: String,
      uniqueApisCount: String,
      apiEntries: String,
      uniqueApiEventPairsCount: String,
      apiEventEntries: String
    )
      : String {
      return StringBuilder(template)
        .replaceVariable("exploration_title", explorationTitle)
        .replaceVariable("total_run_time", totalRunTime)
        .replaceVariable("total_actions_count", totalActionsCount)
        .replaceVariable("total_resets_count", totalResetsCount)
        .replaceVariable("unique_apis_count", uniqueApisCount)
        .replaceVariable("api_entries", apiEntries)
        .replaceVariable("unique_api_event_pairs_count", uniqueApiEventPairsCount)
        .replaceVariable("api_event_entries", apiEventEntries)
        .toString()
    }
  }

  val summaryString: String by lazy {
    if (data.isEmpty())
      "Exploration output was empty (no apks), so this summary is empty."
    else
      Resource("apk_exploration_summary_header.txt").extractedPath.text + buildSummary(data)
  }
  override fun writeOut() {
    Files.write(file, summaryString.toByteArray())
  }
}