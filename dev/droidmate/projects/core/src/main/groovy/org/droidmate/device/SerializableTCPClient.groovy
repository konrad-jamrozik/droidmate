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

  protected final String serverAddress = "localhost";
  private int socketTimeout


  public SerializableTCPClient(int socketTimeout)
  {
    this.socketTimeout = socketTimeout
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

  /**
   * Sends through TCP socket the serialized {@code input} to server under {@link #serverAddress}:{@code port}.<br/>
   * Next, waits until server returns his answer and returns it.
   */
  @Override
  @SuppressWarnings("unchecked")
  public OutputFromServerT queryServer(InputToServerT input, int port) throws TcpServerUnreachableException, DeviceException
  {

    OutputFromServerT output
    try
    {
      log.trace("Socket socket = new Socket($serverAddress, $port)")

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
      Socket socket = new Socket(serverAddress, port)
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
        inputStream = new ObjectInputStream(socket.inputStream)
      } catch (EOFException e)
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
}
