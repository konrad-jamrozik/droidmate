// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command.exploration

import org.droidmate.android_sdk.IApk
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.IDeviceWithReadableLogs
import org.droidmate.misc.Failable

public interface IExploration
{

  Failable<IApkExplorationOutput2, DeviceException> run(IApk app, IDeviceWithReadableLogs device)
}
