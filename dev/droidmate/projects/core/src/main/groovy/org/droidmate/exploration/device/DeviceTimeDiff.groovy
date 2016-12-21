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

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.DeviceException
import org.droidmate.apis.ITimeFormattedLogcatMessage
import org.droidmate.apis.TimeFormattedLogcatMessage
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.misc.MonitorConstants

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * <p>
 * The device has different internal clock than the host machine. This class represents the time diff between the clocks.
 *
 * </p><p>
 * Use {@link DeviceTimeDiff#sync} on a device clock time to make it in sync with the host machine clock time.
 *
 * </p><p>
 * For example, if the device clock is 3 seconds into the future as compared to the host machine clock,
 * 3 seconds will be subtracted from the sync() input, {@code deviceTime}.
 *
 * </p>
 */
@Slf4j
 class DeviceTimeDiff implements IDeviceTimeDiff
{

  private final IExplorableAndroidDevice device

  private Duration diff = null

  DeviceTimeDiff(IExplorableAndroidDevice device)
  {
    this.device = device
  }

  @Override
  LocalDateTime sync(LocalDateTime deviceTime) throws DeviceException
  {
    assert deviceTime != null

    if (diff == null)
      diff = computeDiff(device)
    assert diff != null

    return deviceTime.minus(diff)
  }

  private Duration computeDiff(IExplorableAndroidDevice device) throws DeviceException
  {
    LocalDateTime deviceTime = device.getCurrentTime()
    LocalDateTime now = LocalDateTime.now()
    Duration diff = Duration.between(now, deviceTime)

    def formatter = DateTimeFormatter.ofPattern(
      MonitorConstants.monitor_time_formatter_pattern, MonitorConstants.monitor_time_formatter_locale)
    String msg = "computeDiff(device) result: " +
      "Current time: ${now.format(formatter)} " +
      "Device time: ${deviceTime.format(formatter)} " +
      "Resulting diff: ${diff.toString()}"

    log.trace(msg)

    assert diff != null
    return diff
  }

  @Override
  List<ITimeFormattedLogcatMessage> syncMessages(List<ITimeFormattedLogcatMessage> messages) throws DeviceException
  {
    return messages.collect {
      
//      log.trace("syncing: curr diff: ${this.diff} log dev. time: $it.time tag: $it.tag pid: $it.pidString, payload first 200 chars: ${it.messagePayload.take(200)}")
      
      TimeFormattedLogcatMessage.from(
        this.sync(it.time),
        it.level, it.tag, it.pidString, it.messagePayload)
    }

  }

  @Override
  void reset()
  {
     diff = null
  }
}
