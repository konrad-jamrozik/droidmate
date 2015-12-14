// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tools

import org.droidmate.exceptions.DeviceException

/**
 * @see AndroidDeviceDeployer
 */
public interface IAndroidDeviceDeployer
{
  void withSetupDevice(int deviceIndex, Closure closure) throws DeviceException
}
