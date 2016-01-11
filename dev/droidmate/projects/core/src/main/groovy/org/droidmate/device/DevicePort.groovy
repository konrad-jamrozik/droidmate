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

class DevicePort implements IDevicePort
{

  private final IAdbWrapper adbWrapper
  private final String      deviceSerialNumber
  private final int         port

  DevicePort(IAdbWrapper adbWrapper, String deviceSerialNumber, int port)
  {
    this.adbWrapper = adbWrapper
    this.port = port
    this.deviceSerialNumber = deviceSerialNumber
  }

  @Override
  Socket getSocket(String serverAddress)
  {
    Socket socket
    try
    {
      // KJA curr work
      // KJA2 KNOWN BUG sometimes device loses connection for a microsecond, breaking port forwards. If this happens, just
      // reestablish ports.
      // Managed to get here "java.net.ConnectException: Connection refused: connect" when I manually unplugged the USB cable
      // during a test. For logs, see: C:\my\local\repos\chair\droidmate\resources\debug_logs\forced_manual_usb_cable_unplug
      //
      // Observation 1: this happens when device is not reachable at all, e.g. USB got unplugged. However, this does NOT happen
      // if the package with the server was force-stopped. Instead, TcpServerUnreachableException is thrown on constructing
      // ObjectInputStream from socket.inputStream below.
      //
      // Observation 2: the "java.net.ConnectException: Connection refused: connect" happens if the port hasn't been forwarded.
      // I.e.: this:
      // new Socket("localhost", MonitorJavaTemplate.srv_port1) // port is 59776
      // will return
      // java.net.ConnectException: Connection refused: connect
      // unless first AndroidDevice.forwardPort(MonitorJavaTemplate.srv_port1)
      // is made. In such case, it will work just fine.
      //
      // Observation 3: this also happens if the device displays pop-up box: "The page at www.soccerdrills.de says: blah blah"
      // It has "cancel" and "ok" buttons. Closing the dialog didn't help, I had to do port forward like:
      // adb forward tcp:59776 tcp:59776
      socket = new Socket(serverAddress, this.port)
    } catch (ConnectException e)
    {
      throw e
    }
    return socket

  }

  @Override
  void forward()
  {
    this.adbWrapper.forwardPort(this.deviceSerialNumber, this.port)
  }

//  OutputFromServerT queryServerGuardingAgainstConnectException(InputToServerT input, int port)
//    throws TcpServerUnreachableException, DeviceException
//  {
//    // KJA current work
//    OutputFromServerT output = null
//    Utils.retryOnFalse( {
//      try
//      {
//        output = this._queryServer(input, port) as OutputFromServerT
//      } catch (ConnectException exception)
//      {
//        // KJA forward ports here
//        return false
//      }
//      assert output != null
//      return true
//    }, 0,0) // KJA attempts + delays
//
//    return output
//  }
}
