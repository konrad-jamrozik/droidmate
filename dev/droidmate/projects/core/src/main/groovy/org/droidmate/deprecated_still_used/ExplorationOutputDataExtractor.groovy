// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.util.logging.Slf4j
import org.droidmate.apis.ExcludedApis
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common.logcat.Api
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.*
import org.droidmate.exploration.output.FilteredApis
import org.droidmate.logcat.IApiLogcatMessage

import java.time.LocalDateTime

import static java.time.temporal.ChronoUnit.MILLIS

@SuppressWarnings("GroovyAssignabilityCheck")
// Turns of the writer.write() warnings that cannot distinguish between String and Writable.
@Slf4j
@Deprecated
class ExplorationOutputDataExtractor implements IExplorationOutputDataExtractor
{

  public static final String secondsPassedHeaderText = " seconds_passed"

  /** The value of this constant will be converted to "nan" string understandable by pgfplots as "do not plot this data point". */
  private static final int    nanInt          = -1
  private static final String DataColumnWidth = 3

  private final Configuration            config


  ExplorationOutputDataExtractor(Configuration config)
  {
    this.config = config
  }
  
  // KJA2-1 to remove
  @Override
  void pgfplotsChartInputData(Map cfgMap, ExplorationOutput explorationOutput, Writer writer)
  {
    assert cfgMap?.timeTickSize != null
    assert cfgMap?.timeTicks != null
    assert explorationOutput != null
    assert writer != null

    int timeTickSize = cfgMap.timeTickSize
    int timeTicks = cfgMap.timeTicks
    boolean perEvent = cfgMap.perEvent ?: false

    // First column header.
    List<String> header = [secondsPassedHeaderText]

    // First column data, for X axis labels: seconds passed.
    List<Double> secondsPassed = (0..timeTicks).collect {(double) it / (1000 / timeTickSize)}

    // 2nd and remaining columns headers: app package names.
    explorationOutput.each {header << sprintf("%s", it.header)}

    // 2nd and remaining columns data, the data series: unique APIs seen for given app until given amount of seconds have passed.
    List<List<Integer>> uniqApisUntilSecondsForEachApk = computeUniqApisUntilSecondsForEachApk(explorationOutput, secondsPassed, timeTickSize, perEvent, /* AppGuard apis */ false)

    if (config.outputAppGuardCharts)
    {
      explorationOutput.each {header << sprintf("%s", "AppGuard-" + it.header)}
      uniqApisUntilSecondsForEachApk += computeUniqApisUntilSecondsForEachApk(explorationOutput, secondsPassed, timeTickSize, perEvent, /* AppGuard apis */ true)
    }

    // Assert the data has correct number of columns.
    assert header.size() == 1 + uniqApisUntilSecondsForEachApk.size()
    // Assert the data columns have correct length.
    assert uniqApisUntilSecondsForEachApk.every {it.size() == secondsPassed.size()}

    List<List<String>> formattedColumnsData = formatColumnsForPgfplotsChartInputDataTable(secondsPassed, uniqApisUntilSecondsForEachApk)
    outputPgfplotsChartInputDataTable(header, formattedColumnsData, writer)
  }

  /**
   * Returns a list of table column values. Each item of the list, i.e. each column, represents values for given apk.
   * The values for given apk represent the number of API seen until given amount of seconds have passed,
   * as given in the input list {@code secondsPassed}.
   */
  // KJA2-1 to remove
  private List<List<Integer>> computeUniqApisUntilSecondsForEachApk(
    ExplorationOutput explorationOutput, List<Double> secondsPassed, int timeTickSize, boolean perEvent, boolean appGuardApis)
  {
    return explorationOutput.collect {IApkExplorationOutput aeo ->

      // No APIs calls have ever been logged.
      if (aeo.apiLogs.flatten().empty)
        return secondsPassed.collect {nanInt}

      List<List<IApiLogcatMessage>> filteredApiLogs = filterApiLogs(aeo.apiLogs, aeo.appPackageName, appGuardApis)

      List<Long> uniqueApiSeenMillis = perEvent ?
        computeUniqueApiSeenMillisPerEvent(filteredApiLogs, aeo.monitorInitTime, aeo.actions) :
        computeUniqueApiSeenMillis(filteredApiLogs, aeo.monitorInitTime)

      List<Integer> uniqueApisSeenUntilSecondsForEachApk = secondsPassed.collect {Double currentSeconds ->

        LocalDateTime lastApiLogTime = filteredApiLogs.flatten().isEmpty() ? null : filteredApiLogs.flatten()*.time.sort().last()

        Integer uniqApisSeenInCurrentTick = currentSeconds == 0 ? 0 : uniqueApiSeenMillis
          .findAll {((currentSeconds * 1000) - timeTickSize <= it) && (it <= currentSeconds * 1000)}.size()

        Integer uniqApisInCurrentSeconds = uniqueApiSeenMillis.findAll {it <= currentSeconds * 1000}.size()

        assert uniqApisInCurrentSeconds >= uniqApisSeenInCurrentTick

        if (!enoughDataToComputeApiCallTimes(aeo, uniqApisSeenInCurrentTick, uniqApisInCurrentSeconds))
          return nanInt

        return returnApisUnlessExplorationEnded(aeo, currentSeconds, lastApiLogTime, uniqApisSeenInCurrentTick, uniqApisInCurrentSeconds)
      }

      return uniqueApisSeenUntilSecondsForEachApk
    }
  }

  /**
   * <p>
   * Filters out all API logs that:<br/>
   * - have a stack trace that does not contain any frame starting with the package name of the explored app;<br/>
   * - are redundant APIs
   * (see {@link ExplorationOutputDataExtractor#possiblyRedundantApiCalls(ExplorationOutput, java.io.Writer)
   *});<br/>
   * - (if a flag is set) are not in app guard APIs list
   * (see {@link ExplorationOutputDataExtractor#isInAppGuardApiList(org.droidmate.logcat.IApiLogcatMessage)
   *});<br/>
   * - (if a flag is set) are in {@link org.droidmate.apis.ExcludedApis}.<br/>
   * </p><p>
   * In addition, this method ensures there are no calls to {@code Socket.&lt;init>} made by monitor TCP server (source of the server:
   * {@code org.droidmate.uiautomator_daemon.MonitorJavaTemplate.MonitorTCPServer});
   *
   * </p><p>
   * All such logs are expected to be removed from monitor logs by monitor, before being transferred to the host machine.
   *
   * </p>
   *
   */
  // KJA2-1 to remove
  @Deprecated
  /// !!! DUPLICATION WARNING !!! org.droidmate.exploration.output.FilteredApis
  private List<List<IApiLogcatMessage>> filterApiLogs(List<List<IApiLogcatMessage>> apiLogs, String appPackageName, boolean appGuardApis = false)
  {
    List<List<IApiLogcatMessage>> out = []

    apiLogs.each {List<IApiLogcatMessage> logsPerMethod ->

      List<IApiLogcatMessage> currentFilteredList = []

      logsPerMethod.each {IApiLogcatMessage log ->
        List<String> st = log.stackTrace.split(Api.stack_trace_frame_delimiter)

        assert !FilteredApis.isStackTraceOfMonitorTcpServerSocketInit(st): "The Socket.<init> monitor logs were expected to be removed by monitor before being sent to the host machine."
        if (!FilteredApis.isStackTraceOfRedundantApiCall(st))
          currentFilteredList << log
      }

      if (config.removeHardCodedApis)
      {
        currentFilteredList = currentFilteredList.findAll {!(new ExcludedApis().contains(it.methodName))}
      }

      currentFilteredList = currentFilteredList.findAll {!it.isCallToStartInternalActivity(appPackageName)}

      out << currentFilteredList
    }
    assert apiLogs.size() == out.size()
    out.each {assert it != null}
    return out
  }

  private static boolean enoughDataToComputeApiCallTimes(
    IApkExplorationOutput apkExplorationOutput, int uniqApisSeenInCurrentTick, int uniqApisInCurrentSeconds)
  {
    // True if the explored apk was not inlined or possibly true if exception was thrown during exploration.
    if (apkExplorationOutput.monitorInitTime == null)
    {
      assert uniqApisSeenInCurrentTick == 0
      assert uniqApisInCurrentSeconds == 0
      return false
    }

    if (apkExplorationOutput.actions.size() == 0)
    {
      assert apkExplorationOutput.caughtException != null
      assert uniqApisSeenInCurrentTick == 0
      assert uniqApisInCurrentSeconds == 0
      return false
    }

    assert apkExplorationOutput.monitorInitTime != null
    assert apkExplorationOutput.actions.size() > 0
    return true
  }

  private static int returnApisUnlessExplorationEnded(
    IApkExplorationOutput aeo, double currentSecondsPassed, LocalDateTime lastApiCallLogTime,
    int uniqApisSeenInCurrentTick,
    int uniqApisInCurrentSeconds)
  {
    /*
      Here we compare against max of exploration end time and last API call log time, instead of only exploration end time, as
      last API call log time can be later than the exploration end time. This is because exploration end time comes from the
      clock on the machine running DroidMate and last API call log time comes from the clock of the Android device, and these
      two clock may not be synced to the millisecond. Empirical observations show that after manual syncing by resetting the
      clocks at minute turnover they are off by 1-2 seconds after one day.

      See also: org.droidmate.deprecated_still_used.ApkExplorationOutput.clockImprecision
     */
    if (currentSecondsPassed > MILLIS.between(aeo.monitorInitTime, [aeo.explorationEndTime, lastApiCallLogTime].max()) / 1000)
    {
      if (uniqApisSeenInCurrentTick > 0)
        return uniqApisInCurrentSeconds

      return nanInt
    } else
      return uniqApisInCurrentSeconds
  }

  static List<Long> computeUniqueApiSeenMillis(List<List<IApiLogcatMessage>> apiLogs, LocalDateTime monitorInitTime)
  {
    Collection<List<IApiLogcatMessage>> logsByMethod = apiLogs.flatten().groupBy {it.uniqueString}.values()

    List<Long> newUniqueApiSeenMillis = logsByMethod.collect {List<IApiLogcatMessage> logsForMethod ->

      LocalDateTime firstLogForMethodTime = logsForMethod.collect {it.time}.sort().first()

      return monitorInitTime.until(firstLogForMethodTime, MILLIS)

    }.sort()

    return newUniqueApiSeenMillis
  }

  List<Long> computeUniqueApiSeenMillisPerEvent(List<List<IApiLogcatMessage>> apiLogs, LocalDateTime monitorInitTime, List<TimestampedExplorationAction> actions)
  {
    List<List> uniqueLogEventTripletsByFirstLog = computeFirstUniqueLogsEventsPairsWithActionIndexes(apiLogs, actions)

    List<Long> newUniqueLogEventPairsSeenMillis = uniqueLogEventTripletsByFirstLog.collect {
      return monitorInitTime.until((it[0] as IApiLogcatMessage).time, MILLIS)
    }

    return newUniqueLogEventPairsSeenMillis

  }

  private List<List> computeFirstUniqueLogsEventsPairsWithActionIndexes(List<List<IApiLogcatMessage>> apiLogs, List<TimestampedExplorationAction> actions)
  {
    int currActionIndex = -1;
    List<List> logActionIndexTriplets = apiLogs
      .collect {logsForAction ->
      currActionIndex++; logsForAction.collect {
        [it, actions[currActionIndex], currActionIndex + 1]
      }
    }.flatten().collate(3)

    if (logActionIndexTriplets == [[]])
      logActionIndexTriplets = []

    Map logActionIndexTripletsByUniqueLogEventPair = logActionIndexTriplets.groupBy {List logActionIndexTriplet ->
      def log = logActionIndexTriplet[0] as IApiLogcatMessage
      def action = logActionIndexTriplet[1] as TimestampedExplorationAction
      def actionIndex = logActionIndexTriplet[2] as int
      return [log.uniqueString, extractUniqueEvent(action, log)]
    }

    List<List> uniqueLogEventActionIndexTripletsByFirstLog = logActionIndexTripletsByUniqueLogEventPair.values().collect {

      List logActionTripletsList ->
        LocalDateTime minLogTime = logActionTripletsList.collect {List logActionTriplet ->
          def log = logActionTriplet[0] as IApiLogcatMessage
          def action = logActionTriplet[1] as TimestampedExplorationAction
          def actionIndex = logActionTriplet[2] as int
          return log.time
        }.min()

        List tripletsWithMinTime = logActionTripletsList.findAll {(it[0] as IApiLogcatMessage).time == minLogTime}

        assert tripletsWithMinTime.size() >= 1
        return tripletsWithMinTime[0]
    }
    return uniqueLogEventActionIndexTripletsByFirstLog
  }

  private static int eventDisplayWidth = 60
  /**
   * Maps given timestamped exploration action into a string representing an abstract class of all exploration actions that
   * are considered equivalent when determining pairs of [API call, unique event], used e.g. in generating saturation charts.
   *
   * @param timestampedAction
   * @return
   */
  private String extractUniqueEvent(TimestampedExplorationAction timestampedAction, IApiLogcatMessage apiLog)
  {
    ExplorationAction action = timestampedAction.explorationAction

    if (action.class in [ResetAppExplorationAction, TerminateExplorationAction])
    {
      return "<reset>".padRight(eventDisplayWidth, ' ')
    } else if (action instanceof EnterTextExplorationAction)
    {
      if (!(apiLog.threadId in ["1", "?"]))
        return "background".padRight(eventDisplayWidth, ' ')

      return (getWidgetUniqueEventString((action as EnterTextExplorationAction))).padRight(eventDisplayWidth, ' ')
    } else if (action instanceof WidgetExplorationAction)
    {
      if (!(apiLog.threadId in ["1", "?"]))
        return "background".padRight(eventDisplayWidth, ' ')

      return (getWidgetUniqueEventString((action as WidgetExplorationAction))).padRight(eventDisplayWidth, ' ')
    } else throw new UnexpectedIfElseFallthroughError()
  }

  private String getWidgetUniqueEventString(ExplorationAction wa)
  {
    String clickString
    Widget w
    if (wa instanceof WidgetExplorationAction)
    {
      w = wa.widget
      String clickType = wa.longClick ? "l-click" : "click"
      clickString = "$clickType:"
    } else
    {
      assert wa instanceof EnterTextExplorationAction
      w = wa.widget
      clickString = "enterText:"
    }
    List<String> parts = []

    if (config.widgetUniqueStringWithFieldPrecedence)
    {
      if (w?.resourceId?.size() > 0)
        parts += "res:" + w.strippedResourceId
      else if (w?.contentDesc?.size() > 0)
        parts += "dsc:" + w.contentDesc
      else if (w?.text?.size() > 0)
        parts += "txt:" + w.text
    } else
    {
      if (w?.resourceId?.size() > 0)
        parts += "res:" + w.strippedResourceId

      if (w?.contentDesc?.size() > 0)
        parts += "dsc:" + w.contentDesc

      if (w?.text?.size() > 0)
        parts += "txt:" + w.text
    }

    if (parts.size() > 0)
      return clickString + parts
    else
      return "unlabeled"
  }

  /**
   * Returns a list of table columns to be output directly to a text file. Each column is a list of values, one per each row.
   */
  static List<List<String>> formatColumnsForPgfplotsChartInputDataTable(List<Double> secondsPassed, List<List<Integer>> uniqApisUntilSecondsForEachApk)
  {

    List<List<String>> output = []

    output << secondsPassed.collect {sprintf("%${secondsPassedHeaderText.length()}.1f", it)}

    uniqApisUntilSecondsForEachApk.each {
      output << it.collect {
        if (it != -1)
          sprintf("%${DataColumnWidth}d", it)
        else
        // "nan" explanation: pgfplots interprets it as "do not plot this data point"
          sprintf("%${DataColumnWidth}s", "nan")
      }
    }
    return output
  }

  static void outputPgfplotsChartInputDataTable(List<String> header, List<List<String>> columnsData, Writer writer)
  {
    writer.write(header.join(" ") + "\n")
    (0..columnsData[0].size() - 1).each {int rowIndex ->
      writer.write(columnsData.collect {it[rowIndex]}.join(" ") + "\n")
    }
    writer.close()
  }
}