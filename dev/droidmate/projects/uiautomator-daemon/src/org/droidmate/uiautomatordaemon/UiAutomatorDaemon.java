// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomatordaemon;

import android.util.Log;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import static org.droidmate.common_android.Constants.*;

public class UiAutomatorDaemon extends UiAutomatorTestCase
{

  @SuppressWarnings("UnusedDeclaration")
  /* Will be launched by org.droidmate.android_sdk.AdbWrapper.startUiaDaemon().
     See also:
     org.droidmate.common_android.Constants.uiaDaemon_initMethodName
  */
  public void init() throws InterruptedException
  {
    boolean waitForGuiToStabilize = Boolean.valueOf((String) getParams().get(uiaDaemonParam_waitForGuiToStabilize));
    int waitForWindowUpdateTimeout = Integer.valueOf((String) getParams().get(uiaDaemonParam_waitForWindowUpdateTimeout));
    int tcpPort = Integer.valueOf((String) getParams().get(uiaDaemonParam_tcpPort));

    IUiAutomatorDaemonDriver uiAutomatorDaemonDriver = new UiAutomatorDaemonDriver(this, waitForGuiToStabilize, waitForWindowUpdateTimeout);
    UiAutomatorDaemonServer uiAutomatorDaemonServer = new UiAutomatorDaemonServer(uiAutomatorDaemonDriver);

    Log.d(uiaDaemon_logcatTag, "init: Starting UiAutomatorDaemonServer...");
    Thread serverThread = null;
    try
    {
      serverThread = uiAutomatorDaemonServer.start(tcpPort);
    } catch (InterruptedException e)
    {
      Log.e(uiaDaemon_logcatTag, "init: Starting UiAutomatorDaemonServer failed.", e);
    }
    if (serverThread == null) throw new AssertionError();
    Log.d(uiaDaemon_logcatTag, "init: Starting UiAutomatorDaemonServer succeeded.");

    // Postpone process termination until the server thread finishes.
    serverThread.join();
    if (!uiAutomatorDaemonServer.isClosed()) throw new AssertionError();

    Log.i(uiaDaemon_logcatTag, "init: Shutting down UiAutomatorDaemon.");
  }
}
