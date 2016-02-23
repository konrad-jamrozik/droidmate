// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.uiautomator2daemon;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.R.attr.handle;
import static org.droidmate.common_android.Constants.*;

// To deploy the APK: adb install -r uiautomator2-daemon-debug.apk
// To start the service: adb shell am startservice --user 0 -a org.droidmate.uiautomator2daemon.UiAutomatorDaemonService -e wait_for_gui_to_stabilize <VALUE> -e wait_for_window_update_timeout <VALUE> -e uiadaemon_server_tcp_port <VALUE>
// To stop service: adb shell am force-stop org.droidmate.uiautomator2daemon.UiAutomatorDaemonServer

// More information on: http://stackoverflow.com/questions/7415997/how-to-start-and-stop-android-service-from-a-adb-shell

public class UiAutomatorDaemonService extends Service
{
  private UiAutomatorDaemon uiAutomatorDaemon;

  private void handleIntent(Intent intent)
  {
    Bundle extras = intent.getExtras();

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

    this.uiAutomatorDaemon = new UiAutomatorDaemon();
    uiAutomatorDaemon.init(waitForGuiToStabilize, waitForWindowUpdateTimeout, tcpPort);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent)
  {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    handleIntent(intent);

    // We want this service to continue running until it is explicitly
    // stopped, so return sticky.
    return START_STICKY;
  }

  @Override
  public void onDestroy()
  {
    // Shut down UI Automator
    this.uiAutomatorDaemon.shutdown();
  }
}
