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
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class GUICoverage(val data: IApkExplorationOutput2) {
  fun toTable(): Table<Int, String, Int> {
    //var table = HashBasedTable.create<Int, String, Int>()
    var table = ImmutableTable.Builder<Int, String, Int>()
      .orderColumnsBy(compareBy {
        when (it) { "time" -> 0; "GUI elements seen" -> 1 else -> throw UnexpectedIfElseFallthroughError()
        }
      })
      .orderRowsBy(naturalOrder<Int>())
      .put(0, "time", 10)
      .put(1, "time", 20)
      .put(2, "time", 30)
      .put(3, "time", 40)
      .put(4, "time", 50)
      .put(5, "time", 60)
      .put(0, "GUI elements seen", 0)
      .put(1, "GUI elements seen", 1)
      .put(2, "GUI elements seen", 3)
      .put(3, "GUI elements seen", 7)
      .put(4, "GUI elements seen", 16)
      .put(5, "GUI elements seen", 16)
      .build()
    return table
  }
}