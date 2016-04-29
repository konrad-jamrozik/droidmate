// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.command.uia_test_cases

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.MonitorConstants
import org.droidmate.common.logcat.ApiLogcatMessage
import org.droidmate.common.logcat.TimeFormattedLogcatMessage
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.IExplorationOutputCollectorFactory
import org.droidmate.deprecated_still_used.TimestampedExplorationAction
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.ExplorationAction
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.droidmate.logcat.IUiaTestActionLogcatMessage
import org.droidmate.logcat.UiaTestActionLogcatMessage
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants

import java.time.LocalDateTime
import java.util.regex.Matcher

import static org.droidmate.common.Assert.assertRegexMatches
import static org.droidmate.exploration.actions.ExplorationAction.*

@Slf4j
@TypeChecked
class UiaTestCaseLogsProcessor implements IUiaTestCaseLogsProcessor
{
  // !!! DUPLICATION WARNING !!!
  // With org.droidmate.uia_manual_test_cases.SnapchatTestCases
  private static final String testCaseStartedPrefix = "Test case started. Name: ";
  private static final String testCaseFinished = "Test case finished.";
  private static final String actionType_click = "click";
  private static final String actionType_longClick = "longClick";
  private static final String actionType_enterText = "enterText";
  private static final String actionType_pressBack = "press back";

  private final IExplorationOutputCollectorFactory explorationOutputCollectorFactory


  UiaTestCaseLogsProcessor(IExplorationOutputCollectorFactory explorationOutputCollectorFactory)
  {
    this.explorationOutputCollectorFactory = explorationOutputCollectorFactory
  }

  @Override
  IApkExplorationOutput process(List<String> logs)
  {
    assert logs?.size() > 0

    List<ITimeFormattedLogcatMessage> logcatMessages = logs
      .findAll {it.size() > 0}
      .collect {TimeFormattedLogcatMessage.from(it)}

    ITimeFormattedLogcatMessage firstMsg = logcatMessages.first()
    ITimeFormattedLogcatMessage lastMsg = logcatMessages.last()

    // Assert the first message denotes test case start and extract test case name from it.
    assert firstMsg.tag == UiautomatorDaemonConstants.uiaTestCaseTag
    assertTestCaseStartPrefix(logcatMessages)
    String testCaseName = firstMsg.messagePayload - testCaseStartedPrefix
    assert testCaseName.size() > 0

    // Assert the last message denotes test case end.

    assert lastMsg.tag == UiautomatorDaemonConstants.uiaTestCaseTag
    assert lastMsg.messagePayload == testCaseFinished

    // String test case start & end messages.
    assert logcatMessages.size() > 2: "Expected more than 2 logcat messages. Instead there are: ${logcatMessages.size()}"
    logcatMessages = logcatMessages[1..-2]
    firstMsg = logcatMessages.first()

    // The click done by human that launched the app from apps screen. DroidMate instead issues "reset" exploration action.
    assert firstMsg.tag == UiautomatorDaemonConstants.uiaTestCaseTag
    LocalDateTime firstExplActionTime = firstMsg.time
    logcatMessages = logcatMessages.drop(1)
    firstMsg = logcatMessages.first()

    String backwardCompatibleMonitorTag = "Monitor "
    assert firstMsg.tag == MonitorConstants.tag_init || firstMsg.tag == backwardCompatibleMonitorTag
    assert firstMsg.messagePayload == MonitorConstants.msg_ctor_success

    // Drop the instrumentation redirection status messages.
    logcatMessages = logcatMessages.drop(1).dropWhile {it.tag == UiautomatorDaemonConstants.instrumentation_redirectionTag}
    firstMsg = logcatMessages.first()
    assert firstMsg.tag == MonitorConstants.tag_init || firstMsg.tag == backwardCompatibleMonitorTag
    assert firstMsg.messagePayload.startsWith(MonitorConstants.msgPrefix_init_success)

    // Extract monitor init time and app package name from message announcing monitor finished initializing.
    LocalDateTime monitorInitTime = firstMsg.time
    String appPackageName = firstMsg.messagePayload - MonitorConstants.msgPrefix_init_success - " "
    assert appPackageName == appPackageName.trim()

    logcatMessages = logcatMessages.drop(1)

    def apkExplOut = collectExplorationOutput(testCaseName, appPackageName, firstExplActionTime, monitorInitTime, logcatMessages)
    assert apkExplOut.isUiaTestCase
    return apkExplOut
  }

  private static void assertTestCaseStartPrefix(List<ITimeFormattedLogcatMessage> logcatMessages)
  {
    assert logcatMessages[0].messagePayload.startsWith(testCaseStartedPrefix):
      "logcatMessages[0].messagePayload.startsWith(testCaseStartedPrefix):\n" +
        "payload : ${logcatMessages[0].messagePayload}\n" +
        "prefix  : $testCaseStartedPrefix"
  }

  @SuppressWarnings("GroovyAssignmentToMethodParameter")
  private IApkExplorationOutput collectExplorationOutput(String testCaseName, String appPackageName, LocalDateTime firstExplActionTime, LocalDateTime monitorInitTime, List<ITimeFormattedLogcatMessage> logcatMessages)
  {
    assert logcatMessages.each {assert it.tag in [MonitorConstants.tag_api, UiautomatorDaemonConstants.uiaTestCaseTag]}
    // the reset app action is assumed to be done by the user manually before she starts the uia run.
    TimestampedExplorationAction firstAction = TimestampedExplorationAction.from(newResetAppExplorationAction(), firstExplActionTime)
    def collector = explorationOutputCollectorFactory.create(appPackageName)

    IApkExplorationOutput apkExplOut = collector.collect(/* uiaTestCaseName */ testCaseName) {IApkExplorationOutput apkExplOut ->

      apkExplOut.monitorInitTime = monitorInitTime
      apkExplOut.actions << firstAction
      apkExplOut.comments << "Reset the app by calling Package Manager through adb (Android Debug Bridge)."
      boolean lastLogWasEvent = true

      while (!logcatMessages.isEmpty())
      {
        if (logcatMessages[0].tag == MonitorConstants.tag_api)
        {
          consumeApiLogs(apkExplOut, logcatMessages)
          lastLogWasEvent = false
        } else if (logcatMessages[0].tag == UiautomatorDaemonConstants.uiaTestCaseTag)
        {
          if (lastLogWasEvent)
            apkExplOut.apiLogs << []
          consumeExplorationAction(apkExplOut, logcatMessages)
          lastLogWasEvent = true
        } else throw new UnexpectedIfElseFallthroughError()
      }
      if (lastLogWasEvent)
        apkExplOut.apiLogs << []

      def apis = apkExplOut.apiLogs.last()
      LocalDateTime lastEvent = [apkExplOut.actions.last().timestamp, !apis.isEmpty() ? apkExplOut.apiLogs.last().last().time : null].max()
      apkExplOut.actions << TimestampedExplorationAction.from(newTerminateExplorationAction(), lastEvent.plusSeconds(1))
      apkExplOut.apiLogs << []
      apkExplOut.comments << "Terminate the exploration."
      apkExplOut.explorationEndTime = lastEvent.plusSeconds(2)

      assert apkExplOut.actions.size() == apkExplOut.apiLogs.size()
    }

    return apkExplOut
  }


  private static void consumeApiLogs(
    IApkExplorationOutput output,
    List<ITimeFormattedLogcatMessage> msgs)
  {
    assert msgs?.size() > 0

    output.apiLogs.add(
      msgs.takeWhile {it.tag == MonitorConstants.tag_api}.collect {ApiLogcatMessage.from(it)} as List<IApiLogcatMessage>)

    while (!msgs.isEmpty() && msgs[0].tag == MonitorConstants.tag_api)
      msgs.remove(0)
  }


  private static void consumeExplorationAction(
    IApkExplorationOutput output,
    List<ITimeFormattedLogcatMessage> msgs)
  {
    assert msgs?.size() > 0

    IUiaTestActionLogcatMessage uiaMsg = UiaTestActionLogcatMessage.from(msgs[0])
    output.actions << TimestampedExplorationAction.from(toExplorationAction(uiaMsg), msgs[0].time)
    output.comments << uiaMsg.comment

    msgs.remove(0)
  }

  private static ExplorationAction toExplorationAction(IUiaTestActionLogcatMessage uiaMsg)
  {
    ExplorationAction ea

    // !!! DUPLICATION WARNING !!!
    // With org.droidmate.uia_manual_test_cases.SnapchatTestCases#actionType_*
    if (uiaMsg.actionType == actionType_click)
    {
      assert uiaMsg.widget != null
      ea = newWidgetExplorationAction(uiaMsg.widget)
    } else if (uiaMsg.actionType == actionType_longClick)
    {
      assert uiaMsg.widget != null
      ea = newWidgetExplorationAction(uiaMsg.widget, /* longClick: */ true)
    } else if (uiaMsg.actionType.startsWith(actionType_enterText))
    {
      assert uiaMsg.widget != null
      String textToEnter = extractTextToEnter(uiaMsg)
      ea = newEnterTextExplorationAction(textToEnter, uiaMsg.widget)
    } else if (uiaMsg.actionType == actionType_pressBack)
    {
      assert uiaMsg.widget == null
      ea = newPressBackExplorationAction()
    } else
      throw new UnexpectedIfElseFallthroughError()

    return ea
  }

  private static String extractTextToEnter(IUiaTestActionLogcatMessage uiaMsg)
  {
    Matcher m = uiaMsg.actionType =~ /enterText\[(.*)\]/
    assertRegexMatches(uiaMsg.actionType, m)
    assert (m[0] as List).size() == 2
    String textToEnter = (m[0] as List)[1] as String
    return textToEnter
  }

}
