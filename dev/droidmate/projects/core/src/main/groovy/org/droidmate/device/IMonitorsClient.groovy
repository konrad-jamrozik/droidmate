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
import org.droidmate.exceptions.DeviceNeedsRebootException

interface IMonitorsClient
{
  boolean anyMonitorIsReachable() throws DeviceNeedsRebootException, DeviceException

  ArrayList<ArrayList<String>> getCurrentTime() throws DeviceNeedsRebootException, DeviceException

  ArrayList<ArrayList<String>> getLogs() throws DeviceNeedsRebootException, DeviceException

  void closeMonitorServers() throws DeviceException

  List<Integer> getPorts()

  void forwardPorts() throws DeviceException
}