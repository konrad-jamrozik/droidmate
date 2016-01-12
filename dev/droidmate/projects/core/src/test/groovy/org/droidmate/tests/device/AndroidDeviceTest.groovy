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
  // KJA current work
  @Category([RequiresDevice])
  @Test
  void "reboots"()
  {
    withApkDeployedOnDevice() {IAndroidDevice device, IApk deployedApk ->

      device.clearLogcat()
      device.guiSnapshot
      device.reboot()


//      System.out.println("Please replug now...");
//      sleep(3000)
//      System.out.println("2...");
//      sleep(1000)
//      System.out.println("1...");
//      sleep(1000)
//      System.out.println("Execution continues");

      device.guiSnapshot
    }
  }


  @Category([RequiresDevice])
  @Test
  void "Launches app, then checks, clicks, stops and checks it again"()
  {
    withApkDeployedOnDevice() {IAndroidDevice device, IApk deployedApk ->

      device.perform(newLaunchActivityDeviceAction(deployedApk.launchableActivityComponentName))
      assert device.guiSnapshot.guiState.belongsToApp(deployedApk.packageName)

      // Act 1
      assert device.appProcessIsRunning(deployedApk.packageName)

      // Act 2
      assert device.anyMonitorIsReachable()

      // Act 3
      device.perform(newClickGuiDeviceAction(100, 100))
      assert device.guiSnapshot.guiState.belongsToApp(deployedApk.packageName)

      // Act 4
      device.clearPackage(deployedApk.packageName)
      assert device.guiSnapshot.guiState.isHomeScreen()

      // Act 5
      assert !device.appProcessIsRunning(deployedApk.packageName)

      // Act 6
      assert !device.anyMonitorIsReachable()

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
}
