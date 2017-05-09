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

package org.droidmate.tools

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.*
import org.droidmate.configuration.Configuration
import org.droidmate.device.IAndroidDevice
import org.droidmate.device.IDeployableAndroidDevice
import org.droidmate.errors.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.exploration.device.RobustDevice
import org.droidmate.logging.Markers
import org.droidmate.misc.Assert
import org.droidmate.misc.BuildConstants
import org.droidmate.misc.DroidmateException
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants

import java.nio.file.Paths

@Slf4j
 class AndroidDeviceDeployer implements IAndroidDeviceDeployer
{

  private final Configuration         cfg
  private final IAdbWrapper           adbWrapper
  private final IAndroidDeviceFactory deviceFactory

  /**
   * <p>
   * <i> --- This doc was last reviewed on 21 Dec 2013.</i>
   * </p><p>
   * Determines if the device accessed through this class is currently setup. The value of this field is modified
   * by {@link #trySetUp(org.droidmate.device.IDeployableAndroidDevice)}  and {@link #tryTearDown(org.droidmate.device.IDeployableAndroidDevice)}.
   *
   * </p><p>
   * Useful to be tested for in preconditions requiring for the device to be set-up.
   *
   */
  private boolean deviceIsSetup

  // To make DroidMate work with multiple A(V)D, this list will have to be one for all AndroidDeviceDeployer-s, not one per inst.
  private List<String> usedSerialNumbers = [] as List<String>


  AndroidDeviceDeployer(Configuration cfg, IAdbWrapper adbWrapper, IAndroidDeviceFactory deviceFactory)
  {
    this.cfg = cfg
    this.adbWrapper = adbWrapper
    this.deviceFactory = deviceFactory
  }

  /**
   * <p>
   * Setups android device for DroidMate purposes. Starts adb server if necessary, forwards ports, pushes uiautomator-daemon jar,
   * pushes monitor apk and starts uiautomator-daemon server.
   *
   * </p><p>
   * Remember to call {@link #tryTearDown} when done with the device.
   *
   * </p>
   * @throws DeviceException if any of the operation fails.
   */
  protected void trySetUp(IDeployableAndroidDevice device) throws DeviceException
  {
    this.adbWrapper.startAdbServer()

    // Nataniel: Had to invert order, otherwise it crashes on the first time it's executed because the UiAutomator2Daemon was never installed on the device
    device.reinstallUiautomatorDaemon()
    device.pushMonitorJar()
    device.setupConnection()
    device.initModel()

    this.deviceIsSetup = true
  }

  /**
   * <p>
   * Stops the uiautomator-daemon and removes its jar from the A(V)D. Call it after {@link #trySetUp}
   *
   * </p>
   * @throws DeviceException if any of the operations fails.
   * @see #trySetUp(IDeployableAndroidDevice)
   */
  protected void tryTearDown(IDeployableAndroidDevice device) throws DeviceException
  {
    assert device != null

    deviceIsSetup = false

    if (device.available)
    {
      log.trace("Tearing down.")
      device.pullLogcatLogFile()
      device.closeConnection()
      if (cfg.androidApi == Configuration.api23)
      {
        // WISH why failure is ignored here? Ask Borges
        device.uninstallApk(UiautomatorDaemonConstants.uia2Daemon_testPackageName, /* ignoreFailure = */ true)
        device.uninstallApk(UiautomatorDaemonConstants.uia2Daemon_packageName, /* ignoreFailure = */ true)
      } else throw new UnexpectedIfElseFallthroughError()
      device.removeJar(Paths.get(BuildConstants.monitor_on_avd_apk_name))
    }
    else
      log.trace("Device is not available. Skipping tear down.")
  }

  /**
   * <p>
   * Setups the A(V)D, executes the {@code closure} and tears down the device.
   * Adds any exceptions to the returned collection of exceptions.
   * </p>
   *
   * @see #trySetUp(IDeployableAndroidDevice)
   * @see #tryTearDown(IDeployableAndroidDevice)
   */
  @Override
  List<ExplorationException> withSetupDevice(int deviceIndex, Closure<List<ApkExplorationException>> computation)
  {
    log.info("Setup device with deviceIndex of $deviceIndex")
    Assert.checkClosureFirstParameterSignature(computation, IRobustDevice)

    List<ExplorationException> explorationExceptions = []
    //noinspection GroovyAssignabilityCheck
    def (IRobustDevice device, String serialNumber, Throwable throwable) = setupDevice(deviceIndex)
    if (throwable != null)
    {
      explorationExceptions << new ExplorationException(throwable)
      return explorationExceptions
    }

    assert explorationExceptions.empty
    try
    {
      List<ApkExplorationException> apkExplorationExceptions = computation(device)
      explorationExceptions += apkExplorationExceptions
    }
    catch (Throwable computationThrowable)
    {
      log.error("!!! Caught ${computationThrowable.class.simpleName} in withSetupDevice($deviceIndex)->computation($device). " +
        "This means ${ApkExplorationException.simpleName}s have been lost, if any! " +
        "Adding the exception as a cause to an ${ExplorationException.class.simpleName}. " +
        "Then adding to the collected exceptions list.\n" +
        "The ${computationThrowable.class.simpleName}: $computationThrowable")

      explorationExceptions << new ExplorationException(computationThrowable)
    }
    finally
    {
      log.debug("Finalizing: withSetupDevice($deviceIndex)->finally{} for computation($device)")
      try
      {
        tryTearDown(device)
        usedSerialNumbers -= serialNumber

      } catch (Throwable tearDownThrowable)
      {
        log.warn(Markers.appHealth, 
          "! Caught ${tearDownThrowable.class.simpleName} in withSetupDevice($deviceIndex)->tryTearDown($device). " +
          "Adding as a cause to an ${ExplorationException.class.simpleName}. " +
          "Then adding to the collected exceptions list.\n" +
          "The ${tearDownThrowable.class.simpleName}: $tearDownThrowable")

        explorationExceptions << new ExplorationException(tearDownThrowable)
      }
      log.debug("Finalizing DONE: withSetupDevice($deviceIndex)->finally{} for computation($device)")
    }
    return explorationExceptions
  }

  private List setupDevice(int deviceIndex)
  {
    try
    {
      String serialNumber = tryResolveSerialNumber(this.adbWrapper, this.usedSerialNumbers, deviceIndex)

      this.usedSerialNumbers << serialNumber

      IRobustDevice device = robustWithReadableLogs(this.deviceFactory.create(serialNumber))

      trySetUp(device)

      return [device, serialNumber, null]

    } catch (Throwable setupDeviceThrowable)
    {
      log.warn(Markers.appHealth, 
        "! Caught ${setupDeviceThrowable.class.simpleName} in setupDevice(deviceIndex: $deviceIndex). " +
        "Adding as a cause to an ${ExplorationException.class.simpleName}. Then adding to the collected exceptions list.")

      return [null, null, setupDeviceThrowable]
    }
  }

  private
  static String tryResolveSerialNumber(IAdbWrapper adbWrapper, List<String> usedSerialNumbers, int deviceIndex) throws DeviceException
  {
    List<AndroidDeviceDescriptor> devicesDescriptors = adbWrapper.getAndroidDevicesDescriptors()
    String serialNumber = getSerialNumber(devicesDescriptors, usedSerialNumbers, deviceIndex)
    return serialNumber

  }

  static String getSerialNumber(List<AndroidDeviceDescriptor> deviceDescriptors, List<String> usedSerialNumbers, int deviceIndex)
  {
//    log.trace("Serial numbers of found android devices:")
//    assert deviceDescriptors?.size() > 0
//    deviceDescriptors.each {AndroidDeviceDescriptor add -> log.trace(add.deviceSerialNumber)}

    List<String> unrecognizedNumbers = usedSerialNumbers.minus(deviceDescriptors*.deviceSerialNumber)
    if (unrecognizedNumbers.size() > 0)
      throw new DroidmateException("While obtaining new A(V)D serial number, DroidMate detected that one or more of the " +
        "already used serial numbers do not appear on the list of serial numbers returned by the 'adb devices' command. " +
        "This indicates the device(s) with these number most likely have been disconnected. Thus, DroidMate throws exception. " +
        "List of the offending serial numbers: $unrecognizedNumbers")

    def unusedDescriptors = deviceDescriptors.findAll {AndroidDeviceDescriptor add ->
      !(add.deviceSerialNumber in usedSerialNumbers)
    }

    if (unusedDescriptors.size() == 0)
      throw new DroidmateException("No unused A(V)D serial numbers have been found. List of all already used serial numbers: " +
        "$usedSerialNumbers")

    if (unusedDescriptors.size() < deviceIndex + 1)
      throw new DroidmateException("Requested device with device no. ${deviceIndex + 1} but the no. of available devices is ${unusedDescriptors.size()}.")

    String serialNumber
    serialNumber = unusedDescriptors.findAll {AndroidDeviceDescriptor add -> !add.isEmulator}[deviceIndex]?.deviceSerialNumber
    if (serialNumber == null)
      serialNumber = unusedDescriptors.findAll {AndroidDeviceDescriptor add -> add.isEmulator}[deviceIndex]?.deviceSerialNumber

    assert serialNumber != null
    return serialNumber
  }

  IRobustDevice robustWithReadableLogs(IAndroidDevice device)
  {
    return new RobustDevice(device, this.cfg)

  }


}
