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
import org.droidmate.android_sdk.DeviceException
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.android_sdk.IApk
import org.droidmate.android_sdk.NoAndroidDevicesAvailableException
import org.droidmate.apis.ITimeFormattedLogcatMessage
import org.droidmate.apis.TimeFormattedLogcatMessage
import org.droidmate.configuration.Configuration
import org.droidmate.device.datatypes.*
import org.droidmate.device.model.DeviceModel
import org.droidmate.device.model.IDeviceModel
import org.droidmate.errors.UnexpectedIfElseFallthroughError
import org.droidmate.logging.LogbackUtils
import org.droidmate.misc.Boolean3
import org.droidmate.misc.BuildConstants
import org.droidmate.misc.MonitorConstants
import org.droidmate.misc.Utils
import org.droidmate.uiautomator_daemon.DeviceCommand
import org.droidmate.uiautomator_daemon.DeviceResponse
import org.droidmate.uiautomator_daemon.UiautomatorWindowHierarchyDumpDeviceResponse

import java.awt.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.List

import static org.droidmate.device.datatypes.AndroidDeviceAction.newLaunchAppDeviceAction
import static org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.*

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
 class AndroidDevice implements IAndroidDevice
{

  private final String        serialNumber
  private final Configuration cfg
  private final IAdbWrapper   adbWrapper
  private final ITcpClients   tcpClients
  private       IDeviceModel  deviceModel

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
  void pushJar(Path jar, String targetFileName = null) throws DeviceException
  {
    log.debug("pushJar(${jar.toString()}, $targetFileName)")
    adbWrapper.pushJar(serialNumber, jar, targetFileName)
  }


  @Override
  boolean hasPackageInstalled(String packageName) throws DeviceException
  {
    log.debug("hasPackageInstalled($packageName)")
    return adbWrapper.listPackage(serialNumber, packageName).contains(packageName)
  }


  @Override
   IDeviceGuiSnapshot getGuiSnapshot() throws DeviceNeedsRebootException, DeviceException
  {
    log.debug("getGuiSnapshot()")

    def response = this.issueCommand(
      new DeviceCommand(DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP)) as UiautomatorWindowHierarchyDumpDeviceResponse

    def outSnapshot = new UiautomatorWindowDump(
      response.windowHierarchyDump,
      new Dimension(response.displayWidth, response.displayHeight),
      this.deviceModel.androidLauncherPackageName
    )

    log.debug("getGuiSnapshot(): $outSnapshot")
    return outSnapshot
  }

  @Override
  void perform(IAndroidDeviceAction action) throws DeviceNeedsRebootException, DeviceException
  {
    log.debug("perform($action)")
    //noinspection GroovyInArgumentCheck
    assert action?.class in [ClickGuiAction, AdbClearPackageAction, LaunchMainActivityDeviceAction]

    //noinspection GroovyAssignabilityCheck
    switch (action)
    {
      case ClickGuiAction:
        performGuiClick((action as ClickGuiAction))
        break
      case LaunchMainActivityDeviceAction:
        assert false : "call .launchMainActivity() directly instead"
        break
      case AdbClearPackageAction:
        assert false : "call .clearPackage() directly instead"
        break
      default:
        throw new UnexpectedIfElseFallthroughError()
    }
  }

  DeviceResponse performGuiClick(ClickGuiAction action) throws DeviceNeedsRebootException, DeviceException
  {
    return issueCommand(new DeviceCommand(DEVICE_COMMAND_PERFORM_ACTION, action.guiAction))
  }

  /**
   * <p>
   * Issues given {@code deviceCommand} to the A(V)D, obtains the device answer, checks for errors and return the
   * device response, unless there were errors along the way. If there were errors, it throws an exception.
   * </p><p>
   * The issued command can be potentially handled either by aut-addon or uiautomator-daemon. This method resolves
   * who should be the recipient and sends the command using {@link TcpClients#uiautomatorClient}.
   *
   * </p><p>
   * <i>This doc was last reviewed on 14 Sep '13.</i>
   * </p>
   */
  private DeviceResponse issueCommand(DeviceCommand deviceCommand) throws DeviceNeedsRebootException, DeviceException
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
  void closeConnection() throws DeviceNeedsRebootException, DeviceException
  {
    this.stopUiaDaemon(false)
  }

  @Override
  void stopUiaDaemon(boolean uiaDaemonThreadIsNull) throws DeviceNeedsRebootException, DeviceException
  {
    log.trace("stopUiaDaemon(uiaDaemonThreadIsNull:$uiaDaemonThreadIsNull)")
    this.issueCommand(new DeviceCommand(DEVICE_COMMAND_STOP_UIADAEMON))

    if (uiaDaemonThreadIsNull) 
      assert this.tcpClients.uiaDaemonThreadIsNull 
    else 
      this.tcpClients.waitForUiaDaemonToClose()

    assert Utils.retryOnFalse( {!this.uiaDaemonIsRunning()}, 3, 300)
    log.trace("DONE stopUiaDaemon()")

  }


  @Override
  boolean isAvailable() throws DeviceException
  {
//    log.trace("isAvailable(${this.serialNumber})")
    try
    {
      this.adbWrapper.androidDevicesDescriptors.any {it.deviceSerialNumber == this.serialNumber}
    } catch (NoAndroidDevicesAvailableException ignored)
    {
      return false
    }
  }

  @Override
  void reboot() throws DeviceException
  {
//    log.trace("reboot(${this.serialNumber})")
    this.adbWrapper.reboot(this.serialNumber)
  }

  @Override
  boolean uiaDaemonClientThreadIsAlive()
  {
    return this.tcpClients.uiaDaemonThreadIsAlive
  }

  @Override
  void setupConnection() throws DeviceException
  {
    log.trace("setupConnection($serialNumber) / this.clearLogcat()")
    this.clearLogcat()
    log.trace("setupConnection($serialNumber) / this.tcpClients.forwardPorts()")
    this.tcpClients.forwardPorts()
    log.trace("setupConnection($serialNumber) / this.tcpClients.startUiaDaemon()")
    startUiaDaemon()
    log.trace("setupConnection($serialNumber) / DONE")
  }

  @Override
  void startUiaDaemon()
  {
    assert !this.uiaDaemonIsRunning()
    this.tcpClients.startUiaDaemon()
  }

  @Override
  void removeLogcatLogFile() throws DeviceException
  {
    log.debug("removeLogcatLogFile()")
    if (cfg.androidApi == Configuration.api19)
      this.adbWrapper.removeFile_api19(this.serialNumber, logcatLogFileName)
    else if (cfg.androidApi == Configuration.api23)
      this.adbWrapper.removeFile_api23(this.serialNumber, logcatLogFileName, uia2Daemon_packageName)
    else throw new UnexpectedIfElseFallthroughError()


  }

  @Override
  void pullLogcatLogFile() throws DeviceException
  {
    log.debug("pullLogcatLogFile()")
    if (cfg.androidApi == Configuration.api19)
      this.adbWrapper.pullFile_api19(this.serialNumber, logcatLogFileName, LogbackUtils.getLogFilePath("logcat.txt"))
    else if (cfg.androidApi == Configuration.api23)
      this.adbWrapper.pullFile_api23(this.serialNumber, logcatLogFileName, LogbackUtils.getLogFilePath("logcat.txt"), uia2Daemon_packageName)
    else throw new UnexpectedIfElseFallthroughError()

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
  List<List<String>> readAndClearMonitorTcpMessages() throws DeviceNeedsRebootException, DeviceException
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
  LocalDateTime getCurrentTime() throws DeviceNeedsRebootException, DeviceException
  {
    List<List<String>> msgs = this.tcpClients.getCurrentTime()

    assert msgs.size() == 1
    assert msgs[0].size() == 3
    assert !(msgs[0][0]?.empty)
    assert msgs[0][1] == null
    assert msgs[0][2] == null


    return LocalDateTime.parse(msgs[0][0], DateTimeFormatter.ofPattern(MonitorConstants.monitor_time_formatter_pattern, MonitorConstants.monitor_time_formatter_locale))

  }

  private Boolean appProcessIsRunning(String appPackageName) throws DeviceException
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
  Boolean anyMonitorIsReachable() throws DeviceNeedsRebootException, DeviceException
  {
    log.debug("anyMonitorIsReachable()")
    return this.tcpClients.anyMonitorIsReachable()
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
  void uninstallApk(String apkPackageName, boolean ignoreFailure) throws DeviceException
  {
    log.debug("uninstallApk($apkPackageName, ignoreFailure: $ignoreFailure)")
    adbWrapper.uninstallApk(serialNumber, apkPackageName, ignoreFailure)
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
  void closeMonitorServers() throws DeviceException
  {
    log.debug("closeMonitorServers()")
    tcpClients.closeMonitorServers()
  }


  @Override
  void clearPackage(String apkPackageName) throws DeviceException
  {
    log.debug("clearPackage($apkPackageName)")
    adbWrapper.clearPackage(serialNumber, apkPackageName)
  }

  @Override
  void removeJar(Path jar) throws DeviceException
  {
    log.debug("removeJar($jar)")
    adbWrapper.removeJar(serialNumber, jar)
  }

  @Override
  void installApk(Path apk) throws DeviceException
  {
    log.debug("installApk($apk.fileName)")
    adbWrapper.installApk(serialNumber, apk)
  }

  @Override
  void takeScreenshot(IApk app, String suffix) throws DeviceException
  {
    log.debug("takeScreenshot($app, $suffix)")
    
    assert app != null
    assert !suffix?.empty
    
    Path targetFile = Paths.get("${cfg.droidmateOutputDir}/${cfg.screenshotsDir}/${app.fileNameWithoutExtension}_${suffix}.png")
    targetFile.mkdirs()
    assert !Files.exists(targetFile)
    String targetFileString = targetFile.toString().replace(File.separator, "/")
    
    adbWrapper.takeScreenshot(serialNumber, targetFileString)
  }
  
  private static boolean uiaDaemonHandlesCommand(DeviceCommand deviceCommand)
  {
    return deviceCommand.command in [
      DEVICE_COMMAND_PERFORM_ACTION,
      DEVICE_COMMAND_STOP_UIADAEMON,
      DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP,
      DEVICE_COMMAND_GET_IS_ORIENTATION_LANDSCAPE,
      DEVICE_COMMAND_GET_DEVICE_MODEL
    ]
  }

  @Override
  Boolean appIsRunning(String appPackageName) throws DeviceNeedsRebootException, DeviceException
  {
    return this.anyMonitorIsReachable() && this.appProcessIsRunning(appPackageName)
  }

  @Override
  void clickAppIcon(String iconLabel) throws DeviceNeedsRebootException, DeviceException
  {
    this.perform(newLaunchAppDeviceAction(iconLabel))
  }

  @Override
  void initModel() throws DeviceException
  {
    log.trace("initModel(): this.issueCommand(new DeviceCommand(DEVICE_COMMAND_GET_DEVICE_MODEL))")
    DeviceResponse response = this.issueCommand(new DeviceCommand(DEVICE_COMMAND_GET_DEVICE_MODEL))
    assert response.model != null

    this.deviceModel = DeviceModel.build(response.model)
    assert this.deviceModel != null
  }

  @Override
  void reinstallUiautomatorDaemon() throws DeviceException
  {
    if (cfg.androidApi == Configuration.api19)
    {
      this.pushJar(this.cfg.uiautomatorDaemonJar)
    }
    else if (cfg.androidApi == Configuration.api23)
    {
      // Uninstall packages in case previous DroidMate run had some leftovers in the form of a living uia-daemon.
      // Commented out as seems to be superfluous with .installApk() as it reinstalls apps.
//      this.executeAdbCommand("uninstall $uia2Daemon_packageName", "Success")
//      this.executeAdbCommand("uninstall ${uia2Daemon_packageName}.test", "Success")

      this.installApk(this.cfg.uiautomator2DaemonApk)
      this.installApk(this.cfg.uiautomator2DaemonTestApk)

    } else throw new UnexpectedIfElseFallthroughError()

  }

  @Override
  void pushMonitorJar() throws DeviceException
  {
    if (cfg.androidApi == Configuration.api19)
    {
      this.pushJar(this.cfg.monitorApkApi19, BuildConstants.monitor_on_avd_apk_name)
    }
    else if (cfg.androidApi == Configuration.api23)
    {
      this.pushJar(this.cfg.monitorApkApi23, BuildConstants.monitor_on_avd_apk_name)

    } else throw new UnexpectedIfElseFallthroughError()

  }
  
  @Override 
  void reconnectAdb() throws DeviceException
  {
    this.executeAdbCommand("reconnect", "done")
  }
  
  @Override
  void executeAdbCommand(String command, String successfulOutput) throws DeviceException
  {
    this.adbWrapper.executeCommand(this.serialNumber, command, successfulOutput)
  }

  @Override
  boolean uiaDaemonIsRunning()
  {
    String packageName
    if (cfg.androidApi == Configuration.api19)
      packageName = uiaDaemon_packageName
    else if (cfg.androidApi == Configuration.api23)
      packageName = uia2Daemon_packageName
    else throw new UnexpectedIfElseFallthroughError()
    
    String processList = this.adbWrapper.executeCommand(this.serialNumber, "shell ps $packageName", "USER")
    return processList.contains(packageName)
  }

  @Override
  String toString()
  {
    return "{device $serialNumber}"
  }

}
