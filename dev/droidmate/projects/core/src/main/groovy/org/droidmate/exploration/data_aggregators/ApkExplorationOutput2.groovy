// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.exploration.data_aggregators

import groovy.util.logging.Slf4j
import org.droidmate.TimeDiffWithTolerance
import org.droidmate.android_sdk.IApk
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exceptions.DroidmateError
import org.droidmate.exploration.actions.*
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.storage.IStorage2

import java.time.Duration
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

  ApkExplorationOutput2(IApk apk, List<RunnableExplorationActionWithResult> actRess, LocalDateTime explorationStartTime, LocalDateTime explorationEndTime)
  {
    this.apk = apk
    this.actRess = actRess
    this.explorationStartTime = explorationStartTime
    this.explorationEndTime = explorationEndTime
    assert this.apk != null
    assert this.actRess != null
    this.verify()
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
      assert this.containsExplorationEndTime
      
      assertFirstActionIsReset()
      assertLastActionIsTerminateOrResultIsFailure()
      assertLastGuiSnapshotIsHomeOrResultIsFailure()
      assertOnlyLastActionMightHaveDeviceException()
      assertDeviceExceptionIsMissingOnSuccessAndPresentOnFailureNeverNull()

      assertLogsAreSortedByTime()
      warnIfTimestampsAreIncorrectWithGivenTolerance()
      
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
  
  void assertDeviceExceptionIsMissingOnSuccessAndPresentOnFailureNeverNull()
  {
    boolean lastResultSuccessful = actRess.last().result.successful
    assert exception != null
    assert lastResultSuccessful == exception instanceof DeviceExceptionMissing
  }
  
  void assertOnlyLastActionMightHaveDeviceException()
  {
    assert this.actRess.dropRight(1).every {RunnableExplorationActionWithResult pair ->
      return pair.result.successful
    }
  }

  void warnIfTimestampsAreIncorrectWithGivenTolerance()
  {
    /**
     * <p>
     * Used for time comparisons allowing for some imprecision.
     *
     * </p><p>
     * Some time comparisons in DroidMate happen between time obtained from an Android device and a time obtained from the machine
     * on which DroidMate runs. Because these two computers most likely won't have clocks synchronized with millisecond precision,
     * this variable is incorporated in such time comparisons.
     *
     * </p>
     */
    def diff = new TimeDiffWithTolerance(Duration.ofSeconds(3))
    warnIfExplorationStartTimeIsNotBeforeEndTime(diff)
    warnIfExplorationStartTimeIsNotBeforeFirstLogTime(diff)
    warnIfLastLogTimeIsNotBeforeExplorationEndTime(diff)
    warnIfLogsAreNotAfterAction(diff)
  }

  private boolean warnIfExplorationStartTimeIsNotBeforeEndTime(TimeDiffWithTolerance diff)
  {
    return diff.warnIfBeyond(this.explorationStartTime, this.explorationEndTime, "exploration start time", "exploration end time")
  }

  private void warnIfExplorationStartTimeIsNotBeforeFirstLogTime(TimeDiffWithTolerance diff)
  {
    if (!this.apiLogs.empty)
    {
      def firstLog = this.apiLogs.find {!it.empty}?.first()
      if (firstLog != null)
        diff.warnIfBeyond(this.explorationStartTime, firstLog.time, "exploration start time", "first API log")
    }
  }

  private void warnIfLastLogTimeIsNotBeforeExplorationEndTime(TimeDiffWithTolerance diff)
  {
    if (!this.apiLogs.empty)
    {
      def lastLog = this.apiLogs.find {!it.empty}?.last()
      if (lastLog != null)
        diff.warnIfBeyond(lastLog.time, this.explorationEndTime, "last API log", "exploration end time")
    }
  }

  private void warnIfLogsAreNotAfterAction(TimeDiffWithTolerance diff)
  {
    this.actRess.each {
      if (!it.result.deviceLogs.apiLogsOrEmpty.empty)
      {
        def actionTime = it.action.timestamp
        def firstLogTime = it.result.deviceLogs.apiLogsOrEmpty.first().time
        diff.warnIfBeyond(actionTime, firstLogTime, "action time", "first log time for action")
      }
    }
  }
  
  @Override
  Integer getExplorationTimeInMs()
  {
    return MILLIS.between(explorationStartTime, explorationEndTime)
  }
  
  @Override
  Duration getExplorationDuration()
  {
    return Duration.between(explorationStartTime, explorationEndTime)
  }

  @Override
  boolean getContainsExplorationStartTime()
  {
    return this.explorationStartTime != null
  }

  @Override
  boolean getContainsExplorationEndTime()
  {
    return this.explorationEndTime != null
  }


  @Override
  boolean getExceptionIsPresent()
  {
    return !(exception instanceof DeviceExceptionMissing)
  }
  
  @Override 
  DeviceException getException()
  {
    return actRess.last().result.exception
  }

  @Override
  List<List<IApiLogcatMessage>> getApiLogs()
  {
    return this.actRess.collect {it.result.deviceLogs.apiLogsOrEmpty}
  }

  @Override
  List<IRunnableExplorationAction> getActions()
  {
    return this.actRess.collect {it.action}
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
