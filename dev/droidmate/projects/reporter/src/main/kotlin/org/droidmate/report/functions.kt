// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import com.konradjamrozik.Resource
import org.droidmate.extractedPathString
import org.zeroturnaround.exec.ProcessExecutor
import java.util.concurrent.TimeUnit
import kotlin.comparisons.compareBy
import kotlin.comparisons.naturalOrder

fun plot(dataFilePath: String, outputFilePath: String) {

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
  println(result.outputString())
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