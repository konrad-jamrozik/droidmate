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

package org.droidmate.tests.device

import groovy.transform.TypeChecked
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.ExplorationException
import org.droidmate.android_sdk.IApk
import org.droidmate.configuration.Configuration
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.misc.BuildConstants
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.RequiresDevice
import org.droidmate.test_suite_categories.RequiresDeviceSlow
import org.droidmate.tests.DroidmateGroovyTestCase
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
  void "Obtains GUI snapshot for manual inspection"()
  {
    IDeviceTools deviceTools = new DeviceTools(
      new ConfigurationForTests()
        .setArgs([Configuration.pn_androidApi, Configuration.api23,])
        .forDevice()
        .get()
    )
    deviceTools.deviceDeployer.withSetupDevice(0) {IRobustDevice device ->
      println device.guiSnapshot.windowHierarchyDump
      return []
    }
  }

  @Category([RequiresDevice])
  @Test
  void "Turns wifi on on api23"()
  {
    IDeviceTools deviceTools = new DeviceTools(new ConfigurationForTests().setArgs([Configuration.pn_androidApi, Configuration.api23,]).forDevice().get())
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
