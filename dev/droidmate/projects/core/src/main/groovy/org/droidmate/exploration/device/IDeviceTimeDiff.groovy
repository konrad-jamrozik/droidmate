// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceNeedsRebootException
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

interface IDeviceTimeDiff
{

  LocalDateTime sync(LocalDateTime deviceTime) throws DeviceNeedsRebootException, DeviceException

  List<ITimeFormattedLogcatMessage> syncMessages(List<ITimeFormattedLogcatMessage> messages) throws DeviceNeedsRebootException, DeviceException

  void reset()
}