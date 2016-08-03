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
package org.droidmate.device

import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceNeedsRebootException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.uiautomator_daemon.DeviceCommand
import org.droidmate.uiautomator_daemon.DeviceResponse
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants

class UiautomatorDaemonClient implements IUiautomatorDaemonClient
{

  private final ISerializableTCPClient<DeviceCommand, DeviceResponse> client

  private final IAdbWrapper adbWrapper
  private final String      deviceSerialNumber
  private final int         port
  private final int         serverStartTimeout
  private final int         serverStartQueryDelay

  private Thread uiaDaemonThread

  UiautomatorDaemonClient(IAdbWrapper adbWrapper, String deviceSerialNumber, int port, int socketTimeout, int serverStartTimeout, int serverStartQueryDelay)
  {
    this.adbWrapper = adbWrapper
    this.deviceSerialNumber = deviceSerialNumber
    this.port = port
    this.serverStartTimeout = serverStartTimeout
    this.serverStartQueryDelay = serverStartQueryDelay

    this.client = new SerializableTCPClient<DeviceCommand, DeviceResponse>(socketTimeout)
  }

  @Override
  DeviceResponse sendCommandToUiautomatorDaemon(DeviceCommand deviceCommand) throws DeviceNeedsRebootException, TcpServerUnreachableException, DeviceException
  {
    this.client.queryServer(deviceCommand, this.port)
  }

  @Override
  void forwardPort() throws DeviceException
  {
    this.adbWrapper.forwardPort(this.deviceSerialNumber, this.port)
  }

  @Override
  void startUiaDaemon() throws DeviceException
  {
    this.uiaDaemonThread = startUiaDaemonThread(this.adbWrapper, this.deviceSerialNumber, this.port)

    validateUiaDaemonServerStartLogcatMessages()

    assert getUiaDaemonThreadIsAlive()

  }

  private void validateUiaDaemonServerStartLogcatMessages()
  {
    List<String> msgs = this.adbWrapper.waitForMessagesOnLogcat(
      this.deviceSerialNumber,
      UiautomatorDaemonConstants.UIADAEMON_SERVER_START_TAG,
      1,
      this.serverStartTimeout,
      this.serverStartQueryDelay)

    assert !msgs?.empty
    assert (msgs.size() == 1):
      "Expected exactly one message on logcat (with tag $UiautomatorDaemonConstants.UIADAEMON_SERVER_START_MSG) " +
        "confirming that uia-daemon server has started. Instead, got ${msgs.size()} messages. Msgs:\n${msgs.join("\n")}"
    assert msgs[0].contains(UiautomatorDaemonConstants.UIADAEMON_SERVER_START_MSG)
  }

  @Override
  public boolean getUiaDaemonThreadIsAlive()
  {
    assert this.uiaDaemonThread != null
    return this.uiaDaemonThread.alive
  }

  private static Thread startUiaDaemonThread(IAdbWrapper adbWrapper, String deviceSerialNumber, int port)
  {
    return Thread.startDaemon(new UiAutomatorDaemonThread(adbWrapper, deviceSerialNumber, port).&run)
  }

  @Override
  public void waitForUiaDaemonToClose() throws DeviceException
  {
    assert (uiaDaemonThread != null)
    try
    {
      uiaDaemonThread.join()
    } catch (InterruptedException e)
    {
      throw new DeviceException(e)
    }
  }

}


