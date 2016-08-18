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

import org.droidmate.android_sdk.ApkTestHelper
import org.droidmate.apis.Api
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

import org.droidmate.apis.ApiLogcatMessageTestHelper

class ExplorationOutput2Builder
{

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
    assert attributes.explorationStartTime instanceof LocalDateTime
    assert attributes.explorationEndTimeMss instanceof Integer

    String packageName = attributes.name
    this.currentlyBuiltApkOut2 = new ApkExplorationOutput2(
      ApkTestHelper.build(
        packageName,
        "${packageName}/${packageName}.MainActivity",
        "$packageName" + "1",
        "applicationLabel"
      )
    )
    this.currentlyBuiltApkOut2.explorationStartTime = attributes.explorationStartTime
    this.currentlyBuiltApkOut2.explorationEndTime = explorationStartPlusMss(attributes.explorationEndTimeMss as Integer)

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
    Integer mssSinceExplorationStart = attributes.mss ?: 0
    LocalDateTime timestamp = explorationStartPlusMss(mssSinceExplorationStart)

    def runnableAction = parseRunnableAction(attributes.action as String, timestamp)
    return runnableAction
  }

  public ExplorationActionRunResult buildActionResult(Map attributes)
  {
    def deviceLogs = buildDeviceLogs(attributes)
    def guiSnapshot = attributes.guiSnapshot ?: UiautomatorWindowDumpTestHelper.newHomeScreenWindowDump() 

    def successful = attributes.containsKey("successful") ? attributes.successful : true

    def exception = successful ? new DeviceExceptionMissing() :
      new DeviceException("Exception created in ${ExplorationOutput2Builder.simpleName}.buildActionResult()")

    def packageName = attributes.packageName ?: currentlyBuiltApkOut2.packageName
    assert packageName?.size() >= 1
    
    def result = new ExplorationActionRunResult(successful, packageName, deviceLogs, guiSnapshot, exception)
    return result
  }


  private IDeviceLogs buildDeviceLogs(Map attributes)
  {
    List<List> apiLogs = attributes.logs ?: []

    def deviceLogs = new DeviceLogs()

    deviceLogs.apiLogs = apiLogs.collect {

      assert it.size() == 2
      def methodName = it[0] as String
      def mssSinceExplorationStart = it[1] as Integer

      return ApiLogcatMessageTestHelper.newApiLogcatMessage(
        time: explorationStartPlusMss(mssSinceExplorationStart),
        methodName: methodName,
        // Minimal stack trace to pass all the validation checks.
        // In particular, the ->Socket.<init> is enforced by asserts in org.droidmate.report.FilteredDeviceLogs.Companion.isStackTraceOfMonitorTcpServerSocketInit
        stackTrace: "$Api.monitorRedirectionPrefix->Socket.<init>->$currentlyBuiltApkOut2.packageName"
      )
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

  private LocalDateTime explorationStartPlusMss(Integer mss)
  {
    return datePlusMss(this.currentlyBuiltApkOut2.explorationStartTime, mss)
  }

  private LocalDateTime datePlusMss(LocalDateTime date, Integer mss)
  {
    return date.plusNanos((mss * 1000000) as long)
  }
}
