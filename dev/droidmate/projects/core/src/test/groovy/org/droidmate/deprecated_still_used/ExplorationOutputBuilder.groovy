// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import org.droidmate.common.DroidmateException
import org.droidmate.common.logcat.Api
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.ExplorationAction
import org.droidmate.exploration.actions.ExplorationActionTestHelper
import org.droidmate.logcat.IApiLogcatMessage

import java.time.LocalDateTime

import static org.droidmate.common.logcat.ApiLogcatMessageTestHelper.newApiLogcatMessage

@Deprecated
class ExplorationOutputBuilder
{
  private static int apkNameIndex    = 0
  private static int methodNameIndex = 0

  private ApkExplorationOutput currentlyBuiltApkExplOutput
  private ExplorationOutput    builtOutput = []

  ExplorationOutput returnBuiltList() {builtOutput}

  static ExplorationOutput build(
    Map attributes = [:],
    @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ExplorationOutputBuilder) Closure buildDefinition)
  {
    def builder = new ExplorationOutputBuilder()
    apkNameIndex = 0

    buildDefinition.delegate = builder
    buildDefinition.resolveStrategy = Closure.DELEGATE_FIRST
    buildDefinition()
    return builder.returnBuiltList()
  }

  void apk(Map attributes, Closure apkBuildDefinition)
  {
    assert attributes.monitorInitTime instanceof LocalDateTime

    String appPackageName = attributes.name ?: "apk${apkNameIndex++}"

    currentlyBuiltApkExplOutput = ApkExplorationOutput.create(
      appPackageName: appPackageName, monitorInitTime: attributes.monitorInitTime)

    apkBuildDefinition.delegate = this
    apkBuildDefinition.resolveStrategy = Closure.DELEGATE_ONLY
    apkBuildDefinition()
    builtOutput << currentlyBuiltApkExplOutput
  }

  void explorationAction(Map attributes)
  {
    Long mssSinceMonitorInit = attributes.mssSinceMonitorInit ?: 0
    String actionString = attributes.action

    ExplorationAction action
    switch (actionString) {
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
    TimestampedExplorationAction builtAction = TimestampedExplorationAction.from(action, monitorInitPlusMss(mssSinceMonitorInit))

    currentlyBuiltApkExplOutput.actions << builtAction
  }

  void apiLogs(Map attributes)
  {
    List<Integer> mssSinceMonitorInit = attributes.mssSinceMonitorInit ?: []
    List<String> methodNames = attributes.methodNames ?: []

    assert mssSinceMonitorInit.size() == methodNames.size()
    int logsCount = methodNames.size()

    List<IApiLogcatMessage> builtApiLogs = []

    if (logsCount > 0)
    {
      (0..logsCount-1).each {int i ->
        builtApiLogs << newApiLogcatMessage(
          time: monitorInitPlusMss(mssSinceMonitorInit[i]),
          methodName: methodNames[i],
          // Minimal stack trace to pass all the validation checks.
          // In particular, the ->Socket.<init> is enforced by asserts in org.droidmate.exploration.output.FilteredApis.isStackTraceOfMonitorTcpServerSocketInit
          stackTrace: "$Api.monitorRedirectionPrefix->Socket.<init>->$currentlyBuiltApkExplOutput.appPackageName"
        )
      }
    }

    currentlyBuiltApkExplOutput.apiLogs << builtApiLogs

  }

  void explorationEnd(Map attributes)
  {
    Long mssSinceMonitorInit = attributes.mssSinceMonitorInit ?: 0
    currentlyBuiltApkExplOutput.explorationEndTime = monitorInitPlusMss(mssSinceMonitorInit)
  }

  void caughtException(Map attributes)
  {
    Long mssSinceMonitorInit = attributes.mssSinceMonitorInit ?: 0
    currentlyBuiltApkExplOutput.caughtException = new DroidmateException("Exception created by ExplorationOutputBuilder.build()")
    currentlyBuiltApkExplOutput.explorationEndTime = monitorInitPlusMss(mssSinceMonitorInit)
  }

  private LocalDateTime monitorInitPlusMss(Long mss)
  {
    return currentlyBuiltApkExplOutput.monitorInitTime.plusNanos((mss * 1000000) as long)
  }
}
