package org.droidmate.report

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ExplorationOutput2Report(val output: ExplorationOutput2) {

  fun report(): Unit {
    // KJA current work
    output.forEach {
      GUICoverageReportFile(it).writeOut()
    }
  }
}

class GUICoverageReportFile(val it: IApkExplorationOutput2) {

  fun writeOut() {

    val file = Paths.get("./temp_dir_for_tests/${it.apk.fileName}_GUIReportFile.txt")
    GUICoverage(it).table().writeOut(file)
  }

}

fun <R, C, V> Table<R, C, V>.writeOut(file: Path) {
  val cellsString = this.cellSet().joinToString { it.toString() }
  Files.write(file, cellsString.toByteArray())
}

class GUICoverage(val data: IApkExplorationOutput2) {
  fun table(): Table<Int, Int, Int> {
    var table = HashBasedTable.create<Int, Int, Int>()
    table.put(0, 0, 10)
    table.put(1, 0, 15)
    table.put(2, 0, 15)
    return table
  }
}
