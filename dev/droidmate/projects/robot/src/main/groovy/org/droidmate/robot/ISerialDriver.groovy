// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

public interface ISerialDriver
{
  Vector<String> getSerialPortNames()

  void connect(String serialPortName) throws RobotException

  void send(String string) throws RobotException

  String receive() throws RobotException

  String receive(String expectedResponse) throws RobotException

  void close()
}