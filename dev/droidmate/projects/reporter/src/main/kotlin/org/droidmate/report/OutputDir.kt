package org.droidmate.report

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.storage.Storage2
import java.nio.file.Path

class OutputDir(val dir: Path) {

  fun read() : ExplorationOutput2
  {
    return ExplorationOutput2.from(Storage2(dir))
  }
}