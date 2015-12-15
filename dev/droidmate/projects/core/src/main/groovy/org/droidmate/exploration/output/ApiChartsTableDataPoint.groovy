// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

class ApiChartsTableDataPoint implements IApiChartsTableDataPoint
{

  private static final int printWidth = 3

  private final int data

  ApiChartsTableDataPoint(int data)
  {
    this.data = data
  }

  @Override
  String print()
  {
    if (data == ApkApisChart.nanInt)
      return sprintf("%${printWidth}s", "nan")

    return sprintf("%${printWidth}d", data)
  }
}
