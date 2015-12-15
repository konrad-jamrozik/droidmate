// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logging

import groovy.util.logging.Slf4j
import org.droidmate.common.logging.LogbackConstants

import static groovy.io.FileType.FILES

@Slf4j
class LogbackUtilsRequiringLogbackLog
{

  public static void cleanLogsDir()
  {
    File logsDir = new File(LogbackConstants.LOGS_DIR_PATH)
    if (!logsDir.directory)
      logsDir.mkdirs()

    String logNotDeleted = ""

    logsDir.eachFile(FILES) {File logFile ->

      if (logFile.name in LogbackConstants.fileAppendersUsedBeforeCleanLogsDir)
        return

      if (!logFile.delete())
      {
        logNotDeleted += " $logFile.name,"
        logFile.write("")
      }
    }

    log(logsDir, logNotDeleted)
  }

  private static void log(File logsDir, String logNotDeleted)
  {
    String logMsgPrefix = "Deleted old logs in directory $logsDir."
    if (!logNotDeleted.empty)
    {
      //noinspection GroovyAssignmentToMethodParameter
      logNotDeleted = logNotDeleted[0..-2] // Remove trailing comma.
      logMsgPrefix += " Files that couldn't be deleted and thus had their content was instead erased:"
    }
    log.trace(logMsgPrefix + logNotDeleted)
  }
}
