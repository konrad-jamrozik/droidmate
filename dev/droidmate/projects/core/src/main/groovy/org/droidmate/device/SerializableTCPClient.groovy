// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device

import groovy.util.logging.Slf4j
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.lib_android.MonitorJavaTemplate

@Slf4j
public class SerializableTCPClient<InputToServerT extends Serializable, OutputFromServerT extends Serializable> implements ISerializableTCPClient<InputToServerT, OutputFromServerT>
{

  private final String        serverAddress = "localhost"
  private final int           socketTimeout
  private final IDeviceReboot deviceReboot


  public SerializableTCPClient(int socketTimeout, IDeviceReboot deviceReboot)
  {
    this.socketTimeout = socketTimeout
    this.deviceReboot = deviceReboot
  }

  @Override
  Boolean isServerReachable(int port) throws DeviceException
  {
    try
    {
      return this.queryServer(MonitorJavaTemplate.srvCmd_connCheck, port) != null
    } catch (TcpServerUnreachableException ignored)
    {
      return false
    } catch (Throwable throwable)
    {
      throw new DeviceException("Unexpected Throwable while checking if isServerReachable. " +
        "The Throwable is given as a cause of this exception.", throwable, true)
    }
  }

  public OutputFromServerT queryServer(InputToServerT input, int port) throws TcpServerUnreachableException, DeviceException
  {
    OutputFromServerT output
    try
    {
      output = this._queryServer(input, port) as OutputFromServerT

    } catch (ConnectException exception)
    {
      log.debug("Querying server resulted in $exception. Rebooting device and trying again.")

      this.deviceReboot.tryRun()

      try
      {
        output = this._queryServer(input, port) as OutputFromServerT

      } catch (ConnectException exception2)
      {
        throw new DeviceException("Querying server resulted in $exception2 even after device reboot.", /* stopFurtherApkExplorations */ true)
      }
    }

    assert output != null
    return output
  }

  /**
   * Sends through TCP socket the serialized {@code input} to server under {@link #serverAddress}:{@code port}.<br/>
   * Next, waits until server returns his answer and returns it.
   */
  @SuppressWarnings("unchecked")
  private OutputFromServerT _queryServer(InputToServerT input, int port) throws TcpServerUnreachableException, DeviceException, ConnectException
  {

    OutputFromServerT output
    try
    {
      log.trace("Socket socket = new Socket($serverAddress, $port)")

      Socket socket = this.tryGetSocket(serverAddress, port)

      socket.soTimeout = this.socketTimeout

      ObjectInputStream inputStream

      // This will block until corresponding socket output stream (located on server) is flushed.
      //
      // Reference:
      // 1. the ObjectInputStream constructor comment.
      // 2. search for: "Note - The ObjectInputStream constructor blocks until" in:
      // http://docs.oracle.com/javase/7/docs/platform/serialization/spec/input.html
      //
      try
      {
//        log.trace("inputStream = new ObjectInputStream(socket.inputStream)")
        // Got here once java.net.SocketTimeoutException: Read timed out on
        // monitorTcpClient.queryServer(MonitorJavaTemplate.srvCmd_get_logs, it)
        // KJA port reforwarding results in java.io.EOFException: null here on ObjectInputStream.<init> on uiautomator client
        inputStream = new ObjectInputStream(socket.inputStream)
      } catch (EOFException e)
      {
        throw new TcpServerUnreachableException(e)
      } catch (SocketTimeoutException e)
      {
        throw new TcpServerUnreachableException(e)
      }
      assert inputStream != null

      ObjectOutputStream outputStream
      try
      {
//        log.trace("outputStream = new ObjectOutputStream(socket.outputStream)")
        outputStream = new ObjectOutputStream(socket.outputStream)
      } catch (EOFException e)
      {
        throw new TcpServerUnreachableException(e)
      }
      assert outputStream != null

//      log.trace("outputStream.writeObject(input)")
      outputStream.writeObject(input)
      outputStream.flush()

//      log.trace("output = (OutputFromServerT) inputStream.readObject()")
      // Managed to get here "java.io.EOFException: null" when I manually unplugged the USB cable
      // during a test. For logs, see: C:\my\local\repos\chair\droidmate\resources\debug_logs\forced_manual_usb_cable_unplug
      output = (OutputFromServerT) inputStream.readObject()

      log.trace("socket.close()")
      socket.close()

    }
    catch (IOException | ClassNotFoundException e)
    {
      throw new DeviceException("SerializableTCPClient has thrown exception while querying server.", e, true)
    }

    return output
  }

  // KJA does not help. Instead, do adb reboot with 60 seconds wait.
  // If device is not connected on 'adb reboot':
  // error: device '(null)' not found
  private Socket tryGetSocket(String serverAddress, int port) throws ConnectException
  {
    Socket socket
    try
    {
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
      socket = new Socket(serverAddress, port)
    } catch (ConnectException e)
    {
      throw e
    }
    return socket
  }
}
