// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomatordaemon;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import java.io.File;
import java.io.IOException;

import static org.droidmate.common_android.Constants.*;

@TargetApi(Build.VERSION_CODES.FROYO)
public class UiAutomatorDaemon extends UiAutomatorTestCase
{

  @SuppressWarnings("UnusedDeclaration")
  /* Will be launched by org.droidmate.android_sdk.AdbWrapper.startUiaDaemon().
     See also:
     org.droidmate.common_android.Constants.uiaDaemon_initMethodName
  */
  public void init()
  {
    saveLogcatToFile();

    boolean waitForGuiToStabilize = Boolean.valueOf((String) getParams().get(uiaDaemonParam_waitForGuiToStabilize));
    int waitForWindowUpdateTimeout = Integer.valueOf((String) getParams().get(uiaDaemonParam_waitForWindowUpdateTimeout));
    int tcpPort = Integer.valueOf((String) getParams().get(uiaDaemonParam_tcpPort));

    IUiAutomatorDaemonDriver uiAutomatorDaemonDriver = new UiAutomatorDaemonDriver(this, waitForGuiToStabilize, waitForWindowUpdateTimeout);
    UiAutomatorDaemonServer uiAutomatorDaemonServer = new UiAutomatorDaemonServer(uiAutomatorDaemonDriver);

    Log.d(uiaDaemon_logcatTag, "uiAutomatorDaemonServer.start("+tcpPort+")");
    Thread serverThread = null;
    try
    {
      serverThread = uiAutomatorDaemonServer.start(tcpPort);
    } catch (Throwable t)
    {
      Log.e(uiaDaemon_logcatTag, "uiAutomatorDaemonServer.start("+tcpPort+") / FAILURE", t);
    }
    if (serverThread == null) throw new AssertionError();
    Log.d(uiaDaemon_logcatTag, "uiAutomatorDaemonServer.start("+tcpPort+") / SUCCESS");

    try
    {
      // Postpone process termination until the server thread finishes.
      serverThread.join();
    } catch (InterruptedException e)
    {
      Log.wtf(uiaDaemon_logcatTag, e);
    }
    if (!uiAutomatorDaemonServer.isClosed()) throw new AssertionError();

    Log.i(uiaDaemon_logcatTag, "init: Shutting down UiAutomatorDaemon.");
  }

  public void saveLogcatToFile() {
    String fileName = uiaDaemon_logcatFileName;

    File outputFile = new File(Environment.getDataDirectory(),fileName);

    if (outputFile.exists())
    {
      boolean logDeletionResult = outputFile.delete();
      if (!logDeletionResult)
        Log.wtf(uiaDaemon_logcatTag, "Failed to delete existing file "+fileName +" !");
    }

    Log.d(uiaDaemon_logcatTag, "Logging logcat to: "+outputFile.getAbsolutePath());
    try
    {
      // For explanation of the exec string, see org.droidmate.android_sdk.AdbWrapper.readMessagesFromLogcat()
      Runtime.getRuntime().exec("logcat -b main -v time -f "+outputFile.getAbsolutePath());
    } catch (IOException e)
    {
      Log.wtf(uiaDaemon_logcatTag, e);
    }
  }
}
