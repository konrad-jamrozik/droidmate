// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import org.droidmate.common.logcat.Api
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DroidmateError
import org.droidmate.exploration.actions.ResetAppExplorationAction
import org.droidmate.exploration.actions.TerminateExplorationAction
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.Duration
import java.time.LocalDateTime

@Slf4j
@Canonical
@Deprecated
class ApkExplorationOutput implements IApkExplorationOutput
{

  private static final long serialVersionUID = 1

  /**
   * Denotes the exploration from which this output was collected completed successfully, i.e. without throwing exception
   * that would terminate the exploration before correctly completing this output.
   *
   * @see ExplorationOutputCollector#collect(java.lang.String, groovy.lang.Closure)
   */
  boolean completed = false

  String appPackageName

  List<ITimeFormattedLogcatMessage> instrumentationMsgs = []

  List<TimestampedExplorationAction> actions = []

  List<IDeviceGuiSnapshot> guiSnapshots = []

  List<List<IApiLogcatMessage>> apiLogs = []

  Exception caughtException = null

  LocalDateTime monitorInitTime
  LocalDateTime explorationEndTime

  IApkExplorationOutput.IUiaTestCaseAnnotations annotations

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
  public static final Duration clockImprecision = Duration.ofMillis(3000)

  static ApkExplorationOutput create(Map params)
  {
    if (params.uiaTestCaseName != null)
      params.annotations = new UiaTestCaseAnnotations(testCaseName: params.uiaTestCaseName)
    params.remove("uiaTestCaseName")
    return new ApkExplorationOutput(params)
  }

  // WISH distinguish between "monitorInit is null because raw apk was deployed" and "monitorInit is null because exception
  // was thrown before the log was read"
  @Override
  void verifyCompletedDataIntegrity() throws DroidmateError
  {
    try
    {
      assert explorationEndTime != null
      assert !(apiLogs?.any {it == null})
      assert !(actions?.any {it == null})
      // WISH assertion error here. Maybe when the app crashes, api logs might be missing?
      // Maybe this was caused by the fact that in org.droidmate.device.SerializableTCPClient.queryServer
      // the ObjectOutputStream(socket.getOutputStream()) wasn't wrapped in proper try catch when the error occurred,
      // and so the catch was not triggered in
      // org.droidmate.exploration.device.ApiLogsReader.getAndClearMessagesFromMonitorTcpServer
      assert apiLogs?.size() == actions?.size()


      if (caughtException == null)
      {
        // WISH fails if exhausted all attempts to obtain Valid gui snapshot in
        // org.droidmate.deprecated.ValidUiautomatorWindowDumpProvider.getValidGuiSnapshot.
        // Looks like this is because the code returns MissingGuiSnapshot instead of throwing something,
        // and so no exception is being caught.
        assert actions?.size() >= 2


        if (!isUiaTestCase)
          assert guiSnapshots?.size() >= 2

        assert actions.first().explorationAction instanceof ResetAppExplorationAction
        if (!isUiaTestCase)
          assert guiSnapshots.first().guiState.belongsToApp(appPackageName)

        assert completed.implies(actions.last().explorationAction instanceof TerminateExplorationAction)
        if (!isUiaTestCase)
          assert completed.implies(guiSnapshots.last().guiState.homeScreen)

        assert explorationEndTime >= actions.last().timestamp

        // Assert exploration actions have increasing timestamps.
        assert actions*.timestamp == actions*.timestamp.collect().sort()

        if (monitorInitTime == null) // This can happen if the apk under exploration hadn't monitor inlined into it.
          assert apiLogs.flatten().empty
        else
        {
          if (!(explorationEndTime >= monitorInitTime - clockImprecision))
            log.warn("Exploration end time is earlier than the monitor init time, even with the allowed clock imprecision.\n" +
              "The monitor init time is             ${monitorInitTime},\n" +
              "the exploration end time is          ${explorationEndTime}\n" +
              "and the allowed clock imprecision is ${clockImprecision.toMillis()} ms.")

          List<IApiLogcatMessage> flatApiLogs = apiLogs.flatten()
          if (!(flatApiLogs.empty))
          {
            List<LocalDateTime> apiLogsTimes = flatApiLogs*.time.sort()

            /* We cannot assert that apiLogsTimes.first() >= monitorInitTime.
               Reason: timestamps are not sequential because the Log.i operation is not atomic within system.
               For details, see http://stackoverflow.com/a/13194705/986533 */
            if (!(apiLogsTimes.first() >= monitorInitTime - Duration.ofSeconds(10)))
              log.warn("First logged API call time is more than 10 seconds earlier than the monitor init time.\n" +
                "The monitor init time:       ${monitorInitTime},\n" +
                "the first API call log time: ${apiLogsTimes.first()}.")

            if (!(apiLogsTimes.last() <= explorationEndTime + clockImprecision))
              log.warn("Last logged API call time is later than the exploration end time, even with the allowed clock " +
                "imprecision.\n" +
                "The exploration end time is          ${explorationEndTime},\n" +
                "the last logged API call time is     ${apiLogsTimes.last()}\n" +
                "and the allowed clock imprecision is ${clockImprecision.toMillis()} ms.")

            assert flatApiLogs.sortedByTimePerPID()

            boolean warningLogged = false

            // Assert API logs logged after given exploration action have higher timestamps than the action.
            apiLogs.eachWithIndex {List<IApiLogcatMessage> apiLogsForAction, int i ->

              apiLogsForAction.each {
                if (!(it.time >= actions[i].timestamp - clockImprecision))
                {
                  if (!warningLogged)
                    log.warn("One of the API calls logged after $i-th exploration action has earlier time than " +
                      "the $i-th exploration action, even with allowed clock imprecision.\n" +
                      "The offending API call has time   of ${it.time},\n" +
                      "the exploration action time       is ${actions[i].timestamp},\n" +
                      "and the allowed clock imprecision is ${clockImprecision.toMillis()} ms.\n" +
                      "There might be more offending API calls logged, after the same or other exploration actions. " +
                      "If this is the case, the information about them won't be output to avoid spamming " +
                      "diagnostic output.")
                  warningLogged = true
                }
              }
            }
          }
        }
      }

      if (apiLogs != null)
      {
        // Check being made here: ensure that stack traces have to come from DroidMate-provided monitor.
        List<List<String>> stackTraces = apiLogs*.stackTrace.flatten().collect {it.split("->") as List<String>}
        stackTraces.each {List<String> stackTrace ->
          assert stackTrace.any {it.contains(Api.monitorRedirectionPrefix)}
        }
      }

      if (isUiaTestCase)
      {
        assert annotations?.testCaseName?.size() > 0
      }
    } catch (AssertionError e)
    {
      throw new DroidmateError("Verification of exploration output from $appPackageName failed with: " +
        "<exception message cut out due to IntelliJ hanging on too long stdout>. " +
        "As a consequence, the output is malformed and attempts of further processing will either throw an exception or result " +
        "in bogus results.", e)
    }
  }

  public static IApkExplorationOutput from(IApkExplorationOutput2 apkout2)
  {
    // @formatter:off
    String                             packageName        = apkout2.packageName
    // monitorInitTime is assigned explorationStartTime because the new format no longer has monitorInitTime.
    LocalDateTime                      monitorInitTime    = apkout2.explorationStartTime
    List<ITimeFormattedLogcatMessage>  instrMsgs          = apkout2.actRess.first().result.deviceLogs.instrumentationMsgsOrNull
    List<TimestampedExplorationAction> actions            = apkout2.actRess.collect { TimestampedExplorationAction.from(it.action.base, it.action.timestamp) }
    List<IDeviceGuiSnapshot>           guiSnapshots       = apkout2.actRess.collect { it.result.guiSnapshot }
    List<List<IApiLogcatMessage>>      apiLogs            = apkout2.actRess.collect { it.result.deviceLogs.apiLogsOrEmpty }
    DeviceException                    caughtException    = (apkout2.noException) ? null : apkout2.exception
    LocalDateTime                      explorationEndTime = apkout2.explorationEndTime
    // @formatter:on
    boolean completed = true
    IApkExplorationOutput.IUiaTestCaseAnnotations annotations = null

    def out = new ApkExplorationOutput(completed, packageName, instrMsgs, actions, guiSnapshots, apiLogs, caughtException, monitorInitTime, explorationEndTime, annotations)
    out.verifyCompletedDataIntegrity()
    return out
  }

  @Override
  boolean getIsUiaTestCase()
  {
    return annotations != null
  }

  @Override
  String getHeader()
  {
    if (isUiaTestCase)
      return annotations.testCaseName + ":" + appPackageName
    else
      return "droidmate-run:" + appPackageName
  }

  @Override
  List<String> getComments()
  {
    assert isUiaTestCase
    return annotations.comments
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  // To be used in debugging sessions.
  private void debugTimestamps()
  {
    println "app: " + appPackageName
    actions.eachWithIndex {TimestampedExplorationAction action, int i ->
      println "action: " + action.explorationAction.class.simpleName + " time: " + action.timestamp
      apiLogs[i].each {
        println "api log time: " + it.time
      }
    }
  }


  @Override
  public String toString()
  {
    return """\
ApkExplorationOutput{
    completed=$completed,
    appPackageName='$appPackageName',
    # of instrumentationMsgs=${instrumentationMsgs.size()},
    # of actions=${actions.size()},
    # of guiSnapshots=${guiSnapshots.size()},
    # of apiLogs=${apiLogs.size()},
    caughtException=$caughtException,
    monitorInitTime=$monitorInitTime,
    explorationEndTime=$explorationEndTime,
    annotations=$annotations
}"""
  }
}
