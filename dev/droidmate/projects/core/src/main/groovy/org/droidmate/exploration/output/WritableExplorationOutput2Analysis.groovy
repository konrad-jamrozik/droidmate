// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.storage.IWritableDirectory

class WritableExplorationOutput2Analysis implements IWritableExplorationOutput2Analysis
{

  private final ExplorationOutput2          explOut2
  private final List<IWritableApkApisChart> apkCharts

  WritableExplorationOutput2Analysis(ExplorationOutput2 explorationOutput2, Integer timeTicks, Integer timeTickSizeInMs)
  {
    this.explOut2 = explorationOutput2
    this.apkCharts = this.explOut2.collect {
      new WritableApkApisChart(
        new ApkApisChart(it), timeTicks, timeTickSizeInMs)
    }

  }

  List<String> getWriteTargetsNames()
  {
    return this.apkCharts*.fileName
  }

  @Override
  void write(IWritableDirectory dir)
  {
    apkCharts.each {IWritableApkApisChart apkChart ->
      dir.withWriterFor(apkChart.fileName) {Writer wr ->
        apkChart.write(wr)
      }
    }
  }

}
