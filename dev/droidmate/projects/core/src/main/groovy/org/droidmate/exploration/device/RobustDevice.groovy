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
import org.droidmate.common.Utils
import org.droidmate.device.IAndroidDevice
import org.droidmate.device.datatypes.AndroidDeviceAction
import org.droidmate.device.datatypes.AppHasStoppedDialogBoxGuiState
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.ValidationResult
import org.droidmate.exceptions.DeviceException

import static org.droidmate.device.datatypes.AndroidDeviceAction.newPressHomeDeviceAction

@Slf4j
class RobustDevice implements IDeviceWithReadableLogs
{

  @Delegate
  private final IAndroidDevice device

  @Delegate
  private final IDeviceMessagesReader messagesReader

  private final int clearPackageRetryAttempts
  private final int clearPackageRetryDelay

  private final int getValidGuiSnapshotRetryAttempts
  private final int getValidGuiSnapshotRetryDelay

  RobustDevice(IAndroidDevice device,
               int monitorServerStartTimeout,
               int monitorServerStartQueryInterval,
               int clearPackageRetryAttempts,
               int clearPackageRetryDelay,
               int getValidGuiSnapshotRetryAttempts,
               int getValidGuiSnapshotRetryDelay)
  {
    this.device = device
    this.messagesReader = new DeviceMessagesReader(device, monitorServerStartTimeout, monitorServerStartQueryInterval)

    this.clearPackageRetryAttempts = clearPackageRetryAttempts
    this.clearPackageRetryDelay = clearPackageRetryDelay

    this.getValidGuiSnapshotRetryAttempts = getValidGuiSnapshotRetryAttempts
    this.getValidGuiSnapshotRetryDelay = getValidGuiSnapshotRetryDelay

    assert clearPackageRetryAttempts >= 1
    assert clearPackageRetryDelay >= 0
  }

  @Override
  public IDeviceGuiSnapshot getGuiSnapshot() throws DeviceException
  {
    return this.getExplorableGuiSnapshot()
  }

  public IDeviceGuiSnapshot getExplorableGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot guiSnapshot = this.getRetryValidGuiSnapshot()
    guiSnapshot = closeANRIfNecessary(guiSnapshot)
    return guiSnapshot
  }

  private IDeviceGuiSnapshot closeANRIfNecessary(IDeviceGuiSnapshot guiSnapshot) throws DeviceException
  {
    def out = guiSnapshot
    if (guiSnapshot.guiState.isAppHasStoppedDialogBox())
    {
      assert (guiSnapshot.guiState as AppHasStoppedDialogBoxGuiState).OKWidget.enabled
      device.perform(AndroidDeviceAction.newClickGuiDeviceAction(
        (guiSnapshot.guiState as AppHasStoppedDialogBoxGuiState).OKWidget)
      )
      out = this.getRetryValidGuiSnapshot()
    }

    if (out.guiState.isAppHasStoppedDialogBox())
    {
      assert (out.guiState as AppHasStoppedDialogBoxGuiState).OKWidget.enabled
      throw new DeviceException("Failed to properly close ANR even though OK widget is enabled.")
    }

    return out
  }

  public IDeviceGuiSnapshot getRetryValidGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot guiSnapshot = Utils.retryOnException(this.&getValidGuiSnapshot, DeviceException,
      getValidGuiSnapshotRetryAttempts,
      getValidGuiSnapshotRetryDelay
    )

    assert guiSnapshot.validationResult.valid
    return guiSnapshot
  }

  @Override
  Boolean clearPackage(String apkPackageName) throws DeviceException
  {
    Utils.retryOnException(device.&clearPackage.curry(apkPackageName), DeviceException,
      this.clearPackageRetryAttempts,
      this.clearPackageRetryDelay
    )
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

  IDeviceGuiSnapshot getValidGuiSnapshot() throws DeviceException
  {
    IDeviceGuiSnapshot snapshot = device.getGuiSnapshot()
    ValidationResult vres = snapshot.validationResult

    if (!vres.valid)
      throw new DeviceException("Failed to obtain valid GUI snapshot. Validation (failed) result: ${vres.description}")

    return snapshot
  }

  @Override
  String toString() {
    return "robust-"+this.device.toString()
  }
}
