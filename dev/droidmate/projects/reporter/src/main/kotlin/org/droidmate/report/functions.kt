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
import kotlin.comparisons.compareBy
import kotlin.comparisons.naturalOrder

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