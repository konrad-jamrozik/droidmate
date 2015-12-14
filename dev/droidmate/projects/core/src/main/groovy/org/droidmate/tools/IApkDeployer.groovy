// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tools

import org.droidmate.android_sdk.IApk
import org.droidmate.device.IDeployableAndroidDevice
import org.droidmate.exceptions.DeviceException

/**
 * @see ApkDeployer
 */
public interface IApkDeployer
{

  public void withDeployedApk(IDeployableAndroidDevice device, IApk apk, Closure closure)
    throws DeviceException
}
