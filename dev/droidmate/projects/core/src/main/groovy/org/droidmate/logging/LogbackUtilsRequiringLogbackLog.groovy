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

import groovy.util.logging.Slf4j

import static groovy.io.FileType.FILES

@Slf4j
class LogbackUtilsRequiringLogbackLog
{

  public static void cleanLogsDir()
  {
    File logsDir = new File(LogbackConstants.LOGS_DIR_PATH)
    if (!logsDir.directory)
      logsDir.mkdirs()

    String msg_notDeletedLogFileNames = ""

    logsDir.eachFile(FILES) {File logFile ->

      if (logFile.name in LogbackConstants.fileAppendersUsedBeforeCleanLogsDir)
        return

      if (!logFile.delete())
      {
        msg_notDeletedLogFileNames += " $logFile.name,"
        logFile.write("")
      }
    }

    log(logsDir, msg_notDeletedLogFileNames)
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
