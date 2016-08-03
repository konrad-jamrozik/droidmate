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

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.MonitorConstants
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.exceptions.DeviceException
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDateTime

/**
 * See {@link DeviceMessagesReader}
 */
@TypeChecked
@Slf4j
class InitMsgsReader implements IInitMsgsReader
{

  private final IExplorableAndroidDevice device

  private final int monitorServerStartTimeout
  private final int monitorServerStartQueryDelay

  InitMsgsReader(IExplorableAndroidDevice device, int monitorServerStartTimeout, int monitorServerStartQueryDelay)
  {
    this.device = device
    this.monitorServerStartTimeout = monitorServerStartTimeout
    this.monitorServerStartQueryDelay = monitorServerStartQueryDelay

    assert device != null
  }

  /**
   * <p>
   * The logs logged with the monitor logger will have different timestamps than the logged monitor logs, even though
   * {@link IDeviceTimeDiff} is applied. This is because the method that logs is executed a couple of seconds after the monitor
   * logs were logged on the device. Empirical observation shows this to be up to 9 seconds for "monitor init" msgs.
   *
   * </p>
   */
  private Logger monitorLogger = LoggerFactory.getLogger(LogbackConstants.logger_name_monitor)

  // Used by old exploration code
  @Deprecated
  @Override
  LocalDateTime readMonitorMessages(IDeviceTimeDiff deviceTimeDiff) throws DeviceException
  {
    log.debug("readMonitorMessages(deviceTimeDiff)")
    assert deviceTimeDiff != null

    // This is because this call waits for minimum number of messages.
    List<ITimeFormattedLogcatMessage> messages = device.waitForLogcatMessages(
      MonitorConstants.tag_init, 2, monitorServerStartTimeout, monitorServerStartQueryDelay)
    log.debug("readMonitorMessages(): obtained messages")

    checkCount(messages)
    verifyPayloads(messages)
    checkMonitorCtorStatus(messages)

    assert messages.size() in [2,4]

    log.debug("readMonitorMessages(): syncing time")
    def monitorInitTime = deviceTimeDiff.sync(messages[1].time)

    messages = deviceTimeDiff.syncMessages(messages)

    messages.each {monitorLogger.trace("${it.toLogcatMessageString()}")}

    log.debug("readMonitorMessages(): returning monitorInitTime: $monitorInitTime")
    return monitorInitTime
  }

  public void verifyPayloads(List<ITimeFormattedLogcatMessage> messages)
  {
    assert messages.size() in [2,4]

    assert [MonitorConstants.msg_ctor_success, MonitorConstants.msg_ctor_failure].any {
      messages[0].messagePayload.contains(it)
    }

    assert messages[1].messagePayload.contains(MonitorConstants.msgPrefix_init_success)

    if (messages.size() == 4)
    {
      assert [MonitorConstants.msg_ctor_success, MonitorConstants.msg_ctor_failure].any {
        messages[2].messagePayload.contains(it)
      }

      assert messages[3].messagePayload.contains(MonitorConstants.msgPrefix_init_success)
    }
  }

  public void checkMonitorCtorStatus(List<ITimeFormattedLogcatMessage> messages) throws DeviceException
  {
    assert messages.size() in [2,4]

    if (!messages[0].messagePayload.contains(MonitorConstants.msg_ctor_success))
      throw new DeviceException(
        "Monitor failed to construct without exception. " +
          "Logcat message: ${messages[0].messagePayload}")

    if (messages.size() == 4)
    {
      if (!messages[2].messagePayload.contains(MonitorConstants.msg_ctor_success))
        throw new DeviceException(
          "Second monitor failed to construct without exception. " +
            "Logcat message: ${messages[2].messagePayload}")


    }
  }

  public void checkCount(List<ITimeFormattedLogcatMessage> messages) throws DeviceException
  {
    if (!(messages.size() in [2,4]))
    {
      String msgHint = ""

      if (messages.size() < 2)
        msgHint = " Maybe inlining of the apk failed or it is not inlined at all? In the former case," +
          "logcat should contains log of the underlying exception."

      if (messages.size() > 4)
        msgHint = " Maybe monitor was started more than twice, due to the app spawning more than two processes? " +
          "Such situation is unsupported. To diagnose the issue, please inspect logcat. " +
          "If there are more than two processes started, " +
          "logs like 'I/ActivityManager: Start proc' will be present 3 or more times. " +
          "Also, see the logs with tags from ${MonitorConstants.simpleName}"

      throw new DeviceException("Expected to read from logcat 2 or 4 messages tagged '${MonitorConstants.tag_init}'. " +
        "First (and possibly third) message denoting monitor .ctor() finished. " +
        "Second (and possibly fourth) message denoting monitor .init() finished. " +
        "However, the number of messages is instead: ${messages.size()}. " + msgHint)
    }

    assert messages.size() in [2, 4]
  }

  @Deprecated
  @Override
  List<ITimeFormattedLogcatMessage> readInstrumentationMessages(IDeviceTimeDiff deviceTimeDiff) throws DeviceException
  {
    log.debug("readInstrumentationMessages(deviceTimeDiff)")
    assert deviceTimeDiff != null

    List<ITimeFormattedLogcatMessage> messages = device.readLogcatMessages(UiautomatorDaemonConstants.instrumentation_redirectionTag)

    messages = deviceTimeDiff.syncMessages(messages)

    messages.each {monitorLogger.trace("${it.toLogcatMessageString()}")}

    return messages
  }
}
