// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common.logging


public class LogbackConstants
{

  public static final String LOGS_DIR_PATH = getLogsDirPath()

  private static String getLogsDirPath()
  {
    // WISH note: logsDir has to be set on VM arg instead of normal arg. Maybe making it normal arg and then resetting
    // the config as described here [1] would help. [1]: http://logback.qos.ch/manual/configuration.html#joranDirectly
    String logsDir = System.getProperty("logsDir")
    if (logsDir == null)
      // !!! DUPLICATION WARNING !!! org.droidmate.configuration.Configuration.defaultDroidmateOutputDir
      return "." + File.separator + "output_device1" + File.separator + "logs"
    else
      return "." + File.separator + logsDir
  }

  public static final List<String> fileAppendersUsedBeforeCleanLogsDir = [
    appender_name_master,
    appender_name_stdStreams,
    appender_name_runData
  ]

  @Deprecated
  /**
   * Denotes name of logger for logs that have been obtained from logcat from uiautomator-daemon classes during exploration.
   */
  public static final String logger_name_uiad = "From uiautomator-daemon logcat"

  /**
   * Denotes name of logger for logs that have been obtained from logcat from the loaded monitor class during exploration.
   */
  public static final String logger_name_monitor = "from monitor"

  @Deprecated
  public static final String appender_name_uiad = "uiad.txt"

  public static final String appender_name_monitor = "monitor.txt"

  public static final String system_prop_stdout_loglevel = "loglevel"

  public static final String appender_name_stdStreams = "std_streams.txt"

  public static final String appender_name_master = "master_log.txt"

  public static final String appender_name_warnings = "warnings.txt"

  public static final String appender_name_runData = "run_data.txt"

  // WISH More exception hierarchy in the file: which exceptions came together, for which apk. E.g. Apk XYZ, Expl. Act. 150, EX1 attempt failed EX2 attempt failed E3 complete failure.
  public static final String appender_name_exceptions = "exceptions.txt"

  public static final String appender_name_exploration = "exploration.txt"

  public static final String exceptions_log_path = "${LOGS_DIR_PATH}${File.separator}${appender_name_exceptions}"

  public static final String err_log_msg =
    "Please see $exceptions_log_path log for details."
}
