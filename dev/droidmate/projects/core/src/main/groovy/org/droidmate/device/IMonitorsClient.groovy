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
import org.droidmate.exceptions.TcpServerUnreachableException

interface IMonitorsClient
{

  boolean appIsReachable()

  ArrayList<ArrayList<String>> getCurrentTime() throws TcpServerUnreachableException, DeviceException

  ArrayList<ArrayList<String>> getLogs() throws TcpServerUnreachableException, DeviceException

  List<Integer> getPorts()
}