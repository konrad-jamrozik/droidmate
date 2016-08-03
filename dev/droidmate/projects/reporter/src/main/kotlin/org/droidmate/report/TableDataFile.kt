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

import com.google.common.collect.Table
import org.droidmate.withExtension
import java.nio.file.Files
import java.nio.file.Path

class TableDataFile<R, C, V>(val table: Table<R, C, V>, file: Path) : DataFile(file) {

  override fun writeOut() {
    Files.write(file, tableString.toByteArray())
  }

  fun writeOutPlot() {
    plot(
      dataFilePath = file.toAbsolutePath().toString(),
      outputFilePath = plotFile.toAbsolutePath().toString())
  }
  
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
  
  override fun toString(): String{
    return file.toString()
  }
}



