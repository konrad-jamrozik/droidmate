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

import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import com.konradjamrozik.Resource
import com.konradjamrozik.isDirectory
import com.konradjamrozik.isRegularFile
import org.droidmate.extractedPathString
import org.zeroturnaround.exec.ProcessExecutor
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.comparisons.compareBy
import kotlin.comparisons.naturalOrder

fun plot(dataFilePath: String, outputFilePath: String) {

  require(Paths.get(dataFilePath).isRegularFile, {"Paths.get(dataFilePath='$dataFilePath').isRegularFile"})
  require(Paths.get(outputFilePath).parent.isDirectory, {"Paths.get(outputFilePath='$outputFilePath').parent.isDirectory"})
  
  val processExecutor = ProcessExecutor()
    .exitValueNormal()
    .readOutput(true)
    .timeout(5, TimeUnit.SECONDS)
    .destroyOnExit()

  val plotTemplatePathString = Resource("plot_template.plt").extractedPathString

  val variableBindings = listOf(
    "var_interactive=0",
    "var_data_file_path='$dataFilePath'",
    "var_output_file_path='$outputFilePath'")
    .joinToString(";")
  val result = processExecutor.command("gnuplot", "-e", variableBindings, plotTemplatePathString).execute()
  check(result.exitValue == 0)
}

fun <V> buildTable(headers: Iterable<String>, rowCount: Int, computeRow: (Int) -> Iterable<V>): Table<Int, String, V> {

  require(rowCount >= 0)

  val builder = ImmutableTable.Builder<Int, String, V>()
    .orderColumnsBy(compareBy<String> { headers.indexOf(it) })
    .orderRowsBy(naturalOrder<Int>())

  val rows: List<Pair<Int, Iterable<V>>> = 0.rangeTo(rowCount - 1).step(1)
    .map { rowIndex ->
      val row = computeRow(rowIndex)
      check(headers.count() == row.count())
      Pair(rowIndex, row)
    }

  rows.forEach { row: Pair<Int, Iterable<V>> ->
    row.second.forEachIndexed { columnIndex: Int, columnValue: V ->
      builder.put(row.first, headers.elementAt(columnIndex), columnValue)
    }
  }

  return builder.build()
}