package org.droidmate.report

import org.droidmate.exploration.data_aggregators.ExplorationOutput2

class ExplorationOutput2Report(val output: ExplorationOutput2) {

    fun report(): Unit {
      output.map { println(it.apk.fileName) }
    }
}