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

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IAdbWrapper

@Slf4j
class UiAutomatorDaemonThread implements Runnable
{

  private final String      deviceSerialNumber
  private final IAdbWrapper adbWrapper
  private final int         port

  UiAutomatorDaemonThread(IAdbWrapper adbWrapper, String deviceSerialNumber, int port)
  {
    this.adbWrapper = adbWrapper
    this.deviceSerialNumber = deviceSerialNumber
    this.port = port
  }

  @Override
  public void run()
  {
    try
    {
      this.adbWrapper.startUiautomatorDaemon(this.deviceSerialNumber, this.port)
    } catch (Throwable e)
    {
      log.error("$UiAutomatorDaemonThread.simpleName threw ${e.class.simpleName}. The exception:\n", e)
    }
  }

}
