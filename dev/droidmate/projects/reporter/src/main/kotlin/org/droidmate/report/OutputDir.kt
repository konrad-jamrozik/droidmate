// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.deleteDir
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.storage.Storage2
import java.nio.file.Files
import java.nio.file.Path

class OutputDir(val dir: Path) {

  val explorationOutput2: ExplorationOutput2 by lazy {
    ExplorationOutput2.from(Storage2(dir))
  }

  val notEmptyExplorationOutput2: ExplorationOutput2 by lazy {
    check(explorationOutput2.isNotEmpty(), { "Check failed: explorationOutput2.isNotEmpty()" })
    explorationOutput2
  }

  fun clearContents()
  {
    if (Files.exists(dir))
    {
      Files.list(dir).forEach {
        if (Files.isDirectory(it))
          it.deleteDir()
        else
          Files.delete(it)
      }
    }
  }
}