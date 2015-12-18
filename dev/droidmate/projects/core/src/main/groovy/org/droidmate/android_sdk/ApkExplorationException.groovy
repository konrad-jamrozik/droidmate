// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.android_sdk

import groovy.util.logging.Slf4j
import org.droidmate.exceptions.DeviceException

@Slf4j
public class ApkExplorationException extends ExplorationException
{

  private static final long serialVersionUID = 1

  final IApk apk
  final boolean stopFurtherApkExplorations

  public ApkExplorationException(IApk apk, Throwable cause, boolean stopFurtherApkExplorations = false)
  {
    super(cause)
    this.apk = apk
    this.stopFurtherApkExplorations = stopFurtherApkExplorations

    assert apk != null
    assert cause != null

    if (this.shouldStopFurtherApkExplorations())
    {
      log.warn("An ${this.class.simpleName} demanding stopping further apk explorations was just constructed!")
    }
  }

  public boolean shouldStopFurtherApkExplorations()
  {
    if (this.stopFurtherApkExplorations)
      return true

    if (!(this.cause instanceof DeviceException))
      return true

    if ((this.cause as DeviceException).stopFurtherApkExplorations)
      return true

    return false
  }
}
