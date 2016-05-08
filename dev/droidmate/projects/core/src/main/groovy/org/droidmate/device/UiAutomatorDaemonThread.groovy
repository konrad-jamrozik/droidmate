// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IAdbWrapper

@Slf4j
class UiAutomatorDaemonThread implements Runnable
{

  private final String      deviceSerialNumber
  private final IAdbWrapper adbWrapper
  private final int         port

  UiAutomatorDaemonThread(IAdbWrapper adbWrapper, String deviceSerialNumber, int port)
  {
    this.adbWrapper = adbWrapper
    this.deviceSerialNumber = deviceSerialNumber
    this.port = port
  }

  @Override
  public void run()
  {
    try
    {
      this.adbWrapper.startUiautomatorDaemon(this.deviceSerialNumber, this.port)
    } catch (Throwable e)
    {
      log.error("$UiAutomatorDaemonThread.simpleName threw ${e.class.simpleName}. The exception:\n", e)
    }
  }

}
