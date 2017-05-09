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
import org.droidmate.android_sdk.AdbWrapperException
import org.droidmate.android_sdk.DeviceException
import org.droidmate.android_sdk.IApk
import org.droidmate.apis.IApiLogcatMessage
import org.droidmate.configuration.Configuration
import org.droidmate.device.AllDeviceAttemptsExhaustedException
import org.droidmate.device.IAndroidDevice
import org.droidmate.device.TcpServerUnreachableException
import org.droidmate.device.datatypes.*
import org.droidmate.logging.Markers
import org.droidmate.misc.Utils

import static org.droidmate.device.datatypes.AndroidDeviceAction.newPressHomeDeviceAction

@Slf4j
class RobustDevice implements IRobustDevice
{

  @Delegate
  private final IAndroidDevice device

  @Delegate
  private final IDeviceMessagesReader messagesReader

  public static final int ensureHomeScreenIsDisplayedAttempts = 3

  private final int clearPackageRetryAttempts
  private final int clearPackageRetryDelay

  private final int getValidGuiSnapshotRetryAttempts
  private final int getValidGuiSnapshotRetryDelay

  private final int checkAppIsRunningRetryAttempts
  private final int checkAppIsRunningRetryDelay

  private final int stopAppRetryAttempts
  private final int stopAppSuccessCheckDelay

  private final int closeANRAttempts
  private final int closeANRDelay

  private final int checkDeviceAvailableAfterRebootAttempts
  private final int checkDeviceAvailableAfterRebootFirstDelay
  private final int checkDeviceAvailableAfterRebootLaterDelays

  private final int waitForCanRebootDelay

  RobustDevice(IAndroidDevice device, Configuration cfg)
  {
    this(device,
      cfg.clearPackageRetryAttempts,
      cfg.clearPackageRetryDelay,
      cfg.getValidGuiSnapshotRetryAttempts,
      cfg.getValidGuiSnapshotRetryDelay,
      cfg.checkAppIsRunningRetryAttempts,
      cfg.checkAppIsRunningRetryDelay,
      cfg.stopAppRetryAttempts,
      cfg.stopAppSuccessCheckDelay,
      cfg.closeANRAttempts,
      cfg.closeANRDelay,
      cfg.checkDeviceAvailableAfterRebootAttempts,
      cfg.checkDeviceAvailableAfterRebootFirstDelay,
      cfg.checkDeviceAvailableAfterRebootLaterDelays,
      cfg.waitForCanRebootDelay)
  }

  RobustDevice(IAndroidDevice device,
               int clearPackageRetryAttempts,
               int clearPackageRetryDelay,
               int getValidGuiSnapshotRetryAttempts,
               int getValidGuiSnapshotRetryDelay,
               int checkAppIsRunningRetryAttempts,
               int checkAppIsRunningRetryDelay,
               int stopAppRetryAttempts,
               int stopAppSuccessCheckDelay,
               int closeANRAttempts,
               int closeANRDelay,
               int checkDeviceAvailableAfterRebootAttempts,
               int checkDeviceAvailableAfterRebootFirstDelay,
               int checkDeviceAvailableAfterRebootLaterDelays,
               int waitForCanRebootDelay)
  {
    this.device = device
    this.messagesReader = new DeviceMessagesReader(device)

    this.clearPackageRetryAttempts = clearPackageRetryAttempts
    this.clearPackageRetryDelay = clearPackageRetryDelay

    this.getValidGuiSnapshotRetryAttempts = getValidGuiSnapshotRetryAttempts
    this.getValidGuiSnapshotRetryDelay = getValidGuiSnapshotRetryDelay

    this.checkAppIsRunningRetryAttempts = checkAppIsRunningRetryAttempts
    this.checkAppIsRunningRetryDelay = checkAppIsRunningRetryDelay

    this.stopAppRetryAttempts = stopAppRetryAttempts
    this.stopAppSuccessCheckDelay = stopAppSuccessCheckDelay

    this.closeANRAttempts = closeANRAttempts
    this.closeANRDelay = closeANRDelay

    this.checkDeviceAvailableAfterRebootAttempts = checkDeviceAvailableAfterRebootAttempts
    this.checkDeviceAvailableAfterRebootFirstDelay = checkDeviceAvailableAfterRebootFirstDelay
    this.checkDeviceAvailableAfterRebootLaterDelays = checkDeviceAvailableAfterRebootLaterDelays

    this.waitForCanRebootDelay = waitForCanRebootDelay

    assert clearPackageRetryAttempts >= 1
    assert checkAppIsRunningRetryAttempts >= 1
    assert stopAppRetryAttempts >= 1
    assert closeANRAttempts >= 1
    assert checkDeviceAvailableAfterRebootAttempts >= 1

    assert clearPackageRetryDelay >= 0
    assert checkAppIsRunningRetryDelay >= 0
    assert stopAppSuccessCheckDelay >= 0
    assert closeANRDelay >= 0
    assert checkDeviceAvailableAfterRebootFirstDelay >= 0
    assert checkDeviceAvailableAfterRebootLaterDelays >= 0
    assert waitForCanRebootDelay >= 0
  }

  @Override
  IDeviceGuiSnapshot getGuiSnapshot() throws DeviceException
  {
    return this.getExplorableGuiSnapshot()
  }

  @Override
  void uninstallApk(String apkPackageName, boolean ignoreFailure) throws DeviceException
  {
    if (ignoreFailure)
      device.uninstallApk(apkPackageName, ignoreFailure)
    else
    {
      try
      {
        device.uninstallApk(apkPackageName, ignoreFailure)
      } catch (DeviceException e)
      {
        boolean appIsInstalled
        try
        {
          appIsInstalled = device.hasPackageInstalled(apkPackageName)
        } catch (DeviceException e2)
        {
          throw new DeviceException("Uninstallation of $apkPackageName failed with exception E1: '$e'. " +
            "Tried to check if the app that was to be uninstalled is still installed, but that also resulted in exception, E2. " +
            "Discarding E1 and throwing an exception having as a cause E2", e2)
        }
        
        if (appIsInstalled)
          throw new DeviceException("Uninstallation of $apkPackageName threw an exception (given as cause of this exception) and the app is indeed still installed.", e)
        else
        {
          log.debug("Uninstallation of $apkPackageName threw na exception, but the app is no longer installed. Note: this situation has proven to make the uiautomator be unable to dump window hierarchy. Discarding the exception '$e', resetting connection to the device and continuing.")
          // Doing .rebootAndRestoreConnection() just hangs the emulator: http://stackoverflow.com/questions/9241667/how-to-reboot-emulator-to-test-action-boot-completed
          this.closeConnection()
          this.setupConnection()
        }
      }
    }
  }

  @Override
  void setupConnection() throws DeviceException
  {
    rebootIfNecessary("device.setupConnection()", true) { this.device.setupConnection() }
  }

  @Override
  void clearPackage(String apkPackageName) throws DeviceException
  {
    // Clearing package has to happen more than once, because sometimes after cleaning suddenly the ActivityManager restarts
    // one of the activities of the app.
    Utils.retryOnFalse({

      Utils.retryOnException(device.&clearPackage.curry(apkPackageName), DeviceException,
        this.clearPackageRetryAttempts,
        this.clearPackageRetryDelay, "clearPackage"
      )

      // Sleep here to give the device some time to stop all the processes belonging to the cleared package before checking
      // if indeed all of them have been stopped.
      sleep(this.stopAppSuccessCheckDelay)

      return !this.getAppIsRunningRebootingIfNecessary(apkPackageName)

    },
      this.stopAppRetryAttempts,
      /* Retry delay. Zero, because after seeing the app didn't stop, we immediately clear package again. */
      0)
  }


  @Override
  IDeviceGuiSnapshot ensureHomeScreenIsDisplayed() throws DeviceException
  {
    def guiSnapshot = this.guiSnapshot
    if (guiSnapshot.guiState.isHomeScreen())
      return guiSnapshot

    Utils.retryOnFalse({
      if (!guiSnapshot.guiState.isHomeScreen())
      {
        if (guiSnapshot.guiState.isSelectAHomeAppDialogBox())
        {
          guiSnapshot = closeSelectAHomeAppDialogBox(guiSnapshot)
        } else if (guiSnapshot.guiState.isUseLauncherAsHomeDialogBox()) 
        {
          guiSnapshot = closeUseLauncherAsHomeDialogBox(guiSnapshot)
        } else
        {
          device.perform(newPressHomeDeviceAction())
          guiSnapshot = this.guiSnapshot
        }
      }
      return guiSnapshot.guiState.isHomeScreen()
    }, ensureHomeScreenIsDisplayedAttempts, /* delay */ 0)

    if (!guiSnapshot.guiState.isHomeScreen())
    {
      throw new DeviceException("Failed to ensure home screen is displayed. " +
        "Pressing 'home' button didn't help. Instead, ended with GUI state of: ${guiSnapshot.guiState}.\n" +
        "Full window hierarchy dump:\n" +
        guiSnapshot.windowHierarchyDump)
    }

    return guiSnapshot
  }

  private IDeviceGuiSnapshot closeSelectAHomeAppDialogBox(IDeviceGuiSnapshot guiSnapshot)
  {
    device.perform(AndroidDeviceAction.newClickGuiDeviceAction(
      guiSnapshot.guiState.widgets.findSingle({it.text == "Launcher"}))
    )

    guiSnapshot = this.guiSnapshot
    if (guiSnapshot.guiState.isSelectAHomeAppDialogBox())
    {
      device.perform(AndroidDeviceAction.newClickGuiDeviceAction(
        guiSnapshot.guiState.widgets.findSingle({it.text == "Just once"}))
      )
      guiSnapshot = this.guiSnapshot
    }
    assert !guiSnapshot.guiState.isSelectAHomeAppDialogBox()
    return guiSnapshot
  }

  private IDeviceGuiSnapshot closeUseLauncherAsHomeDialogBox(IDeviceGuiSnapshot guiSnapshot)
  {
    device.perform(AndroidDeviceAction.newClickGuiDeviceAction(
      guiSnapshot.guiState.widgets.findSingle({it.text == "Just once"}))
    )

    guiSnapshot = this.guiSnapshot
    assert !guiSnapshot.guiState.isUseLauncherAsHomeDialogBox()
    return guiSnapshot
  }


  @Override
  void perform(IAndroidDeviceAction action) throws DeviceException
  {
    rebootIfNecessary("device.perform(action:$action)", false) {this.device.perform(action)}
  }

  @Override
  Boolean appIsNotRunning(IApk apk) throws DeviceException
  {
    return Utils.retryOnFalse({!this.getAppIsRunningRebootingIfNecessary(apk.packageName)},
      checkAppIsRunningRetryAttempts,
      checkAppIsRunningRetryDelay,
    )
  }

  private boolean getAppIsRunningRebootingIfNecessary(String packageName) throws DeviceException
  {
    return rebootIfNecessary("device.appIsRunning(packageName:$packageName)", true) {this.device.appIsRunning(packageName)}
  }

  @Override
  void launchApp(IApk app) throws DeviceException
  {
    log.debug("launchApp(${app.packageName})")

    if (app.launchableActivityName != null)
      this.launchMainActivity(app.launchableActivityComponentName)
    else
    {
      assert app.applicationLabel?.length() > 0
      this.clickAppIcon(app.applicationLabel)
    }
  }

  @Override
  void clickAppIcon(String iconLabel) throws DeviceException
  {
    rebootIfNecessary("device.clickAppIcon(iconLabel:$iconLabel)", true) { this.device.clickAppIcon(iconLabel) }
  }

  @Override
  void launchMainActivity(String launchableActivityComponentName) throws DeviceException
  {
    // KJA recognition if launch succeeded and checking if ANR is displayed should be also implemented for 
    // this.clickAppIcon(), which is called by caller of this method.
    
    boolean launchSucceeded = false
    try
    {
      // WISH when ANR immediately appears, waiting for full SysCmdExecutor.sysCmdExecuteTimeout to pass here is wasteful.
      this.device.launchMainActivity(launchableActivityComponentName)
      launchSucceeded = true
      
    } catch (AdbWrapperException e)
    {
      log.warn(Markers.appHealth, "! device.launchMainActivity($launchableActivityComponentName) threw $e " +
        "Discarding the exception, rebooting and continuing.")

      this.rebootAndRestoreConnection()
    }
    
    // KJA if launch succeeded, but uia-daemon broke, this command will reboot device, returning home screen, 
    // making exploration strategy terminate due to "home screen after reset". This happened on 
    // net.zedge.android_v4.10.2-inlined.apk
    // KJA think where else the bug above can also cause problems. I.e. getting home screen due to uia-d reset.
    def guiSnapshot = this.getExplorableGuiSnapshotWithoutClosingANR()

    // KJA this case happened once com.spotify.music_v1.4.0.631-inlined.apk, but I forgot to write down random seed. 
    // If this will happen more often, consider giving app second chance on restarting even after it crashes:
    // do not try to relaunch here; instead do it in exploration strategy. This way API logs from the failed launch will be 
    // separated.
    if (launchSucceeded && guiSnapshot.guiState.appHasStoppedDialogBox)
      log.debug(Markers.appHealth, "device.launchMainActivity($launchableActivityComponentName) succeeded, but ANR is displayed.")
  }

  private IDeviceGuiSnapshot getExplorableGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot guiSnapshot = this.getRetryValidGuiSnapshotRebootingIfNecessary()
    guiSnapshot = closeANRIfNecessary(guiSnapshot)
    return guiSnapshot
  }

  private IDeviceGuiSnapshot getExplorableGuiSnapshotWithoutClosingANR() throws DeviceException
  {
    return this.getRetryValidGuiSnapshotRebootingIfNecessary()
  }

  private IDeviceGuiSnapshot closeANRIfNecessary(IDeviceGuiSnapshot guiSnapshot) throws DeviceException
  {
    assert guiSnapshot.validationResult.valid
    if (!guiSnapshot.guiState.isAppHasStoppedDialogBox())
      return guiSnapshot

    assert guiSnapshot.guiState.isAppHasStoppedDialogBox()
    assert (guiSnapshot.guiState as AppHasStoppedDialogBoxGuiState).OKWidget.enabled
    log.debug("ANR encountered")

    IDeviceGuiSnapshot out = null

    Utils.retryOnFalse({

      device.perform(AndroidDeviceAction.newClickGuiDeviceAction(
        (guiSnapshot.guiState as AppHasStoppedDialogBoxGuiState).OKWidget)
      )
      out = this.getRetryValidGuiSnapshotRebootingIfNecessary()

      if (out.guiState.isAppHasStoppedDialogBox())
      {
        assert (out.guiState as AppHasStoppedDialogBoxGuiState).OKWidget.enabled
        log.debug("ANR encountered - again. Failed to properly close it even though its OK widget was enabled.")
        return false
      } else
        return true

    },
      this.closeANRAttempts,
      this.closeANRDelay)

    assert out.validationResult.valid
    return out
  }

  private IDeviceGuiSnapshot getRetryValidGuiSnapshotRebootingIfNecessary() throws DeviceException
  {
    return rebootIfNecessary("device.getRetryValidGuiSnapshot()", true) { this.getRetryValidGuiSnapshot() }
  }
  
  private IDeviceGuiSnapshot getRetryValidGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot guiSnapshot
    try
    {
      guiSnapshot = Utils.retryOnException(
        this.&getValidGuiSnapshot,
        DeviceException,
        getValidGuiSnapshotRetryAttempts,
        getValidGuiSnapshotRetryDelay,
        "getValidGuiSnapshot"
      )
    }
    catch (DeviceException e)
    {
      throw new AllDeviceAttemptsExhaustedException("All attempts at getting valid GUI snapshot failed", e)
    }

    assert guiSnapshot.validationResult.valid
    return guiSnapshot
  }

  private IDeviceGuiSnapshot getValidGuiSnapshot() throws DeviceException
  {
    // the rebootIfNecessary will reboot on TcpServerUnreachable
    IDeviceGuiSnapshot snapshot = rebootIfNecessary("device.getGuiSnapshot()", true) {this.device.getGuiSnapshot() }
    ValidationResult vres = snapshot.validationResult

    if (!vres.valid)
      throw new DeviceException("Failed to obtain valid GUI snapshot. Validation (failed) result: ${vres.description}")

    return snapshot
  }
  
  private <T> T rebootIfNecessary(String description, boolean makeSecondAttempt, Closure<T> operationOnDevice) throws DeviceException
  {
    T out
    try
    {
      out = operationOnDevice()
    } catch (TcpServerUnreachableException | AllDeviceAttemptsExhaustedException e)
    {
      log.warn(Markers.appHealth, "! Attempt to execute '$description' threw an exception: $e. " +
        (makeSecondAttempt
        ? "Reconnecting adb, rebooting the device and trying again."
        : "Reconnecting adb, rebooting the device and continuing."))

      this.reconnectAdbDiscardingException("Call to reconnectAdb() just before call to rebootAndRestoreConnection() " +
        "failed with: %s. Discarding the exception and continuing wih rebooting.")
      //this.reinstallUiautomatorDaemon()
      this.rebootAndRestoreConnection()

      if (makeSecondAttempt)
      {
        log.info("Reconnected adb and rebooted successfully. Making second and final attempt at executing '$description'")
        try
        {
          out = operationOnDevice()
          log.info("Second attempt at executing '$description' completed successfully.")
        } catch (TcpServerUnreachableException | AllDeviceAttemptsExhaustedException e2)
        {
          log.warn(Markers.appHealth, "! Second attempt to execute '$description' threw an exception: $e2. " +
            "Giving up and rethrowing.")
          throw e2
        }
      } else
      {
        out = null
      }
    }
    return out
  }
  
  void reconnectAdbDiscardingException(String exceptionMsg) throws DeviceException
  {
    try
    {
      device.reconnectAdb()
    } catch (DeviceException reconnectException)
    {
      log.debug(String.format(exceptionMsg, reconnectException))
    }
  }

  @Override
  void reboot() throws DeviceException
  {
    if (this.device.available)
    {
      log.trace("Device is available for rebooting.")
    } else
    {
      log.trace("Device not yet available for a reboot. Waiting $waitForCanRebootDelay milliseconds. If the device still won't be available, " +
        "assuming it cannot be reached at all.")

      sleep(this.waitForCanRebootDelay)

      if (this.device.available)
        log.trace("Device can be rebooted after the wait.")
      else
        throw new DeviceException("Device is not available for a reboot, even after the wait. Requesting to stop further apk explorations.", true)
    }

    log.trace("Rebooting.")
    this.device.reboot()

    sleep(this.checkDeviceAvailableAfterRebootFirstDelay)
    // WISH use "adb wait-for-device"
    boolean rebootResult = Utils.retryOnFalse({
      def out = this.device.available
      if (!out)
        log.trace("Device not yet available after rebooting, waiting $checkDeviceAvailableAfterRebootLaterDelays milliseconds and retrying")
      return out
    }, checkDeviceAvailableAfterRebootAttempts, checkDeviceAvailableAfterRebootLaterDelays)

    if (rebootResult)
    {
      assert this.device.available
      log.trace("Reboot completed successfully.")
    } else
    {
      assert !this.device.available
      throw new DeviceException("Device is not available after a reboot. Requesting to stop further apk explorations.", true)
    }

    assert !this.device.uiaDaemonClientThreadIsAlive()
  }
  
  @Override
  void rebootAndRestoreConnection() throws DeviceException
  {
    this.reboot()
    this.setupConnection()
    this.resetTimeSync()
  }


  @Override
  List<IApiLogcatMessage> getAndClearCurrentApiLogsFromMonitorTcpServer() throws DeviceException
  {
    return rebootIfNecessary("messagesReader.getAndClearCurrentApiLogsFromMonitorTcpServer()", true) {this.messagesReader.getAndClearCurrentApiLogsFromMonitorTcpServer()}
  }

  @Override
  void closeConnection() throws DeviceException
  {
    rebootIfNecessary("closeConnection()", true) {this.device.closeConnection()}
  }

  @Override
  String toString()
  {
    return "robust-" + this.device.toString()
  }

  @Override
  void initModel() throws DeviceException
  {
    rebootIfNecessary("initModel()", true) {this.device.initModel()}
  }
}
