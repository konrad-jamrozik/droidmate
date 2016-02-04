// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.logcat.IApiLogcatMessage

import java.time.temporal.ChronoUnit

class ApkApisChart implements IApkApisChart
{

  IApkExplorationOutput2 apkOut
  IFilteredApis          fApis

  /** The value of this constant represents a "nan" string understandable by pgfplots as "do not plot this data point". */
  public static final int nanInt = -1

  ApkApisChart(IApkExplorationOutput2 apkOut)
  {
    this.apkOut = apkOut
    this.fApis = new FilteredApis(this.apkOut.apiLogs, this.apkOut.packageName)
  }

  @Override
  String getPackageName()
  {
    return this.apkOut.packageName
  }

  @Override
  IApiChartsTableDataPoint getUniqueApisCalledCountUntilMillis(IMilliseconds millisPassed)
  {
    assert this.apkOut.containsExplorationStartTime

    if (this.apkOut.explorationTimeInMs <= (millisPassed.value - millisPassed.tickSizeInMs))
      return new ApiChartsTableDataPoint(nanInt)

    if (this.apkOut.apiLogs.empty)
      return new ApiChartsTableDataPoint(0)

    if ((millisPassed.value) == 0)
      return new ApiChartsTableDataPoint(0)

    assert this.apkOut.containsExplorationStartTime
    assert this.apkOut.explorationTimeInMs >= (millisPassed.value - millisPassed.tickSizeInMs)
    assert !(this.apkOut.apiLogs.empty)
    assert millisPassed.value > 0

    List<Long> uniqueApisMillisOfFirstOccurrence = getUniqueApisMillisOfFirstOccurrence(this.fApis)
    return getUniqueApisSeenCountUntilMillisPassed(millisPassed, uniqueApisMillisOfFirstOccurrence)
  }

  private List<Long> getUniqueApisMillisOfFirstOccurrence(IFilteredApis fApis)
  {
    return fApis.groupByUniqueString().collect {getTimeFromExplorationStartUntil(firstLog(it))}.sort()
  }

  private IApiLogcatMessage firstLog(List<IApiLogcatMessage> apiLogs)
  {
    apiLogs.sort {it.time}.first()
  }

  private long getTimeFromExplorationStartUntil(IApiLogcatMessage apiLog)
  {
    this.apkOut.explorationStartTime.until(apiLog.time, ChronoUnit.MILLIS)
  }

  private ApiChartsTableDataPoint getUniqueApisSeenCountUntilMillisPassed(IMilliseconds millisPassed, List<Long> uniqueApisMillisOfFirstOccurrence)
  {
    Integer uniqueApisSeenCountUntilMillis = uniqueApisMillisOfFirstOccurrence.findAll {it <= millisPassed.value}.size()
    return new ApiChartsTableDataPoint(uniqueApisSeenCountUntilMillis)
  }
}

