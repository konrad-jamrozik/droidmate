// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logcat

import java.time.LocalDateTime

/**
 * See {@link org.droidmate.common.logcat.TimeFormattedLogcatMessage}
 */
interface ITimeFormattedLogcatMessage extends Serializable
{

  LocalDateTime getTime()

  String getLevel()

  String getTag()

  String getPidString()

  String getMessagePayload()

  String toLogcatMessageString()

}
