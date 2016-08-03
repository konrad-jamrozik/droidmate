// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.uiautomator2daemon;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.*;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class UiAutomator2DaemonTest
{
  //@Rule
  //public final ServiceTestRule mServiceRule = new ServiceTestRule();

  @Test
  public void init()
  {
    Bundle extras = InstrumentationRegistry.getArguments();

    Boolean waitForGuiToStabilize = true;
    int waitForWindowUpdateTimeout = -1;
    int tcpPort = -1;

    if (extras.containsKey(uiaDaemonParam_waitForGuiToStabilize))
      waitForGuiToStabilize = Boolean.valueOf( (String) extras.get(uiaDaemonParam_waitForGuiToStabilize));

    if (extras.containsKey(uiaDaemonParam_waitForWindowUpdateTimeout))
      waitForWindowUpdateTimeout = Integer.valueOf( (String) extras.get(uiaDaemonParam_waitForWindowUpdateTimeout));

    if (extras.containsKey(uiaDaemonParam_tcpPort))
      tcpPort = Integer.valueOf( (String) extras.get(uiaDaemonParam_tcpPort));

    Log.w(uiaDaemon_logcatTag, uiaDaemonParam_waitForGuiToStabilize + "=" + waitForGuiToStabilize);
    Log.w(uiaDaemon_logcatTag, uiaDaemonParam_waitForWindowUpdateTimeout + "=" + waitForWindowUpdateTimeout);
    Log.w(uiaDaemon_logcatTag, uiaDaemonParam_tcpPort + "=" + tcpPort);

    saveLogcatToFile();

    IUiAutomatorDaemonDriver uiAutomatorDaemonDriver = new UiAutomatorDaemonDriver(waitForGuiToStabilize, waitForWindowUpdateTimeout);
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

  private void saveLogcatToFile() {
    String fileName = logcatLogFileName;
    File outputFile = new File(InstrumentationRegistry.getTargetContext().getFilesDir(), fileName);

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