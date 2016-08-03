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

class TcpClients implements ITcpClients
{

  @Delegate
  private final IMonitorsClient          monitorsClient
  @Delegate
  private final IUiautomatorDaemonClient uiautomatorClient

  TcpClients(
    IAdbWrapper adbWrapper,
    String deviceSerialNumber,
    int socketTimeout,
    int uiautomatorDaemonTcpPort,
    int uiautomatorDaemonServerStartTimeout,
    int uiautomatorDaemonServerStartQueryDelay)
  {
    this.uiautomatorClient = new UiautomatorDaemonClient(
      adbWrapper,
      deviceSerialNumber,
      uiautomatorDaemonTcpPort,
      socketTimeout,
      uiautomatorDaemonServerStartTimeout,
      uiautomatorDaemonServerStartQueryDelay)

    this.monitorsClient = new MonitorsClient(socketTimeout, deviceSerialNumber, adbWrapper)
  }

  @Override
  void forwardPorts() throws DeviceException
  {
    this.uiautomatorClient.forwardPort()
    this.monitorsClient.forwardPorts()
  }
}
