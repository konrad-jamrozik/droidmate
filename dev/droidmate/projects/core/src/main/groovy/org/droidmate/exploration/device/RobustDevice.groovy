// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.common.Boolean3
import org.droidmate.common.Utils
import org.droidmate.device.IAndroidDevice
import org.droidmate.device.datatypes.AndroidDeviceAction
import org.droidmate.device.datatypes.AppHasStoppedDialogBoxGuiState
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.ValidationResult
import org.droidmate.exceptions.DeviceException

import static org.droidmate.device.datatypes.AndroidDeviceAction.newPressHomeDeviceAction

@Slf4j
class RobustDevice implements IRobustDevice
{

  @Delegate
  private final IAndroidDevice device

  @Delegate
  private final IDeviceMessagesReader messagesReader

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

  RobustDevice(IAndroidDevice device,
               int monitorServerStartTimeout,
               int monitorServerStartQueryDelay,
               int clearPackageRetryAttempts,
               int clearPackageRetryDelay,
               int getValidGuiSnapshotRetryAttempts,
               int getValidGuiSnapshotRetryDelay,
               int checkAppIsRunningRetryAttempts,
               int checkAppIsRunningRetryDelay,
               int stopAppRetryAttempts,
               int stopAppSuccessCheckDelay,
               int closeANRAttempts,
               int closeANRDelay)
  {
    this.device = device
    this.messagesReader = new DeviceMessagesReader(device, monitorServerStartTimeout, monitorServerStartQueryDelay)

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

    assert clearPackageRetryAttempts >= 1
    assert checkAppIsRunningRetryAttempts >= 1
    assert stopAppRetryAttempts >= 1
    assert closeANRAttempts >= 1

    assert clearPackageRetryDelay >= 0
    assert checkAppIsRunningRetryDelay >= 0
    assert stopAppSuccessCheckDelay >= 0
    assert closeANRDelay >= 0


  }

  @Override
  IDeviceGuiSnapshot getGuiSnapshot() throws DeviceException
  {
    return this.getExplorableGuiSnapshot()
  }

  @Override
  void clearPackage(String apkPackageName) throws DeviceException
  {
    // Clearing package has to happen more than once, because sometimes after cleaning suddenly the ActivityManager restarts
    // one of the activities of the app.
    Utils.retryOnFalse({

      Utils.retryOnException(device.&clearPackage.curry(apkPackageName), DeviceException,
        this.clearPackageRetryAttempts,
        this.clearPackageRetryDelay
      )

      // Sleep here to give the device some time to stop all the processes belonging to the cleared package before checking
      // if indeed all of them have been stopped.
      sleep(this.stopAppSuccessCheckDelay)

      return !this.appIsRunningCheckOnce(apkPackageName)

    },
      this.stopAppRetryAttempts,
      /* Retry delay. Zero, because after seeing the app didn't stop, we immediately clear package again. */
      0)
  }


  @Override
  IDeviceGuiSnapshot ensureHomeScreenIsDisplayed() throws DeviceException
  {
    def guiSnapshot = this.guiSnapshot
    if (!guiSnapshot.guiState.isHomeScreen())
    {
      device.perform(newPressHomeDeviceAction())
      guiSnapshot = this.guiSnapshot
      if (!guiSnapshot.guiState.isHomeScreen())
        throw new DeviceException("Failed to ensure home screen is displayed. " +
          "Pressing 'home' button didn't help. Instead, ended with GUI state of: ${guiSnapshot.guiState}")

    }
    return guiSnapshot
  }

  @Override
  Boolean appIsRunningCheckOnce(String appPackageName) throws DeviceException
  {
    return _appIsRunning(appPackageName)
  }

  @Override
  Boolean appIsRunning(IApk apk) throws DeviceException
  {
    return Utils.retryOnFalse(this.&_appIsRunning.curry(apk.packageName),
      checkAppIsRunningRetryAttempts,
      checkAppIsRunningRetryDelay,
    )
  }

  @Override
  Boolean appIsNotRunning(IApk apk) throws DeviceException
  {
    return Utils.retryOnFalse({!this._appIsRunning(apk.packageName)},
      checkAppIsRunningRetryAttempts,
      checkAppIsRunningRetryDelay,
    )
  }

  @Override
  Boolean3 launchMainActivity(String launchableActivityComponentName) throws DeviceException
  {
    try
    {
      Boolean3 result = this.device.launchMainActivity(launchableActivityComponentName)
      def guiSnapshot = this.getExplorableGuiSnapshotWithoutClosingANR()

      if ((result == Boolean3.True) && guiSnapshot.guiState.appHasStoppedDialogBox)
      {
        log.debug("device.launchMainActivity() succeeded, but ANR is displayed. Returning 'unknown' " +
          "(launch might be successful or not).")
        result = Boolean3.Unknown
      }

      return result

    } catch (DeviceException e)
    {
      log.debug("device.launchMainActivity() threw $e. Returning false (launch failure) without rethrowing.")
      return Boolean3.False
    }
  }

  private IDeviceGuiSnapshot getExplorableGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot guiSnapshot = this.getRetryValidGuiSnapshot()
    guiSnapshot = closeANRIfNecessary(guiSnapshot)
    return guiSnapshot
  }

  private IDeviceGuiSnapshot getExplorableGuiSnapshotWithoutClosingANR() throws DeviceException
  {
    return this.getRetryValidGuiSnapshot()
  }

  private IDeviceGuiSnapshot closeANRIfNecessary(IDeviceGuiSnapshot guiSnapshot) throws DeviceException
  {
    assert guiSnapshot.validationResult.valid
    if (!guiSnapshot.guiState.isAppHasStoppedDialogBox())
      return guiSnapshot

    assert guiSnapshot.guiState.isAppHasStoppedDialogBox()
    assert (guiSnapshot.guiState as AppHasStoppedDialogBoxGuiState).OKWidget.enabled

    IDeviceGuiSnapshot out = null

    Utils.retryOnFalse({

      device.perform(AndroidDeviceAction.newClickGuiDeviceAction(
        (guiSnapshot.guiState as AppHasStoppedDialogBoxGuiState).OKWidget)
      )
      out = this.getRetryValidGuiSnapshot()

      if (out.guiState.isAppHasStoppedDialogBox())
      {
        assert (out.guiState as AppHasStoppedDialogBoxGuiState).OKWidget.enabled
        log.trace("Failed to properly close ANR even though OK widget is enabled.")
        return false
      } else
        return true

    },
      this.closeANRAttempts,
      this.closeANRDelay)

    assert out.validationResult.valid
    return out
  }

  private IDeviceGuiSnapshot getRetryValidGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot guiSnapshot = Utils.retryOnException(
      this.&getValidGuiSnapshot,
      DeviceException,
      getValidGuiSnapshotRetryAttempts,
      getValidGuiSnapshotRetryDelay
    )

    assert guiSnapshot.validationResult.valid
    return guiSnapshot
  }

  private IDeviceGuiSnapshot getValidGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot snapshot = device.getGuiSnapshot()
    ValidationResult vres = snapshot.validationResult

    if (!vres.valid)
      throw new DeviceException("Failed to obtain valid GUI snapshot. Validation (failed) result: ${vres.description}")

    return snapshot
  }

  @Override
  void reboot()
  {
    this.device.reboot()
    sleep(60*1000);
    Utils.retryOnFalse( {
      def out = this.device.available
      if (!out)
        log.trace("Device not yet available after rebooting, waiting ten seconds and retrying")
      return out
    }, 3, 10000) // KJA
  }
  private Boolean _appIsRunning(String appPackageName)
  {
    return device.anyMonitorIsReachable() && device.appProcessIsRunning(appPackageName)
  }

  @Override
  String toString()
  {
    return "robust-" + this.device.toString()
  }
}
