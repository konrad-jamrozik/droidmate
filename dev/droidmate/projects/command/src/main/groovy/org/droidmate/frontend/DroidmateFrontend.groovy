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

package org.droidmate.frontend

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.util.logging.Slf4j
import org.droidmate.command.DroidmateCommand
import org.droidmate.configuration.Configuration
import org.droidmate.configuration.ConfigurationBuilder
import org.droidmate.logging.LogbackConstants
import org.droidmate.logging.LogbackUtilsRequiringLogbackLog
import org.droidmate.misc.DroidmateException

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.time.LocalDate

import static LogbackConstants.system_prop_stdout_loglevel
import static org.droidmate.logging.Markers.runData

/**
 * <p>
 * Entry class of DroidMate. This class should be supplied to the JVM on the command line as the entry class
 * (i.e. the class containing the {@code main} method).
 * </p>
 */
@Slf4j
public class DroidmateFrontend
{
  /**
   * @see DroidmateFrontend
   */
  public static void main(String[] args)
  {
    int exitStatus = main(args, null)
    System.exit(exitStatus)
  }

  public static int main(String[] args, ICommandProvider commandProvider, FileSystem fs = FileSystems.getDefault(), IExceptionHandler exceptionHandler = new ExceptionHandler())
  {
    println "DroidMate, an automated execution generator for Android apps."
    println "Copyright (c) 2012 - ${LocalDate.now().year} Konrad Jamrozik"
    println "This program is free software licensed under GNU GPL v3."
    println ""
    println "You should have received a copy of the GNU General Public License"
    println "along with this program.  If not, see <http://www.gnu.org/licenses/>."
    println ""
    println "email: jamrozik@st.cs.uni-saarland.de"
    println "web: www.droidmate.org"

    int exitStatus = 0
    Date runStart = new Date()

    try
    {
      validateStdoutLoglevel()
      LogbackUtilsRequiringLogbackLog.cleanLogsDir()
      log.info("Bootstrapping DroidMate: building ${Configuration.simpleName} from args " +
        "and instantiating objects for ${DroidmateCommand.simpleName}.")
      log.info("IMPORTANT: for help on how to configure DroidMate, run it with -help")
      log.info("IMPORTANT: for detailed logs from DroidMate run, please see ${LogbackConstants.LOGS_DIR_PATH}.")

      Configuration cfg = new ConfigurationBuilder().build(args, fs)

      DroidmateCommand command
      if (commandProvider != null)
        command = commandProvider.provide(cfg)
      else 
        command = determineAndBuildCommand(cfg)

      log.info("Successfully instantiated ${command.class.simpleName}. Welcome to DroidMate. Lie back, relax and enjoy.")
      log.info("Run start timestamp: " + runStart)

      command.execute(cfg)

    } catch (Throwable e)
    {
      exitStatus = exceptionHandler.handle(e)
    }

    logDroidmateRunEnd(runStart, /* boolean encounteredExceptionsDuringTheRun = */ exitStatus > 0)
    return exitStatus
  }

  private static DroidmateCommand determineAndBuildCommand(Configuration cfg)
  {
    return DroidmateCommand.build(cfg.report, cfg.inline, cfg)
  }

  private static void validateStdoutLoglevel()
  {
    if (!System.hasProperty(system_prop_stdout_loglevel))
      return

    if (!(System.getProperty(system_prop_stdout_loglevel).toUpperCase() in ["TRACE", "DEBUG", "INFO"]))
      throw new DroidmateException("The $system_prop_stdout_loglevel environment variable has to be set to TRACE, " +
        "DEBUG or INFO. Instead, it is set to ${System.getProperty(system_prop_stdout_loglevel)}.")
  }

  private static void logDroidmateRunEnd(Date runStart, boolean encounteredExceptionsDuringTheRun)
  {
    Date runEnd = new Date()
    TimeDuration runDuration = TimeCategory.minus(runEnd, runStart)
    String timestampFormat = "yyyy MMM dd HH:mm:ss"

    if (encounteredExceptionsDuringTheRun)
      log.warn("DroidMate run finished, but some exceptions have been thrown and handled during the run. See previous logs for details.")
    else
      log.info("DroidMate run finished successfully.")

    log.info("Run finish timestamp: ${runEnd.format(timestampFormat)}. DroidMate ran for ${runDuration}.")
    log.info("By default, the results from the run can be found in .${File.separator}${Configuration.defaultDroidmateOutputDir} directory.")
    log.info("By default, for detailed diagnostics logs from the run, see $LogbackConstants.LOGS_DIR_PATH directory.")

    log.info(runData, "Run start  timestamp: ${runStart.format(timestampFormat)}")
    log.info(runData, "Run finish timestamp: ${runEnd.format(timestampFormat)}")
    log.info(runData, "DroidMate ran for: $runDuration")
  }
}
