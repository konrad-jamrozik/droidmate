// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tools

import org.droidmate.android_sdk.ApkExplorationException
import org.droidmate.android_sdk.ExplorationException
import org.droidmate.exceptions.DeviceException

/**
 * @see AndroidDeviceDeployer
 */
public interface IAndroidDeviceDeployer
{
  List<ExplorationException> withSetupDevice(int deviceIndex, Closure<List<ApkExplorationException>> closure) throws DeviceException
}
