// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.data_aggregators

import org.droidmate.android_sdk.ApkTestHelper
import org.droidmate.common.logcat.Api
import org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceExceptionMissing
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.ExplorationAction
import org.droidmate.exploration.actions.ExplorationActionRunResult
import org.droidmate.exploration.actions.ExplorationActionTestHelper
import org.droidmate.exploration.actions.RunnableExplorationAction
import org.droidmate.exploration.device.DeviceLogs
import org.droidmate.exploration.device.IDeviceLogs

import java.time.LocalDateTime

import static org.droidmate.common.logcat.ApiLogcatMessageTestHelper.newApiLogcatMessage

class ExplorationOutput2Builder
{

  private LocalDateTime         currentlyBuiltApkOut2monitorInitTime
  private ApkExplorationOutput2 currentlyBuiltApkOut2
  private ExplorationOutput2    builtOutput = []

  static ExplorationOutput2 build(
    @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ExplorationOutput2Builder) Closure buildDefinition)
  {
    def builder = new ExplorationOutput2Builder()

    buildDefinition.delegate = builder
    buildDefinition.resolveStrategy = Closure.DELEGATE_FIRST
    buildDefinition()
    return builder.builtOutput
  }

  void apk(Map attributes, Closure apkBuildDefinition)
  {
    assert attributes.name instanceof String
    assert attributes.monitorInitTime instanceof LocalDateTime
    assert attributes.explorationEndTimeMss instanceof Integer

    String packageName = attributes.name
    this.currentlyBuiltApkOut2monitorInitTime = attributes.monitorInitTime
    this.currentlyBuiltApkOut2 = new ApkExplorationOutput2(
      ApkTestHelper.build(
        packageName,
        "${packageName}/${packageName}.MainActivity",
        "$packageName" + "1"
      )
    )
    this.currentlyBuiltApkOut2.explorationEndTime = monitorInitPlusMss(attributes.explorationEndTimeMss as Integer)

    apkBuildDefinition()

    this.currentlyBuiltApkOut2.verify()

    builtOutput << currentlyBuiltApkOut2
  }

  void actRes(Map attributes)
  {
    RunnableExplorationAction runnableAction = buildRunnableAction(attributes)
    ExplorationActionRunResult result = buildActionResult(attributes)
    currentlyBuiltApkOut2.add(runnableAction, result)
  }

  public RunnableExplorationAction buildRunnableAction(Map attributes)
  {
    assert attributes.mss instanceof Integer
    Integer mssSinceMonitorInit = attributes.mss ?: 0
    LocalDateTime timestamp = monitorInitPlusMss(mssSinceMonitorInit)

    def runnableAction = parseRunnableAction(attributes.action as String, timestamp)
    return runnableAction
  }

  public ExplorationActionRunResult buildActionResult(Map attributes)
  {
    def deviceLogs = buildDeviceLogs(attributes)
    def guiSnapshot = UiautomatorWindowDumpTestHelper.newHomeScreenWindowDump()

    def successful = attributes.containsKey("successful") ? attributes.successful : true

    def exception = successful ? new DeviceExceptionMissing() :
      new DeviceException("Exception created in ${ExplorationOutput2Builder.simpleName}.buildActionResult()")

    def result = new ExplorationActionRunResult(successful, deviceLogs, guiSnapshot, exception)
    return result
  }


  private IDeviceLogs buildDeviceLogs(Map attributes)
  {
    List<List> apiLogs = attributes.logs ?: []

    def deviceLogs = new DeviceLogs()

    deviceLogs.apiLogs = apiLogs.collect {

      assert it.size() == 2
      def methodName = it[0] as String
      def mssSinceMonitorInit = it[1] as Integer

      return newApiLogcatMessage(
        time: monitorInitPlusMss(mssSinceMonitorInit),
        methodName: methodName,
        // Minimal stack trace to pass all the validation checks.
        // In particular, the ->Socket.<init> is enforced by asserts in org.droidmate.exploration.output.FilteredApis.isStackTraceOfMonitorTcpServerSocketInit
        stackTrace: "$Api.monitorRedirectionPrefix->Socket.<init>->$currentlyBuiltApkOut2.packageName"
      )
    }

    deviceLogs.instrumentationMsgs = []

    if (this.currentlyBuiltApkOut2monitorInitTime != null)
    {
      deviceLogs.monitorInitTime = this.currentlyBuiltApkOut2monitorInitTime
      this.currentlyBuiltApkOut2monitorInitTime = null
    }

    return deviceLogs
  }

  RunnableExplorationAction parseRunnableAction(String actionString, LocalDateTime timestamp)
  {
    ExplorationAction action
    switch (actionString)
    {
      case "reset":
        action = ExplorationAction.newResetAppExplorationAction()
        break
      case "click":
        action = ExplorationActionTestHelper.newWidgetClickExplorationAction()
        break
      case "terminate":
        action = ExplorationAction.newTerminateExplorationAction()
        break
      default:
        throw new UnexpectedIfElseFallthroughError()

    }
    return RunnableExplorationAction.from(action, timestamp)
  }

  private LocalDateTime monitorInitPlusMss(Integer mss)
  {
    if (currentlyBuiltApkOut2.containsMonitorInitTime)
      return monitorInitPlusMss(currentlyBuiltApkOut2.monitorInitTime, mss)
    else
      return monitorInitPlusMss(currentlyBuiltApkOut2monitorInitTime, mss)
  }

  private LocalDateTime monitorInitPlusMss(LocalDateTime monitorInit, Integer mss)
  {
    return monitorInit.plusNanos((mss * 1000000) as long)
  }
}
