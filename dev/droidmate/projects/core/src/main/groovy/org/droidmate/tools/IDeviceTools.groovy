// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.tools

import org.droidmate.android_sdk.IAaptWrapper

interface IDeviceTools
{

  IAaptWrapper getAapt()

  IAndroidDeviceDeployer getDeviceDeployer()

  IApkDeployer getApkDeployer()

}