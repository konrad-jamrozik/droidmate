// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.actions

import org.droidmate.android_sdk.IApk
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.device.IDeviceWithReadableLogs

import java.time.LocalDateTime

interface IRunnableExplorationAction extends Serializable
{
  IExplorationActionRunResult run(IApk app, IDeviceWithReadableLogs device) throws DeviceException

  ExplorationAction getBase()

  LocalDateTime getTimestamp()

}