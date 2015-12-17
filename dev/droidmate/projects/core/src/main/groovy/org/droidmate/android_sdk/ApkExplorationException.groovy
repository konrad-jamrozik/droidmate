// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.android_sdk

import org.droidmate.common.DroidmateException
import org.droidmate.exceptions.DeviceException

public class ApkExplorationException extends DroidmateException
{
  final IApk            apk
  final DeviceException exception

  public ApkExplorationException(IApk apk, DeviceException exception)
  {
    this.apk = apk
    this.exception = exception
  }

  public String getInstanceName()
  {
    this.apk.fileName
  }


  public String getApkPath()
  {
    this.apk.absolutePath
  }


}