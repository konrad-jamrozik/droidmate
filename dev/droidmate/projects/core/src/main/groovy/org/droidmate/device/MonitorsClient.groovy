// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.device

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.DeviceNeedsRebootException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.misc.MonitorConstants

@Slf4j
class MonitorsClient implements IMonitorsClient
{

  private final ISerializableTCPClient<String, ArrayList<ArrayList<String>>> monitorTcpClient

  private final String deviceSerialNumber

  private final IAdbWrapper adbWrapper

  MonitorsClient(int socketTimeout, String deviceSerialNumber, IAdbWrapper adbWrapper)
  {
    this.monitorTcpClient = new SerializableTCPClient<>(socketTimeout)
    this.deviceSerialNumber = deviceSerialNumber
    this.adbWrapper = adbWrapper
  }

  @Override
  public boolean anyMonitorIsReachable() throws DeviceNeedsRebootException, DeviceException
  {
    boolean out = ports.any {
      this.isServerReachable(it)
    }
    if (out)
      log.trace("At least one monitor is reachable.")
    else
      log.trace("No monitor is reachable.")
    return out
  }

  private Boolean isServerReachable(int port)
  {
    ArrayList<ArrayList<String>> out
    try
    {
      out = this.monitorTcpClient.queryServer(MonitorConstants.srvCmd_connCheck, port)
    } catch (TcpServerUnreachableException ignored)
    {
      return false
    }

    ArrayList<String> diagnostics = out.findSingle()
    assert diagnostics.size() >= 2
    String pid = diagnostics[0]
    String packageName = diagnostics[1]
    log.trace("Reached server at port $port. PID: $pid package: $packageName")
    return true
  }

  @Override
  public ArrayList<ArrayList<String>> getCurrentTime() throws DeviceNeedsRebootException, DeviceException
  {
    ArrayList<ArrayList<String>> out = ports.findResult {
      try
      {
        return monitorTcpClient.queryServer(MonitorConstants.srvCmd_get_time, it)

      } catch (DeviceNeedsRebootException e)
      {
        throw e

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
  public ArrayList<ArrayList<String>> getLogs() throws DeviceNeedsRebootException, DeviceException
  {
    Collection<ArrayList<ArrayList<String>>> out = ports.findResults {
      try
      {
        return monitorTcpClient.queryServer(MonitorConstants.srvCmd_get_logs, it)
      } catch (TcpServerUnreachableException ignored)
      {
        log.trace("Did not reach monitor TCP server at port $it when sending out ${MonitorConstants.srvCmd_get_logs} request.")
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
  void closeMonitorServers() throws DeviceException
  {
    ports.each { 
      try
      {
        monitorTcpClient.queryServer(MonitorConstants.srvCmd_close, it)
      } catch (TcpServerUnreachableException ignored)
      {
        log.trace("Did not reach monitor TCP server at port $it when sending out ${MonitorConstants.srvCmd_close} request.")
      } 
    }
  }

  @Override
  List<Integer> getPorts()
  {
    return MonitorConstants.serverPorts.collect {it}
  }

  @Override
  void forwardPorts()
  {
    this.ports.each {this.adbWrapper.forwardPort(this.deviceSerialNumber, it)}
  }
}
