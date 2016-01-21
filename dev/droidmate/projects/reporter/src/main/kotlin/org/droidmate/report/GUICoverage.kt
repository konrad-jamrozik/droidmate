// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2

class GUICoverage(val data: IApkExplorationOutput2) {
  fun toTable(): Table<Int, String, Int> {
    var table = HashBasedTable.create<Int, String, Int>()
    table.put(0, "time", 10)
    table.put(1, "time", 20)
    table.put(2, "time", 30)
    table.put(3, "time", 40)
    table.put(4, "time", 50)
    table.put(5, "time", 60)
    table.put(0, "GUI elements seen", 0)
    table.put(1, "GUI elements seen", 1)
    table.put(2, "GUI elements seen", 3)
    table.put(3, "GUI elements seen", 7)
    table.put(4, "GUI elements seen", 16)
    table.put(5, "GUI elements seen", 16)
    return table
  }
}