// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.Table
import org.droidmate.withExtension
import java.nio.file.Files
import java.nio.file.Path

class TableDataFile<R, C, V>(val table: Table<R, C, V>, file: Path) : DataFile(file) {
  
  private val tableString: String by lazy {
    val headerRowString = table.columnKeySet().joinToString(separator = "\t")

    val dataRowsStrings: List<String> = table.rowMap().map {
      val rowValues = it.value.values
      rowValues.joinToString(separator = "\t")
    }

    val tableString = headerRowString + "\n" + dataRowsStrings.joinToString(separator = "\n")
    tableString
  }

  val plotFile by lazy { file.withExtension("pdf") }

  fun writeOutPlot() {
    plot(
      dataFilePath = file.toAbsolutePath().toString(),
      outputFilePath = plotFile.toAbsolutePath().toString())
  }

  override fun writeOut() {
    Files.write(file, tableString.toByteArray())
  }

  override fun toString(): String{
    return file.toString()
  }
}



