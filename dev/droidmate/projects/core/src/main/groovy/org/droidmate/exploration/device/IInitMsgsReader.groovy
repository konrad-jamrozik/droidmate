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
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

interface IInitMsgsReader
{

  LocalDateTime readMonitorMessages(IDeviceTimeDiff deviceTimeDiff) throws DeviceException




  List<ITimeFormattedLogcatMessage> readInstrumentationMessages(IDeviceTimeDiff deviceTimeDiff) throws DeviceException
}


