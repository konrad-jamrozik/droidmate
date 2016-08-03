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


import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import org.droidmate.uiautomator_daemon.DeviceCommand;
import org.droidmate.uiautomator_daemon.DeviceResponse;
import org.droidmate.uiautomator_daemon.UiAutomatorDaemonException;

import static org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.*;

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
