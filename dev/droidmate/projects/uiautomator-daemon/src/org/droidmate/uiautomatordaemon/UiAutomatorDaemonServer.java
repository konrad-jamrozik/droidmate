// Copyright (c) 2012-2016 Saarland University
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
import android.util.Log;
import org.droidmate.common_android.DeviceCommand;
import org.droidmate.common_android.DeviceResponse;
import org.droidmate.common_android.UiAutomatorDaemonException;

import static org.droidmate.common_android.Constants.*;

public class UiAutomatorDaemonServer extends SerializableTCPServerBase<DeviceCommand, DeviceResponse>
{

  private IUiAutomatorDaemonDriver uiaDaemonDriver;

  public UiAutomatorDaemonServer(IUiAutomatorDaemonDriver uiaDaemonDriver)
  {
    super(UIADAEMON_SERVER_START_TAG, UIADAEMON_SERVER_START_MSG);
    this.uiaDaemonDriver = uiaDaemonDriver;
  }

  @TargetApi(Build.VERSION_CODES.FROYO) // for Log.wtf
  @Override
  protected DeviceResponse OnServerRequest(DeviceCommand deviceCommand, Exception deviceCommandReadEx)
  {

    try
    {
      if (deviceCommandReadEx != null)
        throw deviceCommandReadEx;

      assert deviceCommand != null;

      return uiaDaemonDriver.executeCommand(deviceCommand);

    } catch (UiAutomatorDaemonException e)
    {
      Log.e(uiaDaemon_logcatTag, String.format("Server: Failed to execute command %s and thus, obtain appropriate GuiState. " +
        "Returning exception-DeviceResponse.", deviceCommand), e);

      DeviceResponse exceptionDeviceResponse = new DeviceResponse();
      exceptionDeviceResponse.throwable = e;

      return exceptionDeviceResponse;
    } catch (Throwable t)
    {
      Log.wtf(uiaDaemon_logcatTag, String.format(
        "Server: Failed, with a non-"+UiAutomatorDaemonException.class.getSimpleName()+" (!), to execute command %s and thus, " +
        "obtain appropriate GuiState. Returning throwable-DeviceResponse.", deviceCommand), t);

      DeviceResponse throwableDeviceResponse = new DeviceResponse();
      throwableDeviceResponse.throwable = t;

      return throwableDeviceResponse;
    }

  }

  @Override
  protected boolean shouldCloseServerSocket(DeviceCommand deviceCommand) {

    return deviceCommand == null || deviceCommand.command.equals(DEVICE_COMMAND_STOP_UIADAEMON);

  }

}
