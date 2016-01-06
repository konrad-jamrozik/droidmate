// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device

import org.droidmate.lib_android.MonitorJavaTemplate

class MonitorsClient implements IMonitorsClient
{

  private final ISerializableTCPClient<String, ArrayList<ArrayList<String>>> monitorTcpClient

  MonitorsClient(int socketTimeout)
  {
    this.monitorTcpClient = new SerializableTCPClient<>(socketTimeout)
  }

  @Override
  public boolean appIsReachable()
  {
    return this.monitorTcpClient.isServerReachable(MonitorJavaTemplate.srv_port1)
  }

  @Override
  public ArrayList<ArrayList<String>> getCurrentTime()
  {
    return monitorTcpClient.queryServer(MonitorJavaTemplate.srvCmd_get_time, MonitorJavaTemplate.srv_port1)
  }

  @Override
  public ArrayList<ArrayList<String>> getLogs()
  {
    return monitorTcpClient.queryServer(MonitorJavaTemplate.srvCmd_get_logs, MonitorJavaTemplate.srv_port1)
  }

  @Override
  List<Integer> getPorts()
  {
    return [MonitorJavaTemplate.srv_port1, MonitorJavaTemplate.srv_port2]
  }
}
