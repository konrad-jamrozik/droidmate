// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device

import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.android_sdk.IApk
import org.droidmate.common.Boolean3
import org.droidmate.common.logcat.TimeFormattedLogcatMessage
import org.droidmate.common_android.DeviceCommand
import org.droidmate.common_android.DeviceResponse
import org.droidmate.common_android.UiautomatorWindowHierarchyDumpDeviceResponse
import org.droidmate.configuration.Configuration
import org.droidmate.device.datatypes.*
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.NoAndroidDevicesAvailableException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.lib_android.MonitorJavaTemplate
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.awt.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.List

import static org.droidmate.common_android.Constants.*
import static org.droidmate.common_android.NoDeviceResponse.getNoDeviceResponse

/**
 * <p>
 * <i> --- This doc was last reviewed on 21 Dec 2013.</i>
 * </p><p>
 * Provides programmatic access to Android (Virtual) Device. The instance of this class should be available only as a parameter
 * in {@code closure} passed to
 * {@link org.droidmate.tools.IAndroidDeviceDeployer#withSetupDevice(int, Closure)
 * AndroidDeviceDeployer.withSetupDevice(closure)}, thus guaranteeing invariant of this class:
 *
 * </p><p>
 * CLASS INVARIANT: the A(V)D accessed by a instance of this class is setup and available for duration of the instance existence.
 *
 * </p>
 */
@Slf4j
public class AndroidDevice implements IAndroidDevice
{

  private final String        serialNumber
  private final Configuration cfg
  private final IAdbWrapper   adbWrapper
  private final ITcpClients   tcpClients

  AndroidDevice(
    String serialNumber,
    Configuration cfg,
    IAdbWrapper adbWrapper)
  {
    this.serialNumber = serialNumber
    this.cfg = cfg
    this.adbWrapper = adbWrapper
    this.tcpClients = new TcpClients(
      this.adbWrapper,
      this.serialNumber,
      cfg.socketTimeout,
      cfg.uiautomatorDaemonTcpPort,
      cfg.uiautomatorDaemonServerStartTimeout,
      cfg.uiautomatorDaemonServerStartQueryDelay)
  }

  @Override
  void pushJar(File jar) throws DeviceException
  {
    log.debug("pushJar(${jar.path})")
    adbWrapper.pushJar(serialNumber, jar)
  }

  @Override
  boolean hasPackageInstalled(String packageName) throws DeviceException
  {
    log.debug("hasPackageInstalled($packageName)")
    List<String> packageEntries = adbWrapper.listPackages(serialNumber).readLines().findAll {!it.empty}
    return packageEntries.contains("package:" + packageName)
  }

  @Override
  public IDeviceGuiSnapshot getGuiSnapshot() throws DeviceException
  {
    log.debug("getGuiSnapshot()")

    def response = this.issueCommand(
      new DeviceCommand(DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP)) as UiautomatorWindowHierarchyDumpDeviceResponse

    def outSnapshot = new UiautomatorWindowDump(
      response.windowHierarchyDump,
      new Dimension(response.displayWidth, response.displayHeight))

    log.debug("getGuiSnapshot(): $outSnapshot")
    return outSnapshot
  }

  @TypeChecked(TypeCheckingMode.SKIP)
  @Override
  void perform(IAndroidDeviceAction action) throws DeviceException
  {
    log.debug("perform($action)")
    //noinspection GroovyInArgumentCheck
    assert action?.class in [ClickGuiAction, AdbClearPackageAction, LaunchMainActivityDeviceAction]

    //noinspection GroovyAssignabilityCheck
    internalPerform(action)
  }

  // KJA2 multimethod to switch
  DeviceResponse internalPerform(LaunchMainActivityDeviceAction action) throws DeviceException
  {
    launchMainActivity(action.launchableActivityComponentName)
    return noDeviceResponse
  }

  // Used by old exploration code
  @Deprecated
  DeviceResponse internalPerform(AdbClearPackageAction action) throws DeviceException
  {
    clearPackage(action.packageName)
    return noDeviceResponse
  }

  DeviceResponse internalPerform(ClickGuiAction action) throws DeviceException
  {
    return issueCommand(new DeviceCommand(DEVICE_COMMAND_PERFORM_ACTION, action.guiAction))
  }

  public DeviceResponse getIsDeviceOrientationLandscape() throws DeviceException
  {
    log.debug("getIsDeviceOrientationLandscape()")
    return this.issueCommand(new DeviceCommand(DEVICE_COMMAND_GET_IS_ORIENTATION_LANDSCAPE))
  }

  /**
   * <p>
   * Issues given {@code deviceCommand} to the A(V)D, obtains the device answer, checks for errors and return the
   * device response, unless there were errors along the way. If there were errors, it throws an exception.
   * </p><p>
   * The issued command can be potentially handled either by aut-addon or uiautomator-daemon. This method resolves
   * who should be the recipient and sends the command using {@link #uiautomatorClient}.
   *
   * </p><p>
   * <i>This doc was last reviewed on 14 Sep '13.</i>
   * </p>
   */
  private DeviceResponse issueCommand(DeviceCommand deviceCommand) throws DeviceException
  {
    DeviceResponse deviceResponse

    boolean uiaDaemonHandlesCommand = uiaDaemonHandlesCommand(deviceCommand)

    if (!uiaDaemonHandlesCommand)
      throw new DeviceException(String.format("Unhandled command of %s", deviceCommand.command))

    deviceResponse = this.tcpClients.sendCommandToUiautomatorDaemon(deviceCommand)

    assert deviceResponse != null

    throwDeviceResponseThrowableIfAny(deviceResponse)

    assert deviceResponse.throwable == null

    return deviceResponse
  }

  private static void throwDeviceResponseThrowableIfAny(DeviceResponse deviceResponse) throws DeviceException
  {
    if (deviceResponse.throwable != null)
      throw new DeviceException(String.format(
        "Device returned DeviceResponse with non-null throwable, indicating something went horribly wrong on the A(V)D. " +
          "The exception is given as a cause of this one. If it doesn't have enough information, " +
          "try inspecting the logcat output of the A(V)D.",
      ), deviceResponse.throwable)
  }

  @Override
  public void closeConnection() throws DeviceException
  {
    this.stopUiaDaemon()
  }

  private void stopUiaDaemon() throws DeviceException
  {
    log.trace("stopUiaDaemon()")
    this.issueCommand(new DeviceCommand(DEVICE_COMMAND_STOP_UIADAEMON))
    this.tcpClients.waitForUiaDaemonToClose()
    log.trace("DONE stopUiaDaemon()")

  }


  @Override
  void reboot() throws DeviceException
  {
    log.trace("reboot(${this.serialNumber})")
    this.adbWrapper.reboot(this.serialNumber)
  }

  @Override
  boolean isAvailable() throws DeviceException
  {
    log.trace("isAvailable(${this.serialNumber})")
    try
    {
      this.adbWrapper.androidDevicesDescriptors.any {it.deviceSerialNumber == this.serialNumber}
    } catch (NoAndroidDevicesAvailableException ignored)
    {
      return false
    }
  }

  @Override
  boolean uiaDaemonClientThreadIsAlive()
  {
    return this.tcpClients.uiaDaemonThreadIsAlive
  }

  @Override
  void setupConnection() throws DeviceException
  {
    log.trace("setupConnection($serialNumber) / this.tcpClients.forwardPorts()")
    this.tcpClients.forwardPorts()
    log.trace("setupConnection($serialNumber) / this.startUiaDaemon()")
    this.startUiaDaemon()
    log.trace("setupConnection($serialNumber) / DONE")
  }

  @Override
  List<ITimeFormattedLogcatMessage> readLogcatMessages(String messageTag) throws DeviceException
  {
    log.debug("readLogcatMessages(tag: $messageTag)")
    List<String> messages = adbWrapper.readMessagesFromLogcat(this.serialNumber, messageTag)
    return messages.collect {TimeFormattedLogcatMessage.from(it)}
  }

  @Override
  List<ITimeFormattedLogcatMessage> waitForLogcatMessages(String messageTag, int minMessagesCount, int waitTimeout, int queryDelay) throws DeviceException
  {
    log.debug("waitForLogcatMessages(tag: $messageTag, minMessagesCount: $minMessagesCount, waitTimeout: $waitTimeout, queryDelay: $queryDelay)")
    List<String> messages = adbWrapper.waitForMessagesOnLogcat(this.serialNumber, messageTag, minMessagesCount, waitTimeout, queryDelay)
    log.debug("waitForLogcatMessages(): obtained messages: ${messages.join("\n")}")
    return messages.collect {TimeFormattedLogcatMessage.from(it)}
  }

  @Override
  List<List<String>> readAndClearMonitorTcpMessages() throws TcpServerUnreachableException, DeviceException
  {
    log.debug("readAndClearMonitorTcpMessages()")

    ArrayList<ArrayList<String>> msgs = this.tcpClients.getLogs()

    msgs.each {ArrayList<String> msg ->
      assert msg.size() == 3
      assert !(msg[0]?.empty)
      assert !(msg[1]?.empty)
      assert !(msg[2]?.empty)
    }

    return msgs
  }

  @Override
  LocalDateTime getCurrentTime() throws TcpServerUnreachableException, DeviceException
  {
    List<List<String>> msgs = this.tcpClients.getCurrentTime()

    assert msgs.size() == 1
    assert msgs[0].size() == 3
    assert !(msgs[0][0]?.empty)
    assert msgs[0][1] == null
    assert msgs[0][2] == null


    return LocalDateTime.parse(msgs[0][0], DateTimeFormatter.ofPattern(MonitorJavaTemplate.monitor_time_formatter_pattern, MonitorJavaTemplate.monitor_time_formatter_locale))

  }

  @Override
  Boolean appProcessIsRunning(String appPackageName) throws DeviceException
  {
    log.debug("appProcessIsRunning($appPackageName)")
    String ps = this.adbWrapper.ps(this.serialNumber)

    boolean out = ps.contains(appPackageName)
    if (out)
      log.trace("App process of $appPackageName is running")
    else
      log.trace("App process of $appPackageName is not running")
    return out
  }

  @Override
  Boolean anyMonitorIsReachable() throws DeviceException
  {
    log.debug("anyMonitorIsReachable()")
    return this.tcpClients.anyMonitorIsReachable()
  }

  private void startUiaDaemon() throws DeviceException
  {
    log.debug("startUiaDaemon()")
    this.tcpClients.startUiaDaemon()
    log.trace("DONE startUiaDaemon()")
  }

  @Override
  void clearLogcat() throws DeviceException
  {
    log.debug("clearLogcat()")
    adbWrapper.clearLogcat(serialNumber)
  }

  @Override
  void installApk(IApk apk) throws DeviceException
  {
    log.debug("installApk($apk.fileName)")
    adbWrapper.installApk(serialNumber, apk)
  }

  @Override
  void uninstallApk(String apkPackageName, boolean warnAboutFailure) throws DeviceException
  {
    log.debug("uninstallApk($apkPackageName, warnAboutFailure: $warnAboutFailure)")
    adbWrapper.uninstallApk(serialNumber, apkPackageName, warnAboutFailure)
  }

  @Override
  Boolean3 launchMainActivity(String launchableActivityComponentName) throws DeviceException
  {
    log.debug("launchMainActivity($launchableActivityComponentName)")
    adbWrapper.launchMainActivity(serialNumber, launchableActivityComponentName)
    sleep(cfg.launchActivityDelay)
    return Boolean3.True
  }

  @Override
  void clearPackage(String apkPackageName) throws DeviceException
  {
    log.debug("clearPackage($apkPackageName)")
    adbWrapper.clearPackage(serialNumber, apkPackageName)
  }

  @Override
  void removeJar(File jar) throws DeviceException
  {
    log.debug("removeJar($jar)")
    adbWrapper.removeJar(serialNumber, cfg.uiautomatorDaemonJar)

  }

  private static boolean uiaDaemonHandlesCommand(DeviceCommand deviceCommand)
  {
    return deviceCommand.command in [
      DEVICE_COMMAND_PERFORM_ACTION,
      DEVICE_COMMAND_STOP_UIADAEMON,
      DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP,
      DEVICE_COMMAND_GET_IS_ORIENTATION_LANDSCAPE
    ]
  }


  @Override
  public String toString()
  {
    return "{device $serialNumber}"
  }

}
