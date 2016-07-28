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

open class TimeSeriesTable private constructor(val table: Table<Int, String, Int>) : Table<Int, String, Int> by table {

  constructor(
    maxTime: Int,
    headers: List<String>,
    columns: List<Map<Long, Int>>
  ) : this(TimeSeriesTable.build(maxTime, headers, columns))
  
  companion object {
    val partitionSize = 1000L
    fun build(
      maxTime: Int,
      headers: List<String>,
      columns: List<Map<Long, Int>>
    ): Table<Int, String, Int> {
      val timeRange: List<Long> = 0L.rangeTo(maxTime).step(partitionSize).toList()
      return buildTable(
        headers,
        rowCount = timeRange.size,
        computeRow = { rowIndex ->
          val timePassed = timeRange[rowIndex]
          listOf((timePassed / partitionSize).toInt()) + columns.map { it[timePassed]!! }
        }
      )
    }
    
  }
}