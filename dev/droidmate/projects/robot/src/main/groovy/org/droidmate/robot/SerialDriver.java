// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

import com.google.common.base.Stopwatch;
import gnu.io.*;
import org.droidmate.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static org.droidmate.common.logging.Markers.serialDriver;

public class SerialDriver implements ISerialDriver
{
  private InputStream serialPortIn;
  private OutputStream serialPortOut;
  private SerialPort serialPort;

  /** Last string that was sent to serial port using the {@link #send(String)} method. */
  private String lastSentString;

  private RobotConfiguration robotConfig;

  private static Logger log = LoggerFactory.getLogger(SerialDriver.class.getSimpleName());

  SerialDriver(RobotConfiguration robotConfig)
  {
    this.robotConfig = robotConfig;
  }

  public Vector<String> getSerialPortNames()
  {
    log.debug("Getting serial port names.");

    Enumeration<CommPortIdentifier> portEnum;
    Vector<String> portVect = new Vector<String>();
    //noinspection unchecked
    portEnum = CommPortIdentifier.getPortIdentifiers();

    CommPortIdentifier portId;
    while (portEnum.hasMoreElements())
    {
      portId = portEnum.nextElement();
      if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
      {
        portVect.add(portId.getName());
      }
    }

    log.debug("Found {} port(s).", portVect.size());
    return portVect;
  }

  public void connect(String portName) throws RobotException
  {
    log.debug("Connecting to serial port {}...", portName);

    CommPortIdentifier portIdentifier;
    try
    {
      portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

      if (portIdentifier.isCurrentlyOwned())
        throw new RobotException(String.format("Port %s is currently in use.", portName));

      CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

      if (commPort instanceof SerialPort)
      {
        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        serialPortIn = serialPort.getInputStream();
        serialPortOut = serialPort.getOutputStream();

      } else
        throw new RobotException(String.format("The port %s is not instance of a SerialPort!", portName));

    } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException e)
    {
      throw new RobotException(e);
    }

    log.debug("DONE connecting to serial port {}. Connected successfully.", portName);
  }

  @Override
  public void send(String string) throws RobotException
  {
    try
    {
      log.trace("Sending to serial port: " + string);
      serialPortOut.write((string + "\n").getBytes());
    } catch (IOException e)
    {
      throw new RobotException(e);
    }
    lastSentString = string;

    log.trace(serialDriver, "SEND {}", string);

  }


  /**
   * The bytes will be received from {@link #serialPortIn} and converted to string until {@code ok} is encountered
   * or a fixed amount of time passes, given as a command line argument {@link Configuration}
   */
  @Override
  public String receive() throws RobotException
  {
    return receive(robotConfig.robotResponseConfirmation);
  }

  @Override
  public String receive(String expectedResponse) throws RobotException
  {
    log.trace("Receiving from serial port...");

    StringBuilder receipt = new StringBuilder();

    // Requires Guava 15.0.0
//    Stopwatch executionTimeStopwatch = Stopwatch.createStarted();
    // Doesn't require Guava 15.0.0, deprecated!
    // TODO this worked normally contrary to the bug described in build.gradle, but it fails on CeBIT 2014 iMac / MacOSX. "NoSuchMethodException".
    Stopwatch stopwatch = Stopwatch.createUnstarted();

    byte[] buffer = new byte[1024];
    try
    {
      int bytesRead = serialPortIn.read(buffer);

      // commented out because it is too verbose!
//      log.trace("Number of bytes read from serial port InputStream: {}", bytesRead);

      String answer = new String(Arrays.copyOfRange(buffer, 0, bytesRead));

      // commented out because it is too verbose!
//      log.trace("Received from serial port: " + answer);

      receipt.append(answer);

      for (int i = 0; i < bytesRead; i++)
        buffer[i] = 0;

      stopwatch.start();

      while (noExpectedResponseFromRobotYet(receipt, expectedResponse, stopwatch))
      {
        bytesRead = serialPortIn.read(buffer);

        // commented out because it is too verbose!
//        log.trace("Number of bytes read from serial port InputStream: {}", bytesRead);

        answer = new String(Arrays.copyOfRange(buffer, 0, bytesRead));

        // commented out because it is too verbose!
//        log.trace("Received from serial port: " + answer);

        receipt.append(answer);

        for (int i = 0; i < bytesRead; i++)
          buffer[i] = 0;
      }

      stopwatch.stop();

    } catch (IOException e)
    {
      throw new RobotException(e);
    }

    if (!receipt.toString().contains(expectedResponse))
      throw new RobotException(String.format(
        "Robot didn't send successful command completion confirmation.\n" +
          "Expected: %s\n" +
          "Got instead: %s",
        expectedResponse, receipt.toString()));

    log.trace("DONE receiving from serial port. Message received: {}", receipt.toString());
    log.trace(serialDriver, "RECV {}", receipt.toString());

    return receipt.toString();
  }

  private boolean noExpectedResponseFromRobotYet(StringBuilder receipt, String expectedResponse, Stopwatch stopwatch)
  {
    return !receipt.toString().contains(expectedResponse)
      // TODO more CeBIT 2014 quick fixes, because f*** yea
      && stopwatch.elapsed(TimeUnit.MILLISECONDS) <= robotConfig.robotResponseTimeout;
  }

  @Override
  public void close()
  {
    log.info("Closing serial port.");
    serialPort.close();
  }
}
