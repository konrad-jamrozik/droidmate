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
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.extractedPath
import org.droidmate.text
import java.nio.file.Files
import java.nio.file.Path

class Summary(val data: List<IApkExplorationOutput2>, file: Path): DataFile(file) {

  override fun writeOut() {
    Files.write(file, summaryString.toByteArray())
  }

  val summaryString: String by lazy {
    if (data.isEmpty())
      "Exploration output was empty (no apks), so this summary is empty."
    else
      Resource("apk_exploration_summary_header.txt").extractedPath.text + data.joinToString { it -> ApkSummary.build(it) }
  }

}