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
import org.droidmate.android_sdk.ApkExplorationException
import org.droidmate.android_sdk.FirstRealDeviceSerialNumber
import org.droidmate.android_sdk.IApk
import org.droidmate.configuration.Configuration
import org.droidmate.device.IAndroidDevice
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.exploration.device.RobustDevice
import org.droidmate.misc.BuildConstants
import org.droidmate.test_suite_categories.RequiresDevice
import org.droidmate.test_suite_categories.RequiresDeviceSlow
import org.droidmate.test_tools.DroidmateGroovyTestCase
import org.droidmate.test_tools.configuration.ConfigurationForTests
import org.droidmate.tools.ApksProvider
import org.droidmate.tools.DeviceTools
import org.droidmate.tools.IDeviceTools
import org.droidmate.uiautomator_daemon.DeviceCommand
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import static org.droidmate.device.datatypes.AndroidDeviceAction.newClickGuiDeviceAction
import static org.droidmate.device.datatypes.AndroidDeviceAction.newTurnWifiOnDeviceAction
import static org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.DEVICE_COMMAND_GET_DEVICE_MODEL

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
      device.rebootAndRestoreConnection()
      device.getGuiSnapshot()
    }
  }

  /**
   * This test exists for interactive debugging of known, not yet resolved bug. The behavior is as follows.
   * 
   * - If everything works fine and the uiadaemon server is alive, this test should succeed without any need to reinstall uiad apks
   * and setup connection. You can check if the server is allive as follows:
   * 
   * adb shell
   * shell@flo:/ $ ps | grep uia
   * u0_a1027  31550 205   869064 38120 sys_epoll_ 00000000 S org.droidmate.uiautomator2daemon.UiAutomator2Daemon
   *
   * - If the server was somehow corrupted, rerunning this test will hang on the "new ObjectInputStream", even if the installApk
   * and setupConnection methods are run. However, if the uninstall commands are run, then the test will succeed again without
   * problems. Not sure which uninstall is the important one, but I guess the one uninstalling.test
   * 
   * Symptom observations: sometimes, even though server on the device says he is waiting to accept a socket, actually getting 
   * a connected socket to it on client side does nothing. This means the server cannot be even stopped by socket, it has to be 
   * killed by reinstalling the package.
   */
  @Category([RequiresDevice])
  @Test
  void "Restarts uiautomatorDaemon2 and communicates with it via TCP"()
  {
    def cfg = configurationApi23
    IDeviceTools deviceTools = new DeviceTools(cfg)
    IAndroidDevice device = new RobustDevice(
      deviceTools.deviceFactory.create(new FirstRealDeviceSerialNumber(deviceTools.adb).toString()), cfg)

    if (device.isPackageInstalled(UiautomatorDaemonConstants.uia2Daemon_packageName)) 
      println "uia-daemon2 is installed." 
    else
    {
      println 'uia-daemon2 is not installed: reinstallUiautomatorDaemon'
      device.reinstallUiautomatorDaemon()
    }

    println 'setupConnection'
    device.setupConnection()

    println 'Socket socket = new Socket("localhost", 59800)' 
    Socket socket = new Socket("localhost", 59800)

    println 'def inputStream = new ObjectInputStream(socket.inputStream)'
    def inputStream = new ObjectInputStream(socket.inputStream)

    println 'def outputStream = new ObjectOutputStream(socket.outputStream)'
    def outputStream = new ObjectOutputStream(socket.outputStream)

    println 'outputStream.writeObject(new DeviceCommand(DEVICE_COMMAND_GET_DEVICE_MODEL))'
    outputStream.writeObject(new DeviceCommand(DEVICE_COMMAND_GET_DEVICE_MODEL))

    println 'outputStream.flush()'
    outputStream.flush()

    println 'inputStream.readObject()'
    inputStream.readObject()

    println 'socket.close()'
    socket.close()

//    println 'stop uiad'
//    device.stopUiaDaemon(false)

    println "END"
  }

  @Category([RequiresDevice])
  @Test
  void "Print widgets of current GUI screen"()
  {
    withSetupDevice(configurationApi23) {Configuration cfg, IDeviceTools deviceTools, IRobustDevice device ->
      
      def gs = device.guiSnapshot.guiState
      println "widgets (#${gs.widgets.size()}):"
      gs.widgets.each {println it}

      println "actionable widgets (#${gs.actionableWidgets.size()}):"
      gs.actionableWidgets.each {println it}
    }
  }

  @Category([RequiresDevice])
  @Test
  void "Launches app, then checks, clicks, stops and checks it again"()
  {
    withApkDeployedOnDevice() {IRobustDevice device, IApk deployedApk ->

      device.launchMainActivity(deployedApk.launchableActivityComponentName)
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
      assert !device.appProcessIsRunning(deployedApk.packageName)

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
  void "Sets up API23 compatible device and turns wifi on"()
  {
    
    IDeviceTools deviceTools = new DeviceTools(configurationApi23)
    deviceTools.deviceDeployer.withSetupDevice(0) {IRobustDevice device ->
      device.perform(newTurnWifiOnDeviceAction())
      return []
    }
  }

  private static Configuration getConfigurationApi23()
  {
    new ConfigurationForTests()
      .setArgs([Configuration.pn_androidApi, Configuration.api23])
      .forDevice()
      .get()
  }


  private void withApkDeployedOnDevice(Closure computation)
  {
    List<ApkExplorationException> exceptions = []
      withSetupDevice(configurationApi23monitoredInlinedApk) {Configuration cfg, IDeviceTools deviceTools, IRobustDevice device ->
        ApksProvider apksProvider = new ApksProvider(deviceTools.aapt)
        Apk apk = apksProvider.getApks(cfg.apksDirPath, cfg.apksLimit, cfg.apksNames, cfg.shuffleApks).first()
        exceptions = deviceTools.apkDeployer.withDeployedApk(device, apk, computation.curry(device))
      }

    exceptions.every { it.printStackTrace() }

    assert exceptions.empty
  }


  private static Configuration getConfigurationApi23monitoredInlinedApk()
  {
    new ConfigurationForTests()
      .setArgs([
      Configuration.pn_androidApi, Configuration.api23,
      Configuration.pn_apksNames, "[$BuildConstants.monitored_inlined_apk_fixture_api23_name]" as String,
    ])
      .forDevice()
      .get()
  }

  private void withSetupDevice(Configuration cfg, Closure computation)
  {
    IDeviceTools deviceTools = new DeviceTools(cfg)
    deviceTools.deviceDeployer.withSetupDevice(0) {IRobustDevice device -> computation(cfg, deviceTools, device)}

  }
}
