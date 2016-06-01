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
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path

fun <R, C, V> Table<R, C, V>.writeOut(file: Path) {

  val headerRowString = this.columnKeySet().joinToString(separator = "\t")

  val dataRowsStrings: List<String> = this.rowMap().map {
    val rowValues = it.value.values
    rowValues.joinToString(separator = "\t")
  }

  val tableString = headerRowString + "\n" + dataRowsStrings.joinToString(separator = "\n")

  Files.write(file, tableString.toByteArray())
}

// KJA comment
// Reference: http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
fun Int.zeroDigits(digitsToZero: Int): Long {
  return BigDecimal(this.toString()).setScale(-digitsToZero, RoundingMode.DOWN).toBigInteger().toLong()
}