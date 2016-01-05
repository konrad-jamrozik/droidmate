// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tests.device

import groovy.transform.TypeChecked
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.ExplorationException
import org.droidmate.android_sdk.IApk
import org.droidmate.configuration.Configuration
import org.droidmate.device.IAndroidDevice
import org.droidmate.init.InitConstants
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.RequiresDevice
import org.droidmate.test_suite_categories.UnderConstruction
import org.droidmate.tools.ApksProvider
import org.droidmate.tools.DeviceTools
import org.droidmate.tools.IDeviceTools
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import static org.droidmate.device.datatypes.AndroidDeviceAction.newClickGuiDeviceAction
import static org.droidmate.device.datatypes.AndroidDeviceAction.newLaunchActivityDeviceAction

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class AndroidDeviceTest extends DroidmateGroovyTestCase
{

  @Category([RequiresDevice, UnderConstruction])
  @Test
  void "Correctly checks if app is running"()
  {
    // KJA 2 instead, do checks as described in #1012
    withApkDeployedOnDevice() {IAndroidDevice device, IApk deployedApk ->
      assert device.appProcessIsRunning(deployedApk)
      assert device.appMonitorIsReachable(deployedApk)
    }
  }

  @Category(RequiresDevice)
  @Test
  void "Performs 'launch main activity', 'click widget' and 'reset package' actions on the device"()
  {
    withApkDeployedOnDevice() {IAndroidDevice device, IApk apk ->
      performLaunchClickClear(device, apk)
    }
  }

  private void withApkDeployedOnDevice(Closure computation)
  {
    Configuration cfg = new ConfigurationForTests().forDevice().setArgs([
      Configuration.pn_apksNames, "[$InitConstants.monitored_inlined_apk_fixture_name]" as String]
    ).get()
    IDeviceTools deviceTools = new DeviceTools(cfg)

    ApksProvider apksProvider = new ApksProvider(deviceTools.aapt)
    Apk apk = apksProvider.getApks(cfg.apksDirPath, cfg.apksLimit, cfg.apksNames).first()

    List<ExplorationException> exceptions =
      deviceTools.deviceDeployer.withSetupDevice(0) {IAndroidDevice device ->
        deviceTools.apkDeployer.withDeployedApk(device, apk, computation.curry(device))
      }

    exceptions.every {
      it.printStackTrace()
    }
    assert exceptions.empty
  }

  private void performLaunchClickClear(IAndroidDevice device, IApk deployedApk)
  {
    // Act 1
    device.perform(newLaunchActivityDeviceAction(deployedApk.launchableActivityComponentName))
    assert device.guiSnapshot.guiState.belongsToApp(deployedApk.packageName)

    // Act 2
    device.perform(newClickGuiDeviceAction(100, 100))
    assert device.guiSnapshot.guiState.belongsToApp(deployedApk.packageName)

    // Act 3
    device.clearPackage(deployedApk.packageName)
    assert device.guiSnapshot.guiState.isHomeScreen()
  }
}
