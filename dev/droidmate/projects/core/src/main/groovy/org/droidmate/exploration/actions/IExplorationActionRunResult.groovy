// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.actions

import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.device.IDeviceLogs

interface IExplorationActionRunResult extends Serializable
{
  boolean getSuccessful()

  IDeviceLogs getDeviceLogs()

  IDeviceGuiSnapshot getGuiSnapshot()

  DeviceException getException()

}