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
import java.nio.file.Files
import java.nio.file.Path

class Summary(val data: ExplorationOutput2, file: Path): DataFile(file) {

  private val summaryString: String by lazy {
    // KJA2 (reporting) next
    Resource("apk_exploration_summary_header.txt").extractedPath.text + "\n" + (data.first()?.apk?.packageName ?: "no apks")
  }
  override fun writeOut() {
    Files.write(file, summaryString.toByteArray())
  }
}