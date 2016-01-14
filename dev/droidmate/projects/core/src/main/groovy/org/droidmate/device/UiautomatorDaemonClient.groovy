// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device

import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.common_android.Constants
import org.droidmate.common_android.DeviceCommand
import org.droidmate.common_android.DeviceResponse
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.TcpServerUnreachableException

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
  DeviceResponse sendCommandToUiautomatorDaemon(DeviceCommand deviceCommand) throws TcpServerUnreachableException, DeviceException, ConnectException
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
      Constants.UIADAEMON_SERVER_START_TAG,
      1,
      this.serverStartTimeout,
      this.serverStartQueryDelay)

    assert !msgs?.empty
    assert (msgs.size() == 1):
      "Expected exactly one message on logcat (with tag $Constants.UIADAEMON_SERVER_START_MSG) " +
        "confirming that uia-daemon server has started. Instead, got ${msgs.size()} messages. Msgs:\n${msgs.join("\n")}"
    assert msgs[0].contains(Constants.UIADAEMON_SERVER_START_MSG)
  }

  @Override
  public boolean getUiaDaemonThreadIsAlive()
  {
    assert this.uiaDaemonThread != null
    return this.uiaDaemonThread.alive
  }

  private static Thread startUiaDaemonThread(IAdbWrapper adbWrapper, String deviceSerialNumber, int port)
  {
    // KJA consider collapsing to groovys Thread.start {}.
    // http://docs.groovy-lang.org/latest/html/groovy-jdk/java/lang/Thread.html
    // WISH consider making it a daemon thread
    // See http://stackoverflow.com/questions/2213340/what-is-daemon-thread-in-java
    // http://stackoverflow.com/questions/19421027/how-to-create-a-daemon-thread-and-what-for?rq=1

    Thread uiaDaemonThread = new Thread(new UiAutomatorDaemonThread(adbWrapper, deviceSerialNumber, port))
    uiaDaemonThread.start()
    return uiaDaemonThread
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


