// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.data_aggregators

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DroidmateError
import org.droidmate.exploration.actions.*
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.storage.IStorage2

import java.time.LocalDateTime

import static java.time.temporal.ChronoUnit.MILLIS

@Slf4j
class ApkExplorationOutput2 implements IApkExplorationOutput2
{

  private static final long serialVersionUID = 1

  final IApk apk

  List<RunnableExplorationActionWithResult> actRess = new ArrayList<>()

  LocalDateTime explorationStartTime = null
  LocalDateTime explorationEndTime = null

  ApkExplorationOutput2(IApk apk)
  {
    this.apk = apk
    assert apk != null
  }

  @Override
  String getPackageName()
  {
    this.apk.packageName
  }

  @Override
  void add(IRunnableExplorationAction action, IExplorationActionRunResult result)
  {
    assert action != null
    assert result != null
    actRess.add(new RunnableExplorationActionWithResult(action, result))
  }

  @Override
  void setExplorationStartTime(LocalDateTime time)
  {
    assert time != null
    assert explorationStartTime == null
    this.explorationStartTime = time
  }


  @Override
  void setExplorationEndTime(LocalDateTime time)
  {
    assert time != null
    assert explorationEndTime == null
    this.explorationEndTime = time
  }

  @Override
  void verify() throws DroidmateError
  {
    try
    {
      assert this.actRess.size() >= 1
      assert this.containsExplorationStartTime
      assertFirstActionIsReset()
      assertLastActionIsTerminateOrResultIsFailure()
      assertLastGuiSnapshotIsHomeOrResultIsFailure()
      assertOnlyLastActionMightHaveDeviceException()
      assertLogsAreSortedByTime()
    } catch (AssertionError e)
    {
      throw new DroidmateError(e)
    }
  }

  public void assertLogsAreSortedByTime()
  {
    List<IApiLogcatMessage> apiLogs = this.actRess*.result*.deviceLogs*.apiLogsOrEmpty.flatten() as List<IApiLogcatMessage>
    List<LocalDateTime> apiLogsSortedTimes = apiLogs*.time.collect().sort()

    assert explorationStartTime <= explorationEndTime

    assert apiLogs.sortedByTimePerPID()

    if (!apiLogsSortedTimes.empty)
    {
      assert explorationStartTime <= apiLogsSortedTimes.first()
      assert apiLogsSortedTimes.last() <= explorationEndTime
    }

  }

  void assertOnlyLastActionMightHaveDeviceException()
  {
    assert this.actRess.dropRight(1).every {RunnableExplorationActionWithResult pair ->
      return pair.result.successful
    }
  }

  @Override
  Integer getExplorationTimeInMs()
  {
    return MILLIS.between(explorationStartTime, explorationEndTime)
  }

  @Override
  boolean getContainsExplorationStartTime()
  {
    return this.explorationStartTime != null
  }

  @Override
  boolean getNoException()
  {
    boolean lastResultSuccessful = actRess.last().result.successful
    assert lastResultSuccessful || (actRess.last().result.exception != null)
    return lastResultSuccessful
  }

  @Override
  DeviceException getException()
  {
    assert !noException
    return actRess.last().result.exception
  }

  @Override
  List<List<IApiLogcatMessage>> getApiLogs()
  {
    return this.actRess.collect {it.result.deviceLogs.apiLogsOrEmpty}
  }

  @Override
  List<IDeviceGuiSnapshot> getGuiSnapshots()
  {
    return this.actRess.collect {it.result.guiSnapshot}
  }

  void assertFirstActionIsReset()
  {
    assert actRess.first().action instanceof RunnableResetAppExplorationAction
  }

  void assertLastActionIsTerminateOrResultIsFailure()
  {
    RunnableExplorationActionWithResult lastActionPair = actRess.last()
    assert !lastActionPair.result.successful || lastActionPair.action instanceof RunnableTerminateExplorationAction
  }

  void assertLastGuiSnapshotIsHomeOrResultIsFailure()
  {
    RunnableExplorationActionWithResult lastActionPair = actRess.last()
    assert !lastActionPair.result.successful || lastActionPair.result.guiSnapshot.guiState.homeScreen
  }


  @Override
  void serialize(IStorage2 storage2)
  {
    storage2.serialize(this, packageName)
  }
}
