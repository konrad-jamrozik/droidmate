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

package org.droidmate.apis

import org.droidmate.misc.MonitorConstants

import java.time.LocalDateTime

@SuppressWarnings("GroovyUnusedDeclaration") // Actually used in org.droidmate.exploration.data_aggregators.ExplorationOutput2Builder.buildDeviceLogs 
class ApiLogcatMessageTestHelper
{

  public static final String log_level_for_testing = "I"

  static IApiLogcatMessage newApiLogcatMessage(Map apiAttributes)
  {
    def time = apiAttributes.remove("time") as LocalDateTime
    apiAttributes["stackTrace"] = apiAttributes["stackTrace"] ?: "$Api.monitorRedirectionPrefix"

    def logcatMessage = TimeFormattedLogcatMessage.from(
      time ?: TimeFormattedLogcatMessage.assumedDate,
      log_level_for_testing,
      MonitorConstants.tag_api,
      "3993", // arbitrary process ID
      ApiLogcatMessage.toLogcatMessagePayload(new Api(apiAttributes))
    )

    return ApiLogcatMessage.from(logcatMessage)
  }


}
