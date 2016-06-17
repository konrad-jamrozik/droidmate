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

    fun buildString(
      explorationTitle: String,
      totalRunTime: Duration,
      totalActionsCount: Int,
      totalResetsCount: Int,
      uniqueApisCount: Int,
      apiEntries: List<String>,
      uniqueApiEventPairsCount: Int,
      apiEventEntries: List<String>
    )
      : String {
      return StringBuilder(template)
        .replaceVariable("exploration_title", explorationTitle)
        .replaceVariable("total_run_time", totalRunTime.minutesAndSeconds)
        .replaceVariable("total_actions_count", totalActionsCount.toString().padStart(4, ' '))
        .replaceVariable("total_resets_count", totalResetsCount.toString().padStart(4, ' '))
        .replaceVariable("unique_apis_count", uniqueApisCount.toString())
        // KJA this will have to be formatted, as well as apiEventEntries
        .replaceVariable("api_entries", apiEntries.joinToString(separator = "\n"))
        .replaceVariable("unique_api_event_pairs_count", uniqueApiEventPairsCount.toString())
        .replaceVariable("api_event_entries", apiEventEntries.joinToString(separator = "\n"))
        .toString()
    }
  }

  val summaryString: String by lazy {
    // KJA (reporting) current work
    if (data.isEmpty())
      "Exploration output was empty (no apks), so this summary is empty."
    else
      Resource("apk_exploration_summary_header.txt").extractedPath.text + "\n" + "TODO"
  }
  override fun writeOut() {
    Files.write(file, summaryString.toByteArray())
  }
}