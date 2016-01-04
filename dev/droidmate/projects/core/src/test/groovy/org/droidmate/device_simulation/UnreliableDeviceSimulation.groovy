// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.datatypes.*
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.misc.ITimeGenerator

class UnreliableDeviceSimulation implements IDeviceSimulation
{

  @Delegate
  IDeviceSimulation simulation

  IUnreliableDeviceGuiSnapshotProvider unreliableGuiSnapshotProvider

  UnreliableDeviceSimulation(ITimeGenerator timeGenerator, String packageName, String specString)
  {
    this.simulation = new DeviceSimulation(timeGenerator, packageName, specString)
    this.unreliableGuiSnapshotProvider = new UnreliableDeviceGuiSnapshotProvider(this.simulation.currentGuiSnapshot)
  }

  @Override
  void updateState(IAndroidDeviceAction action)
  {
    // WISH later on support for failing calls to AndroidDevice.clearPackage would be nice. Currently,
    // org.droidmate.device_simulation.UnreliableDeviceSimulation.transitionClickGuiActionOnInvalidOrAppHasStoppedDialogBoxSnapshot(org.droidmate.device.datatypes.IAndroidDeviceAction)
    // just updates state of the underlying simulation and that's it.

    if (this.unreliableGuiSnapshotProvider.getCurrentWithoutChange().validationResult.valid
      && !(this.unreliableGuiSnapshotProvider.getCurrentWithoutChange().guiState.isAppHasStoppedDialogBox())
    )
    {
      this.simulation.updateState(action)
      this.unreliableGuiSnapshotProvider = new UnreliableDeviceGuiSnapshotProvider(this.simulation.currentGuiSnapshot)
    } else
    {
      transitionClickGuiActionOnInvalidOrAppHasStoppedDialogBoxSnapshot(action)
    }
  }


  @Override
  IDeviceGuiSnapshot getCurrentGuiSnapshot()
  {
    return this.unreliableGuiSnapshotProvider.provide()
  }

  private void transitionClickGuiActionOnInvalidOrAppHasStoppedDialogBoxSnapshot(IAndroidDeviceAction action)
  {
    switch (action.class)
    {
      case LaunchMainActivityDeviceAction:
        failWithForbiddenActionOnInvalidGuiSnapshot(action)
        break

      case AdbClearPackageAction:
        this.simulation.updateState(action)
        break

      case ClickGuiAction:
        transitionClickGuiActionOnInvalidOrAppHasStoppedDialogBoxSnapshot(action as ClickGuiAction)
        break

      default:
        throw new UnexpectedIfElseFallthroughError()
    }
  }

  private void failWithForbiddenActionOnInvalidGuiSnapshot(IAndroidDeviceAction action)
  {
    assert false: "DroidMate attempted to perform a device action that is forbidden while the device displays " +
      "invalid GUI snapshot or GUI snapshot with 'app has stopped' dialog box. The action: $action"
  }

  private void transitionClickGuiActionOnInvalidOrAppHasStoppedDialogBoxSnapshot(ClickGuiAction action)
  {
    if (this.unreliableGuiSnapshotProvider.getCurrentWithoutChange().guiState.appHasStoppedDialogBox)
    {
      AppHasStoppedDialogBoxGuiState appHasStopped = this.unreliableGuiSnapshotProvider.getCurrentWithoutChange().guiState as AppHasStoppedDialogBoxGuiState
      assert action.getSingleMatchingWidget(appHasStopped.actionableWidgets as Set<Widget>) == appHasStopped.OKWidget:
        "DroidMate attempted to click on 'app has stopped' dialog box on a widget different than 'OK'. The action: $action"

      this.unreliableGuiSnapshotProvider.pressOkOnAppHasStopped()

    } else
    {
      assert false: "DroidMate attempted to perform a click while the device displays an invalid GUI snapshot that is " +
        "not 'app has stopped' dialog box. The forbidden action: $action"
    }
  }
}
