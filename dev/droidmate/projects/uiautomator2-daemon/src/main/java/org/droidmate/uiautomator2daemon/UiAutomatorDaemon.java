// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomator2daemon;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static org.droidmate.common_android.Constants.*;

@TargetApi(Build.VERSION_CODES.FROYO)
public class UiAutomatorDaemon extends UiAutomatorTestCase
{
  private Thread serverThread;
  private UiAutomatorDaemonServer uiAutomatorDaemonServer;

  @SuppressWarnings("UnusedDeclaration")
  /* Will be launched by org.droidmate.android_sdk.AdbWrapper.startUiaDaemon().
     See also:
     org.droidmate.common_android.Constants.uiaDaemon_initMethodName
  */
  /*public void init()
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
  }*/

  public void init(Boolean waitForGuiToStabilize, int waitForWindowUpdateTimeout,
                   int tcpPort)
  {
    saveLogcatToFile();

    // WISH Borges: replaced by command line parameters when converted to Service
    //boolean waitForGuiToStabilize = Boolean.valueOf((String) getParams().get(uiaDaemonParam_waitForGuiToStabilize));
    //int waitForWindowUpdateTimeout = Integer.valueOf((String) getParams().get(uiaDaemonParam_waitForWindowUpdateTimeout));
    //int tcpPort = Integer.valueOf((String) getParams().get(uiaDaemonParam_tcpPort));

    IUiAutomatorDaemonDriver uiAutomatorDaemonDriver = new UiAutomatorDaemonDriver(this, waitForGuiToStabilize, waitForWindowUpdateTimeout);
    this.uiAutomatorDaemonServer = new UiAutomatorDaemonServer(uiAutomatorDaemonDriver);

    Log.d(uiaDaemon_logcatTag, "uiAutomatorDaemonServer.start("+tcpPort+")");
    try
    {
      this.serverThread = uiAutomatorDaemonServer.start(tcpPort);
    } catch (Throwable t)
    {
      Log.e(uiaDaemon_logcatTag, "uiAutomatorDaemonServer.start("+tcpPort+") / FAILURE", t);
    }

    if (this.serverThread == null)
      throw new AssertionError();

    Log.d(uiaDaemon_logcatTag, "uiAutomatorDaemonServer.start("+tcpPort+") / SUCCESS");
  }

  public void shutdown()
  {
    // Close the server
    this.uiAutomatorDaemonServer.close();
    try
    {
      // Postpone process termination until the server thread finishes.
      serverThread.join();
    } catch (InterruptedException e)
    {
      Log.wtf(uiaDaemon_logcatTag, e);
    }
    if (!uiAutomatorDaemonServer.isClosed())
      throw new AssertionError();

    Log.i(uiaDaemon_logcatTag, "init: Shutting down UiAutomatorDaemon.");
  }

  private void saveLogcatToFile() {

    String fileName = logcatLogFileName;

    File outputFile = new File(Environment.getDataDirectory(), fileName);

    if (outputFile.exists())
    {
      boolean logDeletionResult = outputFile.delete();
      if (!logDeletionResult)
        Log.wtf(uiaDaemon_logcatTag, "Failed to delete existing file "+fileName +" !");
    }

    Log.d(uiaDaemon_logcatTag, "Logging logcat to: "+outputFile.getAbsolutePath());
    try
    {
      // - For explanation of the exec string, see org.droidmate.android_sdk.AdbWrapper.readMessagesFromLogcat()
      // - Manual tests with "adb shell ps" show that the executed process will be automatically killed when the uiad process dies.
      Runtime.getRuntime().exec(String.format("logcat -v time -f %s *:D %s:W %s:D %s:D dalvikvm:I ActivityManager:V AccessibilityNodeInfoDumper:S View:E ResourceType:E HSAd-HSAdBannerView:I" ,
        outputFile.getAbsolutePath(), instrumentation_redirectionTag, uiaDaemon_logcatTag, SerializableTCPServerBase.tag));
    } catch (IOException e)
    {
      Log.wtf(uiaDaemon_logcatTag, e);
    }
  }
}
