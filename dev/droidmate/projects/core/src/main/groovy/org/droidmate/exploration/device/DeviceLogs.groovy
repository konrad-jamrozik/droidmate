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
package org.droidmate.exploration.device

import com.google.common.base.MoreObjects
import groovy.util.logging.Slf4j
import org.droidmate.apis.IApiLogcatMessage

@Slf4j
class DeviceLogs implements IDeviceLogs, Serializable
{
  private static final long serialVersionUID = 1

  List<IApiLogcatMessage> apiLogs = null

  DeviceLogs(List<IApiLogcatMessage> apiLogs)
  {
    this.apiLogs = apiLogs
  }
  
/**
   * For safe variant, use {@link #getApiLogsOrEmpty}
   * @return
   */
  @Override
  List<IApiLogcatMessage> getApiLogsOrNull()
  {
    return apiLogs
  }

  @Override
  List<IApiLogcatMessage> getApiLogsOrEmpty()
  {
    if (!readAnyApiLogsSuccessfully)
      return []

    return apiLogs
  }

  @Override
  boolean getReadAnyApiLogsSuccessfully()
  {
    return apiLogs != null
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
      .add("apiLogs#", this.readAnyApiLogsSuccessfully ? apiLogs.size() : "N/A")
      .toString()
  }

}

