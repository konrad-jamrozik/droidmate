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


import ch.qos.logback.classic.Level
import ch.qos.logback.classic.boolex.GEventEvaluator
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.filter.EvaluatorFilter
import org.droidmate.common.SysCmdExecutor
import org.droidmate.common.logging.AllDroidmateMarkersFilter
import org.droidmate.common.logging.Markers
import org.droidmate.logging.LazyFileAppender
import org.droidmate.logging.LogbackAppenders
import org.droidmate.logging.MarkerFilter

import static ch.qos.logback.classic.Level.*
import static ch.qos.logback.core.spi.FilterReply.*
import static org.droidmate.common.logging.LogbackConstants.*
import static org.droidmate.logging.LogbackUtils.getLogFilePath
import static org.droidmate.logging.LogbackUtils.getLogFilePathForLastElement

final Level STDOUT_LOG_LEVEL = toLevel(System.getProperty(system_prop_stdout_loglevel, "INFO"))
final Level STDERR_LOG_LEVEL = ERROR

final String appender_file_osCmd = "os_cmd.txt"

// format modifiers explained: http://logback.qos.ch/manual/layouts.html#formatModifiers
// %rEx modifier explained: http://logback.qos.ch/manual/layouts.html#rootException
final String pat_bare = "%msg%rEx%n"
final String pat_date_level = "%date{yyyy-MM-dd HH:mm:ss.SSS} %-5level %msg%rEx%n"
final String pat_date_level_logger = "%date{yyyy-MM-dd HH:mm:ss.SSS} %-5level %-40logger{40} %msg%rEx%n"

//region Console appenders

appender(LogbackAppenders.appender_stdout, ConsoleAppender) {
  target = "System.out"
  filter(ThresholdFilter) {level = STDOUT_LOG_LEVEL}
  filter(LevelFilter) {level = ERROR; onMatch = DENY; onMismatch = NEUTRAL}
  filter(AllDroidmateMarkersFilter) {onMatch = DENY}
  encoder(PatternLayoutEncoder) {pattern = pat_date_level_logger}
}

appender(LogbackAppenders.appender_stderr, ConsoleAppender) {
  target = "System.err"
  filter(ThresholdFilter) {level = STDERR_LOG_LEVEL}
  filter(AllDroidmateMarkersFilter) {onMatch = DENY}
  encoder(PatternLayoutEncoder) {pattern = pat_date_level_logger}
}

//endregion Console appenders

//region File appenders, specialized
appender(appender_name_stdStreams, LazyFileAppender) {
  file = getLogFilePath(appender_name_stdStreams)
  append = false
  lazy = true

  filter(ThresholdFilter) {level = STDOUT_LOG_LEVEL}
  filter(AllDroidmateMarkersFilter) {onMatch = DENY}
  filter(EvaluatorFilter) {
    // Reference:
    // http://logback.qos.ch/manual/filters.html#GEventEvaluator
    evaluator(GEventEvaluator) {
      // Do not log TRACE from SysCmdExecutor, as it is too verbose.
      expression = "(e.loggerName.contains('$SysCmdExecutor.simpleName') && (e.level == TRACE))"
    }
    onMatch = DENY
    onMismatch = NEUTRAL
  }
  encoder(PatternLayoutEncoder) {pattern = pat_date_level_logger}
}

appender(appender_name_master, LazyFileAppender) {
  file = getLogFilePath(appender_name_master)
  append = false
  lazy = true

  filter(ThresholdFilter) {level = TRACE}
  filter(AllDroidmateMarkersFilter) {onMatch = DENY}

  // Do not log TRACE from SysCmdExecutor, as it is too verbose.
  //
  // Reference:
  // http://logback.qos.ch/manual/filters.html#GEventEvaluator
  filter(EvaluatorFilter) {
    evaluator(GEventEvaluator) {
      expression = "(e.loggerName.contains('$SysCmdExecutor.simpleName') && (e.level == TRACE))"
    }
    onMatch = DENY
    onMismatch = NEUTRAL
  }

  encoder(PatternLayoutEncoder) {pattern = pat_date_level_logger}
}

appender(appender_name_warnings, LazyFileAppender) {
  file = getLogFilePath(appender_name_warnings)
  append = false
  lazy = true

  filter(LevelFilter) {level = WARN; onMatch = ACCEPT; onMismatch = DENY}
  encoder(PatternLayoutEncoder) {pattern = pat_date_level_logger}
}

[[appender_name_monitor, logger_name_monitor]].each {list ->
  String appenderName = list[0]
  String loggerName = list[1]
  appender(appenderName, LazyFileAppender) {
    file = getLogFilePath(appenderName)
    append = false
    lazy = true
    filter(AllDroidmateMarkersFilter) {onMatch = DENY}
    filter(EvaluatorFilter) {
      // Reference:
      // http://logback.qos.ch/manual/filters.html#GEventEvaluator
      evaluator(GEventEvaluator) {
        expression = "(e.loggerName.contains('$loggerName'))"
      }
      onMatch = ACCEPT
      onMismatch = NEUTRAL
    }
    /* We want to log INFO and higher level from appender attached to the root appender, so we know the execution context
       of the logs for current appender, including which apk is currently being explored. */
    filter(ThresholdFilter) {level = INFO}
    encoder(PatternLayoutEncoder) {pattern = pat_date_level_logger}
  }
}

//endregion File appenders, specialized

//region File appenders based on markers

appender(appender_name_exceptions, LazyFileAppender) {
  file = getLogFilePath(appender_name_exceptions)
  append = false
  lazy = true

  filter(MarkerFilter) {
    marker = Markers.exceptions
    onMismatch = DENY; onMatch = NEUTRAL
  }
  encoder(PatternLayoutEncoder) {pattern = pat_date_level_logger}
}

appender(appender_file_osCmd, LazyFileAppender) {
  file = getLogFilePath(appender_file_osCmd)
  append = false
  lazy = true

  filter(MarkerFilter) {
    marker = Markers.osCmd
    onMismatch = DENY; onMatch = NEUTRAL
  }
  encoder(PatternLayoutEncoder) {pattern = pat_bare}
}

appender(appender_name_runData, LazyFileAppender) {
  file = getLogFilePath(appender_name_runData)
  append = false
  lazy = true

  filter(MarkerFilter) {
    marker = Markers.runData
    onMismatch = DENY; onMatch = NEUTRAL
  }
  encoder(PatternLayoutEncoder) {pattern = pat_bare}
}

//endregion File appenders based on markers

//region Appender groups

def warnAppenders = [
  appender_name_warnings,
  appender_name_exceptions
]
def mainFileAppenders = warnAppenders + [
  appender_name_stdStreams,
  appender_name_master,
]
def mainAppenders = mainFileAppenders + [
  LogbackAppenders.appender_stdout,
  LogbackAppenders.appender_stderr,
]

//

//region File appenders based on logger name

// Setting additivity to true makes the logs be appended to all appenders attached to root, including master appender and warnings appender.
List loggersWithLazyFileAppenders = [
  // We cannot refer here to the classes directly as they would make SLF4J create substitute loggers and thus, issue warning to stderr.
  //@formatter:off
  // Uncomment if a detailed SysCmdExecutor log is needed.
//  [loggerName: "org.droidmate.common.SysCmdExecutor",                         additivity: false, pattern: pat_date_level],
  [loggerName: "org.droidmate.android_sdk.AaptWrapper",                       additivity: true,  pattern: pat_date_level],
  [loggerName: "org.droidmate.exploration.strategy.WidgetStrategy",           additivity: false, pattern: pat_date_level, additionalAppenders: warnAppenders],
  [loggerName: "org.droidmate.exploration",                                   additivity: true,  pattern: pat_date_level_logger],//, additionalAppenders: warnAppenders],
  //[loggerName: "org.droidmate.device",                                      additivity: true,  pattern: pat_date_level_logger]//, additionalAppenders: warnAppenders],
  //@formatter:on
]

loggersWithLazyFileAppenders.each {Map it ->
  String loggerName = it.loggerName
  String patternVal = it.pattern
  boolean additivity = it.additivity
  List<String> additionalAppenders = it.additionalAppenders ?: []

  appender(loggerName, LazyFileAppender) {
    file = getLogFilePathForLastElement(loggerName)
    append = false
    lazy = true
    filter(AllDroidmateMarkersFilter) {onMatch = DENY}
    encoder(PatternLayoutEncoder) {pattern = patternVal}
  }

  logger(loggerName, TRACE, [loggerName] + additionalAppenders, additivity)
}
//endregion File appenders based on logger name

//region Remaining loggers

root(TRACE, mainAppenders + [
  appender_name_runData,
  appender_file_osCmd,
  appender_name_monitor,
])

// N00b reference for additivity: http://logback.qos.ch/manual/architecture.html#additivity

// Ensure these loggers log to the given appenders even if the "exploration" package logger has additivity set to false.
//logger("org.droidmate.command.exploration.Exploration", TRACE, mainAppenders - warnAppenders, /* additivity */ true)
//logger("org.droidmate.exploration.strategy.ExplorationStrategy", TRACE, mainAppenders - warnAppenders, /* additivity */ true)
//logger("org.droidmate.exploration.output", TRACE, mainAppenders - warnAppenders, /* additivity */ true)


// Additivity is set to false to stop the uiad logs from appearing in the master log (they would appear there as the "master_log" appender is attached to the root logger)
logger(logger_name_monitor, TRACE, [appender_name_monitor] + warnAppenders, /* additivity */ false)

//endregion
