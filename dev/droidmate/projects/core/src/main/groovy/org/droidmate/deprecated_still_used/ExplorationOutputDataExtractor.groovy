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
import org.droidmate.apis.ApiMapping
import org.droidmate.apis.ApiMethodSignature
import org.droidmate.apis.ExcludedApis
import org.droidmate.apis.IApi
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common.logcat.Api
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.*
import org.droidmate.exploration.output.FilteredApis
import org.droidmate.logcat.IApiLogcatMessage

import java.time.Duration
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

  public static final int actionsPad = 4

  private final boolean                  compareRuns
  private final Configuration            config
  private final List<ApiMethodSignature> appGuardApis


  ExplorationOutputDataExtractor(boolean compareRuns, Configuration config)
  {
    this.compareRuns = compareRuns
    this.config = config
    this.appGuardApis = ApiMapping.parseAppguardLegacyApis(config.appGuardApisList)
  }

  @Override
  void summary(ExplorationOutput output, Writer writer)
  {
    IApkExplorationOutput otherRun
    if (output.any {it.isUiaTestCase})
    {
      assert !compareRuns
      assert output.findAll {it.isUiaTestCase}.size() >= 1
      assert output.findAll {!it.isUiaTestCase}.size() == 1
      otherRun = output.find {!it.isUiaTestCase}

    } else if (compareRuns)
    {
      assert output.size() == 2
      assert output.every {!it.isUiaTestCase}
      assert output*.appPackageName.unique().size() == 1
    }

    output.each {IApkExplorationOutput aeo ->

      if (compareRuns)
      {
        assert output.findAll {it != aeo}.size() == 1
        otherRun = output.find {it != aeo}
      }

      writer.write("==============================================================\n")
      writer.write("$aeo.header\n")
      writer.write("==============================================================\n")
      writer.write("\n");

      writeUseCaseComments(aeo, writer)

      if (writeExceptionAndCheckIfPreMonitorInit(aeo, writer))
        return

      List<List<IApiLogcatMessage>> filteredApiLogs = filterApiLogs(aeo.apiLogs, aeo.appPackageName)
      int i = 0;
      // A collection of pairs of (API log, index of action after which the log was observed)
      List<List> logsWithActionIndexes = filteredApiLogs
        .collect {logs -> i++; logs.collect {[it, i]}}.flatten().collate(2)
      // This might happen if all logs have been filtered out. In such case, .collate() has transformed [] into [[]].
      if (logsWithActionIndexes == [[]])
        logsWithActionIndexes = []

      // A collection of pairs of (unique API log, index of first action after which the log was observed)
      List<List> firstUniqueLogsWithActionIndexes =
        logsWithActionIndexes.groupBy {(it[0] as IApi).uniqueString}.values().collect {it.first()}

      List<List> firstUniqueLogsEventsPairsWithActionIndexes = computeFirstUniqueLogsEventsPairsWithActionIndexes(filteredApiLogs, aeo.actions)

      String formattedActionsCount = "${aeo.actions.size()}".padLeft(actionsPad, ' ')

      writer.write("Total run time:      ${getDurationString(aeo.monitorInitTime, aeo.explorationEndTime)}\n")
      writer.write("Total actions count: $formattedActionsCount (including the final action terminating exploration)\n")

      if (!aeo.isUiaTestCase)
      {
        formattedActionsCount = "${aeo.actions.findAll {it.explorationAction instanceof ResetAppExplorationAction}.size()}".padLeft(actionsPad, ' ')
        writer.write("Total resets count:  $formattedActionsCount (including the initial action)\n")
      }
      writer.write("\n");

      writeUniqueApiCallsTimes(firstUniqueLogsWithActionIndexes, aeo, writer, otherRun)
      writeUniqueApiEventPairsCallsTimes(firstUniqueLogsEventsPairsWithActionIndexes, aeo, writer, otherRun)

      writer.write("\n")
    }
    writer.close()
  }

  private void writeUniqueApiCallsTimes(List<List> firstUniqueLogsWithActionIndexes, IApkExplorationOutput aeo, Writer writer, IApkExplorationOutput otherRun)
  {
    writer.write("--------------------------------------------------------------\n");
    writer.write("Unique API calls count observed in the run: ${firstUniqueLogsWithActionIndexes.size()}\n")
    writer.write("\n")
    writer.write("Below follows a list of first calls to unique APIs. It is to be read as follows:\n")

    if (aeo.isUiaTestCase)
      writer.write("<time of logging the unique API in DroidMate run for the first time, if any> <index of action that triggered the call, if any> | <time of logging the unique API for the first time> <index of action that triggered the call> <the API call data>\n")
    else
      writer.write("<time of logging the unique API for the first time> <index of action that triggered the call> <the API call data>\n")

    writer.write("\n")

    if (aeo.isUiaTestCase)
      writer.write(" DroidMate     | Use case       API signature\n")
    else if (compareRuns)
      writer.write(" Other         | This           API signature\n")
    else
      writer.write(" DroidMate     API signature\n")

    firstUniqueLogsWithActionIndexes.each {
      IApiLogcatMessage api = it[0] as IApiLogcatMessage
      int actionIndex = it[1] as int

      String apiLogTimeOffsetFormatted = getDurationString(aeo.monitorInitTime, api.time)

      String actionIndexFormatted = "$actionIndex".padLeft(actionsPad, ' ');

      if (aeo.isUiaTestCase || compareRuns)
      {

        int otherActionIndex = otherRun.apiLogs.findIndexOf {List<IApi> otherActionLogs ->
          otherActionLogs.any {it.uniqueString == api.uniqueString}
        }
        IApiLogcatMessage dmApi = otherRun.apiLogs[otherActionIndex].find {it.uniqueString == api.uniqueString}


        if (otherActionIndex >= 0)
        {
          String otherApiTimeOffsetFormatted = getDurationString(otherRun.monitorInitTime, dmApi.time)
          String otherActionIndexFormatted = "${otherActionIndex + 1}".padLeft(actionsPad, ' ');
          writer.write("$otherApiTimeOffsetFormatted $otherActionIndexFormatted | ")
        } else
          writer.write("         None! | ")
      }

      writer.write("$apiLogTimeOffsetFormatted $actionIndexFormatted ${apiPrint(api)}\n")
    }
    writer.write("\n")
  }

  private static String apiPrint(IApiLogcatMessage api)
  {
    String apiThreadId = api.threadId.padLeft(4, ' ')
    return "TId: $apiThreadId ${api.uniqueString}"
  }

  private void writeUniqueApiEventPairsCallsTimes(List<List> firstUniqueLogsEventsPairsWithActionIndexes, IApkExplorationOutput aeo, Writer writer, IApkExplorationOutput otherRun)
  {
    writer.write("--------------------------------------------------------------\n");
    writer.write("Unique [API call, event] pairs count observed in the run: ${firstUniqueLogsEventsPairsWithActionIndexes.size()}\n")
    writer.write("\n")
    writer.write("Below follows a list of first calls to unique [API call, event] pairs. It is to be read as follows:\n")

    if (aeo.isUiaTestCase)
      writer.write("<time of logging the unique API call from the unique [API call, event] in DroidMate run for the first time, if any> <index of action that triggered the call, if any> | <time of logging the unique API call from the unique [API call, event] for the first time> <index of action that triggered the call> <the event data> <the API call data>\n")
    else
      writer.write("<time of logging the unique API call from the unique [API call, event] for the first time> <index of action that triggered the call> <the event data> <the API call data>\n")

    writer.write("\n")

    if (aeo.isUiaTestCase)
      writer.write(" DroidMate     | Use case       " + "Event".padRight(eventDisplayWidth, ' ') + " API signature\n")
    else if (compareRuns)
      writer.write(" Other         | This           " + "Event".padRight(eventDisplayWidth, ' ') + " API signature\n")
    else
      writer.write(" DroidMate     " + "Event".padRight(eventDisplayWidth, ' ') + " API signature\n")

    firstUniqueLogsEventsPairsWithActionIndexes.each {
      IApiLogcatMessage api = it[0] as IApiLogcatMessage
      String event = extractUniqueEvent(it[1] as TimestampedExplorationAction, api)
      int actionIndex = it[2] as int

      String apiLogTimeOffsetFormatted = getDurationString(aeo.monitorInitTime, api.time)

      String actionIndexFormatted = "$actionIndex".padLeft(actionsPad, ' ');

      if (aeo.isUiaTestCase || compareRuns)
      {

        int currActionIndex = -1
        int otherActionIndex = otherRun.apiLogs.findIndexOf {List<IApi> otherActionLogs ->
          currActionIndex++
          otherActionLogs.any {
            it.uniqueString == api.uniqueString
          } && extractUniqueEvent(otherRun.actions[currActionIndex], api) == event
        }

        IApiLogcatMessage dmApi = otherRun.apiLogs[otherActionIndex].find {it.uniqueString == api.uniqueString}


        if (otherActionIndex >= 0)
        {
          String dmApiTimeOffsetFormatted = getDurationString(otherRun.monitorInitTime, dmApi.time)
          String dmActionIndexFormatted = "${otherActionIndex + 1}".padLeft(actionsPad, ' ');
          writer.write("$dmApiTimeOffsetFormatted $dmActionIndexFormatted | ")
        } else
          writer.write("         None! | ")
      }

      writer.write("$apiLogTimeOffsetFormatted $actionIndexFormatted $event ${apiPrint(api)}\n")
    }
    writer.write("\n")
  }


  private static boolean writeExceptionAndCheckIfPreMonitorInit(IApkExplorationOutput aeo, Writer writer)
  {
    boolean exceptionThrownBeforeMonitorInit = false
    if (aeo.caughtException != null)
    {
      writer.write("WARNING! This exploration threw an exception.\n")
      writer.write("\n")
      writer.write("Exception message: '${aeo.caughtException.message}'.\n")
      writer.write("\n")
      writer.write(LogbackConstants.err_log_msg + "\n")
      writer.write("\n")

      if (aeo.monitorInitTime == null)
      {
        writer.write("The exception was thrown even before API call monitor initialized. No more data available.\n")
        exceptionThrownBeforeMonitorInit = true
      }
      writer.write("\n")
    }
    return exceptionThrownBeforeMonitorInit
  }

  private static String getDurationString(LocalDateTime start, LocalDateTime endTime)
  {
    Duration actionTimeOffset = Duration.between(start, endTime)
    int m = actionTimeOffset.toMinutes()
    int s = actionTimeOffset.seconds - m * 60
    String actionTimeOffsetFormatted = "$m".padLeft(4, ' ') + "m " + "$s".padLeft(2, ' ') + "s"
    return actionTimeOffsetFormatted
  }

  private static void writeUseCaseComments(IApkExplorationOutput aeo, Writer writer)
  {
    if (aeo.isUiaTestCase)
    {
      writer.write("// Manually-written description of the actions of the use case:\n")
      writer.write("//\n");
      aeo.comments.eachWithIndex {String comment, int i ->
        String istr = "${i + 1}".padLeft(2, ' ');
        writer.write("// $istr. $comment\n")
      }
      writer.write("\n");
    }
  }

  @Override
  void actions(ExplorationOutput output, Writer writer)
  {
    output.each {IApkExplorationOutput aeo ->

      writer.write("==============================================================\n")
      writer.write("$aeo.header\n")
      writer.write("==============================================================\n")
      writer.write("\n");

      int actionsCount = aeo.actions.size()
      writer.write("Exploration actions count: $actionsCount\n")
      writer.write("\n")

      if (writeExceptionAndCheckIfPreMonitorInit(aeo, writer))
        return

      Set<String> uniqApisSeenSoFar = []

      List<List<IApiLogcatMessage>> filteredAllApiLogs = filterApiLogs(aeo.apiLogs, aeo.appPackageName)

      aeo.actions.eachWithIndex {TimestampedExplorationAction tea, int i ->

        Set<IApiLogcatMessage> apiLogs = filteredAllApiLogs[i].unique {it.uniqueString}
        Set<IApiLogcatMessage> newApiLogs = apiLogs.findAll {!(it.uniqueString in uniqApisSeenSoFar)}
        uniqApisSeenSoFar += newApiLogs*.uniqueString

        String apis = "Apis# " + "${apiLogs.size()}".padLeft(2, ' ')
        String newApis = "newApis# " + "${newApiLogs.size()}".padLeft(2, ' ') + ":"

        String actionIndex = "${i + 1}".padLeft(3, ' ')

        String actionString = tea.explorationAction.toTabulatedString()

        if (aeo.isUiaTestCase)
        {
          writer.write("\n")
          writer.write("// ${aeo.comments[i]}\n")
        }
        writer.write("$tea.timestamp $apis $actionIndex/$actionsCount $actionString\n")

        if (!newApiLogs.isEmpty())
        {
          writer.write("$newApis\n")
          newApiLogs.each {writer.write("  ${apiPrint(it)}\n")}
          writer.write("\n")
        }

      }
      writer.write("\n")
    }

    writer.close()
  }

  /// !!! DUPLICATION WARNING !!! org.droidmate.monitor.RedirectionsGenerator.redirMethodDefPrefix
  // and with other code in this class responsible for generating method name.

  private static List<String> manuallyConfirmedNonRedundantApis = [
    "redir_java_net_URL_openConnection0",
    "redir_org_apache_http_impl_client_AbstractHttpClient_execute3",
    "redir_android_bluetooth_BluetoothAdapter_enable0",
    // Actually this call is redundant, but it is a part of suite of API calls detecting Intent-requiring operations.
    "redir_android_content_ContextWrapper_startService1",
    // The same as the call above.
    "redir_android_content_ContextWrapper_sendOrderedBroadcast2",
    // It calls ctor0 but then it calls java.net.Socket#tryAllAddresses which has a lot of logic.
    "redir_13_java_net_Socket_ctor4"
  ]

  // !!! DUPLICATION WARNING !!! org.droidmate.exploration.output.FilteredApis.manuallyConfirmedRedundantApis
  private static List<String> manuallyConfirmedRedundantApis = [
    "redir_4_android_webkit_WebView_ctor1",
    "redir_5_android_webkit_WebView_ctor2",
    "redir_6_android_webkit_WebView_ctor3",
    "redir_7_android_webkit_WebView_ctor4",
    "redir_android_app_ActivityManager_restartPackage1",
    "redir_android_content_ContentResolver_openFileDescriptor2",
    "redir_android_content_ContentResolver_query5",
    "redir_android_net_wifi_WifiManager_isWifiEnabled0",
    "redir_java_net_URL_getContent0",
    "redir_java_net_URL_openStream0",
    "redir_android_widget_VideoView_start0",
    "redir_android_widget_VideoView_setVideoURI1",
    "redir_android_widget_VideoView_stopPlayback0",
    "redir_android_widget_VideoView_release1",
    "redir_android_app_NotificationManager_notify2",
    "redir_android_os_PowerManager_WakeLock_release0",
    // This makes actually two methods redundant (as expected), both having one param, but of different type.
    "redir_android_content_ContextWrapper_setWallpaper1"
  ]
  /// end of duplication warning

  /**
   * Imagine a stack trace of monitored APIs of: A called by B, B called by C. Here, A is at the end of stack trace and B and C
   * are inside it. If such stack trace is encountered, also stack traces of: "B called by C" and "C" are encountered, as all
   * A, B and C are monitored.
   * <p>
   *
   * If we have never seen A inside a stack trace, only at the end, we have a guarantee it is not redundant: it has to be
   * monitored. However, if A would have been inside a stack trace, like B and C are in this case, it might just be delegating to
   * other call, and thus is possibly redundant (doesn't have to be monitored at all) and has to be manually checked for that.
   *
   * </p>
   */
  @Override
  void possiblyRedundantApiCalls(ExplorationOutput output, Writer writer)
  {
    List<String> stackTraces = output.collect {filterApiLogs(it.apiLogs, it.appPackageName)}.flatten()*.stackTrace

    Set<String> possiblyRedundantApis = [] as Set
    stackTraces.each {String st ->
      def apis = st.split(Api.stack_trace_frame_delimiter).findAll {it.startsWith(Api.monitorRedirectionPrefix)}
      possiblyRedundantApis += apis.drop(1)
    }

    manuallyConfirmedNonRedundantApis.each {String nonred ->
      possiblyRedundantApis.removeAll {it.contains(nonred)}
    }

    manuallyConfirmedRedundantApis.each {String nonred ->
      possiblyRedundantApis.removeAll {it.contains(nonred)}
    }

    possiblyRedundantApis.each {
      log.warn("Possibly redundant API call discovered: " + it)
      writer.write("$it\n")
    }

    writer.close()
  }

  @Override
  void stackTraces(ExplorationOutput output, Writer writer)
  {
    output.each {IApkExplorationOutput aeo ->

      writer.write("==============================================================\n")
      writer.write("$aeo.header\n")
      writer.write("==============================================================\n")
      writer.write("\n")

      def fapiLogs = filterApiLogs(aeo.apiLogs, aeo.appPackageName)

      List<List<String>> stackTracesLists = fapiLogs.collectNested {it.stackTrace}

      writer.write("Stack traces count: " + stackTracesLists.flatten().size() + "\n")
      writer.write("\n")

      int actionsCount = stackTracesLists.size()
      stackTracesLists.eachWithIndex {List<String> stl, int i ->

        writer.write("action ${i + 1}/$actionsCount: ${aeo.actions[i].explorationAction}\n")
        writer.write("\n")

        stl.eachWithIndex {String st, int j ->

          fapiLogs[i][j].with {
            writer.write("$it.uniqueString\n")
          }
          writer.write("Tid: ${fapiLogs[i][j].threadId} Param vals: ${fapiLogs[i][j].paramValues}\n")

          writer.write("Stack trace:\n")

          List<String> stFrames = st.split(Api.stack_trace_frame_delimiter).dropWhile {String it -> !(it.startsWith(Api.monitorRedirectionPrefix))}
          stFrames = ["(...)", stFrames[0], "(...)"] + stFrames.dropWhile {String it -> !(it.startsWith(aeo.appPackageName))}

          stFrames.each {writer.write("  $it\n")}
          writer.write("\n")
        }
        writer.write("------------\n")
        writer.write("\n")
      }

    }
    writer.close()
  }

  @Override
  void apiManifest(ExplorationOutput output, Writer writer)
  {
    output.each {IApkExplorationOutput aeo ->

      writer.write("==============================================================\n")
      writer.write("$aeo.header\n")
      writer.write("==============================================================\n")
      writer.write("\n")

      List<IApiLogcatMessage> apiCallData = filterApiLogs(aeo.apiLogs, aeo.appPackageName).flatten().collect {it}

      TreeSet<List<String>> apiManifestForApp = new TreeSet<List<String>>()

      apiCallData.each {IApiLogcatMessage api ->

        List<String> stFrames = api.stackTrace.split(Api.stack_trace_frame_delimiter).findAll {
          it.startsWith(Api.monitorRedirectionPrefix) || it.startsWith(aeo.appPackageName)
        }

        String paddedThreadId = api.threadId.padLeft(4, ' ')
        String handlerString = ("TId: $paddedThreadId " + stFrames.last().padRight(130, ' '))
        String apiCallString = (stFrames.first() - "org.droidmate.monitor.Monitor.").replace("_", ".")
        String paramValsString = api.paramValues.toString()
        String fullString = handlerString + " -> " + apiCallString + " " + paramValsString

        if (!apiManifestForApp.contains(fullString))
          apiManifestForApp.add(fullString)
      }
      apiManifestForApp.each {
        writer.write(it + "\n")
      }
      writer.write("\n")
    }
    writer.close()
  }


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

      if (config.appGuardOnlyApis || appGuardApis)
      {
        currentFilteredList = currentFilteredList.findAll {isInAppGuardApiList(it)}
      }

      currentFilteredList = currentFilteredList.findAll {!it.isCallToStartInternalActivity(appPackageName)}

      out << currentFilteredList
    }
    assert apiLogs.size() == out.size()
    out.each {assert it != null}
    return out
  }

  boolean isInAppGuardApiList(IApiLogcatMessage api)
  {
    assert appGuardApis != null
    return appGuardApis.any {
      it.methodName == api.methodName && it.objectClass == api.objectClass
    }
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