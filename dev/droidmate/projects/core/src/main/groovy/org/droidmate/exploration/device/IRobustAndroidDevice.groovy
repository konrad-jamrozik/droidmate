// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import org.droidmate.device.IAndroidDevice
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException

public interface IDeviceWithReadableLogs extends IAndroidDevice, IDeviceMessagesReader
{
  IDeviceGuiSnapshot ensureHomeScreenIsDisplayed() throws DeviceException
}
