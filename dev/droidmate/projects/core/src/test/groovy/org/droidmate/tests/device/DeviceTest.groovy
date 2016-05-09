// Copyright (c) 2012-2016 Saarland University
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
import org.droidmate.common.BuildConstants
import org.droidmate.configuration.Configuration
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.RequiresDevice
import org.droidmate.test_suite_categories.RequiresDeviceSlow
import org.droidmate.tools.ApksProvider
import org.droidmate.tools.DeviceTools
import org.droidmate.tools.IDeviceTools
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import static org.droidmate.device.datatypes.AndroidDeviceAction.*

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class DeviceTest extends DroidmateGroovyTestCase
{
  @Category([RequiresDeviceSlow])
  @Test
  void "reboots and restores connection"()
  {
    withApkDeployedOnDevice() {IRobustDevice device, IApk deployedApk ->

      device.getGuiSnapshot()
      device.reboot()
      device.getGuiSnapshot()
    }
  }

  @Category([RequiresDevice])
  @Test
  void "Launches app, then checks, clicks, stops and checks it again"()
  {
    withApkDeployedOnDevice() {IRobustDevice device, IApk deployedApk ->

      device.perform(newLaunchActivityDeviceAction(deployedApk.launchableActivityComponentName))
      assert device.guiSnapshot.guiState.belongsToApp(deployedApk.packageName)

      // Act 1
      assert device.appIsRunning(deployedApk.packageName)

      // Act 2
      assert device.anyMonitorIsReachable()

      // Act 3
      device.perform(newClickGuiDeviceAction(100, 100))
      assert device.guiSnapshot.guiState.belongsToApp(deployedApk.packageName)

      // Act 4
      device.clearPackage(deployedApk.packageName)
      assert device.guiSnapshot.guiState.isHomeScreen()

      // Act 5
      assert !device.appIsRunning(deployedApk.packageName)

      // Act 6
      assert !device.anyMonitorIsReachable()

    }
  }

  @Category([RequiresDevice])
  @Test
  void "Turns wifi on"()
  {
    IDeviceTools deviceTools = new DeviceTools(new ConfigurationForTests().forDevice().get())
    deviceTools.deviceDeployer.withSetupDevice(0) {IRobustDevice device ->
      device.perform(newTurnWifiOnDeviceAction())
      return []
    }
  }

  private void withApkDeployedOnDevice(Closure computation)
  {
    Configuration cfg = new ConfigurationForTests().forDevice().setArgs([
      Configuration.pn_apksNames, "[$BuildConstants.monitored_inlined_apk_fixture_api19_name]" as String]
    ).get()

    IDeviceTools deviceTools = new DeviceTools(cfg)

    ApksProvider apksProvider = new ApksProvider(deviceTools.aapt)
    Apk apk = apksProvider.getApks(cfg.apksDirPath, cfg.apksLimit, cfg.apksNames).first()

    List<ExplorationException> exceptions =
      deviceTools.deviceDeployer.withSetupDevice(0) {IRobustDevice device ->
        deviceTools.apkDeployer.withDeployedApk(device, apk, computation.curry(device))
      }

    exceptions.every {
      it.printStackTrace()
    }

    assert exceptions.empty
  }
}
