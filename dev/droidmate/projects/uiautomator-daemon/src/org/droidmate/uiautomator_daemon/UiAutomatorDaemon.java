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

package org.droidmate.uiautomator_daemon;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import java.io.File;
import java.io.IOException;

import static org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.*;

@TargetApi(Build.VERSION_CODES.FROYO)
public class UiAutomatorDaemon extends UiAutomatorTestCase
{

  @SuppressWarnings("UnusedDeclaration")
  /* Will be launched by org.droidmate.android_sdk.AdbWrapper.startUiaDaemon().
     See also:
     org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.uiaDaemon_initMethodName
  */
  public void init()
  {
    try
    {
      saveLogcatToFile();
    } catch (Throwable t)
    {
      Log.e(uiaDaemon_logcatTag, "init.saveLogcatToFile() / FAILURE", t);
    }
    
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

  private void saveLogcatToFile() throws UiAutomatorDaemonException
  {
    String fileName = logcatLogFileName;
    File dataDir = getLogcatDir();
    File outputFile = new File(dataDir, fileName);

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
      Runtime.getRuntime().exec(String.format("logcat -v time -f %s *:D %s:W %s:D %s:D dalvikvm:I ActivityManager:V " +
        "AccessibilityNodeInfoDumper:S View:E ResourceType:E HSAd-HSAdBannerView:I" ,
        outputFile.getAbsolutePath(), instrumentation_redirectionTag, uiaDaemon_logcatTag, SerializableTCPServerBase.tag));
    } catch (IOException e)
    {
      Log.wtf(uiaDaemon_logcatTag, e);
    }
  }

  private File getLogcatDir() throws UiAutomatorDaemonException
  {
    File dataDir = Environment.getDataDirectory();
    
    if (!deviceLogcatLogDir_api19.startsWith(dataDir.toString().substring(1)))
      throw new UiAutomatorDaemonException(
        "The device logcat log dir should point to a subdirectory of device data dir. It doesn't. " +
          "The device data dir: "+dataDir.toString()+ " The device logcat log dir: "+ deviceLogcatLogDir_api19);
    
    File logcatDir = new File(deviceLogcatLogDir_api19);
      
    if (!logcatDir.isDirectory())
      if (!logcatDir.mkdirs())
        throw new UiAutomatorDaemonException("!logcatDir.isDirectory() && !logcatDir.mkdirs(). logcatDir: "+logcatDir.toString());
    return logcatDir;
  }
}
