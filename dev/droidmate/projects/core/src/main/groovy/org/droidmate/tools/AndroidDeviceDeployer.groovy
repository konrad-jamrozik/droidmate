// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tools

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.AndroidDeviceDescriptor
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.common.Assert
import org.droidmate.common.DroidmateException
import org.droidmate.configuration.Configuration
import org.droidmate.device.IAndroidDevice
import org.droidmate.device.IDeployableAndroidDevice
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.device.IDeviceWithReadableLogs
import org.droidmate.exploration.device.RobustDevice
import org.droidmate.lib_android.MonitorJavaTemplate

@Slf4j

public class AndroidDeviceDeployer implements IAndroidDeviceDeployer
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


  public AndroidDeviceDeployer(Configuration cfg, IAdbWrapper adbWrapper, IAndroidDeviceFactory deviceFactory)
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

    adbWrapper.startAdbServer()

    device.forwardPort(cfg.uiautomatorDaemonTcpPort)
    device.forwardPort(MonitorJavaTemplate.srv_port)

    device.pushJar(cfg.uiautomatorDaemonJar)
    device.pushJar(cfg.monitorApk)

    device.startUiaDaemon()

    deviceIsSetup = true
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

    device.stopUiaDaemon()
    device.removeJar(cfg.uiautomatorDaemonJar)
    device.removeJar(cfg.monitorApk)
  }

  /**
   * <p>
   * <i> --- This doc was last reviewed on 21 Dec 2013.</i>
   * </p><p>
   * Setups the A(V)D, executes the {@code closure} and tears down the device.
   * </p>
   *
   * @see #trySetUp(IDeployableAndroidDevice)
   * @see #tryTearDown(IDeployableAndroidDevice)
   */
  @Override
  public void withSetupDevice(int deviceIndex, Closure computation) throws DeviceException
  {
    log.info("withSetupDevice(deviceIndex: $deviceIndex, computation)")

    Assert.checkClosureFirstParameterSignature(computation, IDeviceWithReadableLogs)

    String serialNumber = resolveSerialNumber(adbWrapper, usedSerialNumbers, deviceIndex)

    usedSerialNumbers << serialNumber

    IDeviceWithReadableLogs device = withReadableLogs(deviceFactory.create(serialNumber))

    trySetUp(device)

    Throwable savedTryThrowable = null
    try
    {
      computation(device)
    } catch (Throwable tryThrowable)
    {
      log.debug("! Caught ${tryThrowable.class.simpleName} in withSetupDevice.computation(device). Rethrowing.")
      savedTryThrowable = tryThrowable
      throw savedTryThrowable

    } finally
    {
      log.debug("Finalizing: withSetupDevice.finally {} for computation(device)")
      try
      {
        tryTearDown(device)
        usedSerialNumbers -= serialNumber

      } catch (Throwable tearDownThrowable)
      {
        log.debug("! Caught ${tearDownThrowable.class.simpleName} in tryTearDown(device). Adding suppressed exception, if any, and rethrowing.")
        if (savedTryThrowable != null)
          tearDownThrowable.addSuppressed(savedTryThrowable)
        throw tearDownThrowable
      }
      log.debug("Finalizing DONE: withSetupDevice.finally {} for computation(device)")
    }
  }

  private static String resolveSerialNumber(IAdbWrapper adbWrapper, List<String> usedSerialNumbers, int deviceIndex)
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
        "List of the offending serial numbers: $unrecognizedNumbers");

    def unusedDescriptors = deviceDescriptors.findAll {AndroidDeviceDescriptor add ->
      !(add.deviceSerialNumber in usedSerialNumbers)
    }

    if (unusedDescriptors.size() == 0)
      throw new DroidmateException("No unused A(V)D serial numbers have been found. List of all already used serial numbers: " +
        "$usedSerialNumbers")

    if (unusedDescriptors.size() < deviceIndex + 1)
      throw new DroidmateException("Requested device with device no. ${deviceIndex + 1} but the no. of available devices is ${unusedDescriptors.size()}.")

    String serialNumber;
    serialNumber = unusedDescriptors.findAll {AndroidDeviceDescriptor add -> !add.isEmulator}[deviceIndex]?.deviceSerialNumber
    if (serialNumber == null)
      serialNumber = unusedDescriptors.findAll {AndroidDeviceDescriptor add -> add.isEmulator}[deviceIndex]?.deviceSerialNumber

    assert serialNumber != null
    return serialNumber
  }

  IDeviceWithReadableLogs withReadableLogs(IAndroidDevice device)
  {
    return new RobustDevice(device,
      cfg.monitorServerStartTimeout,
      cfg.monitorServerStartQueryInterval,
      cfg.clearPackageRetryAttempts,
      cfg.clearPackageRetryDelay,
      cfg.getValidGuiSnapshotRetryAttempts,
      cfg.getValidGuiSnapshotRetryDelay)

  }


}
