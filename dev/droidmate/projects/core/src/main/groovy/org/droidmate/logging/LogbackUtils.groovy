// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logging

import org.droidmate.common.logging.LogbackConstants


class LogbackUtils
{
  public static String getLogFilePath(String logName) {"${LogbackConstants.LOGS_DIR_PATH}${File.separator}${logName}"}

  public static String getLogFilePathForLastElement(String className) {getLogFilePath("${className.substring(className.lastIndexOf(".")+1)}.txt")}

}

