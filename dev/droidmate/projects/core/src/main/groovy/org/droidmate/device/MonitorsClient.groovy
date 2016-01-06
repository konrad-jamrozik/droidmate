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

  private final int monitorTcpPort

  MonitorsClient(int socketTimeout, int monitorTcpPort)
  {
    this.monitorTcpClient = new SerializableTCPClient<>(socketTimeout)
    this.monitorTcpPort = monitorTcpPort
  }

  @Override
  public boolean appIsReachable()
  {
    return this.monitorTcpClient.isServerReachable(this.monitorTcpPort)
  }

  @Override
  public ArrayList<ArrayList<String>> getCurrentTime()
  {
    // KJA next: MonitorJavaTemplate.srv_port and remove cfg param
    return monitorTcpClient.queryServer(MonitorJavaTemplate.srvCmd_get_time, this.monitorTcpPort)
  }

  @Override
  public ArrayList<ArrayList<String>> getLogs()
  {
    return monitorTcpClient.queryServer(MonitorJavaTemplate.srvCmd_get_logs, this.monitorTcpPort)
  }


}
