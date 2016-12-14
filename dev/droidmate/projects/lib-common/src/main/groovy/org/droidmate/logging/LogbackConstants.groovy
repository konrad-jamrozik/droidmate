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

package org.droidmate.logging


class LogbackConstants
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
  
  /**
   * Denotes name of logger for logs that have been obtained from logcat from the loaded monitor class during exploration.
   */
  public static final String logger_name_monitor = "from monitor"
  
  public static final String appender_name_monitor = "monitor.txt"

  public static final String system_prop_stdout_loglevel = "loglevel"

  public static final String appender_name_stdStreams = "std_streams.txt"

  public static final String appender_name_master = "master_log.txt"

  public static final String appender_name_warnings = "warnings.txt"

  public static final String appender_name_runData = "run_data.txt"

  public static final String appender_name_health = "app_health.txt"

  // WISH More exception hierarchy in the file: which exceptions came together, for which apk. E.g. Apk XYZ, Expl. Act. 150, EX1 attempt failed EX2 attempt failed E3 complete failure.
  public static final String appender_name_exceptions = "exceptions.txt"

  public static final String appender_name_exploration = "exploration.txt"

  public static final String exceptions_log_path = "${LOGS_DIR_PATH}${File.separator}${appender_name_exceptions}"

  public static final String err_log_msg =
    "Please see $exceptions_log_path log for details."
}
