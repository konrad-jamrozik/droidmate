// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device

import org.droidmate.exceptions.DeviceException

interface IMonitorsClient
{

  boolean anyMonitorIsReachable() throws DeviceException

  ArrayList<ArrayList<String>> getCurrentTime() throws DeviceException

  ArrayList<ArrayList<String>> getLogs() throws DeviceException

  List<Integer> getPorts()

  void forwardPorts() throws DeviceException
}