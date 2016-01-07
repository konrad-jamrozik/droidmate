// Copyright (c) 2012-2016 Saarland University
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
class MonitorsClient implements IMonitorsClient
{

  private final ISerializableTCPClient<String, ArrayList<ArrayList<String>>> monitorTcpClient

  MonitorsClient(int socketTimeout)
  {
    this.monitorTcpClient = new SerializableTCPClient<>(socketTimeout)
  }

  @Override
  public boolean anyMonitorIsReachable()
  {
    ports.any {
      this.monitorTcpClient.isServerReachable(it)
    }
  }

  @Override
  public ArrayList<ArrayList<String>> getCurrentTime() throws TcpServerUnreachableException, DeviceException
  {
    ArrayList<ArrayList<String>> out = ports.findResult {
      try
      {
        return monitorTcpClient.queryServer(MonitorJavaTemplate.srvCmd_get_time, it)
      } catch (TcpServerUnreachableException ignored)
      {
        log.trace("Did not reach monitor TCP server at port $it.")
        return null
      }
    }

    if (out == null)
      throw new DeviceException("None of the monitor TCP servers were available.", /* stopFurtherApkExplorations */ true)

    assert out != null
    return out
  }

  @Override
  public ArrayList<ArrayList<String>> getLogs() throws TcpServerUnreachableException, DeviceException
  {
    Collection<ArrayList<ArrayList<String>>> out = ports.findResults {
      try
      {
        return monitorTcpClient.queryServer(MonitorJavaTemplate.srvCmd_get_logs, it)
      } catch (TcpServerUnreachableException ignored)
      {
        log.trace("Did not reach monitor TCP server at port $it.")
        return null
      }
    }
    assert out != null

    if (out.empty)
    {
      log.trace("None of the monitor TCP servers were available while obtaining API logs.")
      return []
    }

    assert !out.empty
    return (out as Iterable<Iterable>).shallowFlatten()
  }

  @Override
  List<Integer> getPorts()
  {
    return MonitorJavaTemplate.serverPorts
  }
}
