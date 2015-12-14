// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

class WritableApkApisChart implements IWritableApkApisChart
{
  private final IApkApisChart chartData
  private final MillisecondSequence msSeq

  WritableApkApisChart(IApkApisChart chartData, int timeTicks, int timeTickSizeInMs)
  {
    this.chartData = chartData
    this.msSeq = new MillisecondSequence(timeTicks, timeTickSizeInMs)
  }

  @Override
  String getFileName()
  {
    return "chart-${chartData.packageName}.txt"
  }

  @Override
  void write(Writer wr)
  {
    wr.write(headerRow)
    columnsRows.each {wr.write(it)}
  }

  private String getHeaderRow()
  {
    return printRow([msSeq.print(), "droidmate-run:${chartData.packageName}"])
  }

  private String printRow(List<String> dataPoints)
  {
    return dataPoints.join(" ") + "\n"
  }

  private List<String> getColumnsRows()
  {
    int columnsCount = this.msSeq.size()

    return (0..columnsCount - 1).collect {int i ->

      IMilliseconds millisPassed = msSeq[i]
      IApiChartsTableDataPoint apiCallsCount = this.chartData.getUniqueApisCalledCountUntilMillis(millisPassed)

      return printRow([millisPassed.print(), apiCallsCount.print()])
    }
  }
}
