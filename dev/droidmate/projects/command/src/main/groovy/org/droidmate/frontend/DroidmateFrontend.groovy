// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.frontend

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import groovy.util.logging.Slf4j
import org.droidmate.command.DroidmateCommand
import org.droidmate.common.DroidmateException
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.configuration.Configuration
import org.droidmate.configuration.ConfigurationBuilder
import org.droidmate.logging.LogbackUtilsRequiringLogbackLog

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.time.LocalDate

import static LogbackConstants.system_prop_stdout_loglevel
import static org.droidmate.common.logging.Markers.runData

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
    println "DroidMate"
    println "Copyright (c) 2012 - ${LocalDate.now().year} Saarland University"
    println "All rights reserved."

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

      log.info("Successfully instantiatied ${command.class.simpleName}. Welcome to DroidMate. Lie back, relax and enjoy.")
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
    return DroidmateCommand.build(cfg.processUiaTestCasesLogs, cfg.extractData, cfg.report, cfg.inline, cfg)
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
