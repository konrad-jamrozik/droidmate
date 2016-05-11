// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device_simulation

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.common.Boolean3
import org.droidmate.common.DroidmateException
import org.droidmate.device.IAndroidDevice
import org.droidmate.device.datatypes.*
import org.droidmate.exceptions.*
import org.droidmate.exploration.actions.WidgetExplorationAction
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.droidmate.misc.ITimeGenerator
import org.droidmate.misc.TimeGenerator
import org.droidmate.test_base.FilesystemTestFixtures

import java.nio.file.Path
import java.time.LocalDateTime

@Slf4j
public class AndroidDeviceSimulator implements IAndroidDevice
{

  private final List<IDeviceSimulation> simulations

  IDeviceSimulation currentSimulation

  private List<ITimeFormattedLogcatMessage> logcatMessagesToBeReadNext = []

  private final List<IExceptionSpec> exceptionSpecs

  private final ICallCounters callCounters = new CallCounters()

  /**
   * The simulator has only rudimentary support for multiple apps.
   * It is expected to be either used with one app, or with multiple apps only for exception handling simulation.
   * Right now "spec" is used for all the apks simulations on the simulator (obtained pkgNames) and a call to "installApk"
   * switches the simulations.
   */
  AndroidDeviceSimulator(
    ITimeGenerator timeGenerator,
    List<String> pkgNames = [FilesystemTestFixtures.apkFixture_simple_packageName],
    String spec,
    List<IExceptionSpec> exceptionSpecs = [],
    boolean unreliableSimulation = false)
  {

    this.simulations = pkgNames.collect {buildDeviceSimulation(timeGenerator, it, spec, unreliableSimulation)}
    this.exceptionSpecs = exceptionSpecs
    this.currentSimulation = this.simulations[0]

  }

  public IDeviceSimulation buildDeviceSimulation(ITimeGenerator timeGenerator, String packageName, String spec, boolean unreliable)
  {
    //noinspection GroovyIfStatementWithIdenticalBranches // WISH intellij BUG
    if (unreliable)
      return new UnreliableDeviceSimulation(timeGenerator, packageName, spec)
    else
      return new DeviceSimulation(timeGenerator, packageName, spec)
  }


  private String getCurrentlyDeployedPackageName()
  {
    return this.currentSimulation.packageName
  }

  @Override
  boolean hasPackageInstalled(String packageName) throws DeviceException
  {
    log.debug("hasPackageInstalled($packageName)")
    assert this.currentlyDeployedPackageName == packageName

    IExceptionSpec s = findMatchingExceptionSpecAndThrowIfApplies("hasPackageInstalled", packageName)
    if (s != null)
    {
      assert !s.throwsEx
      return s.exceptionalReturnBool
    }

    return this.currentlyDeployedPackageName == packageName
  }

  private IExceptionSpec findMatchingExceptionSpec(String methodName, String packageName)
  {
    return this.exceptionSpecs.findSingleOrDefault(null) {
      it.matches(methodName, packageName, callCounters.get(packageName, methodName))
    }
  }

  private IExceptionSpec findMatchingExceptionSpecAndThrowIfApplies(String methodName, String packageName) throws TestDeviceException
  {
    callCounters.increment(packageName, methodName)
    IExceptionSpec s = findMatchingExceptionSpec(methodName, packageName)
    if (s != null)
    {
      if (s.throwsEx)
        s.throwEx()
    }
    assert !(s?.throwsEx)
    return s
  }

  @Override
  IDeviceGuiSnapshot getGuiSnapshot() throws DeviceException
  {
    log.debug("getGuiSnapshot()")

    findMatchingExceptionSpecAndThrowIfApplies("getGuiSnapshot", this.currentlyDeployedPackageName)

    def outSnapshot = this.currentSimulation.currentGuiSnapshot

    log.debug("getGuiSnapshot(): $outSnapshot")
    return outSnapshot
  }

  @Override
  void perform(IAndroidDeviceAction action) throws TestDeviceException
  {
    log.debug("perform($action)")

    findMatchingExceptionSpecAndThrowIfApplies("perform", this.currentlyDeployedPackageName)

    switch (action.class)
    {
      case LaunchMainActivityDeviceAction:
        assert false : "call .launchMainActivity() directly instead"
        break
      case ClickGuiAction:
        updateSimulatorState(action)
        break
      case AdbClearPackageAction:
        assert false : "call .clearPackage() directly instead"
        break
      default:
        throw new UnexpectedIfElseFallthroughError()
    }
  }

  void updateSimulatorState(IAndroidDeviceAction action)
  {
    if (action instanceof WidgetExplorationAction)
      println "action widget id: ${(action as WidgetExplorationAction).widget.id}"

    this.currentSimulation.updateState(action)
    this.logcatMessagesToBeReadNext.addAll(currentSimulation.currentLogs)
  }

  @Override
  void clearLogcat() throws DroidmateException
  {
    log.debug("clearLogcat()")

    logcatMessagesToBeReadNext.clear()
  }

  @Override
  void closeConnection() throws DeviceException
  {
    findMatchingExceptionSpecAndThrowIfApplies("closeConnection", this.currentlyDeployedPackageName)
  }

  @Override
  List<ITimeFormattedLogcatMessage> readLogcatMessages(String messageTag)
  {
    List<ITimeFormattedLogcatMessage> returnedMessages = logcatMessagesToBeReadNext.findResults {it.tag == messageTag ? it : null}
    return returnedMessages
  }

  @Override
  List<ITimeFormattedLogcatMessage> waitForLogcatMessages(String messageTag, int minMessagesCount, int waitTimeout, int queryDelay) throws DeviceException
  {
    return readLogcatMessages(messageTag)
  }

  @Override
  LocalDateTime getCurrentTime()
  {
    return LocalDateTime.now()
  }

  @Override
  Boolean anyMonitorIsReachable()
  {
    this.currentSimulation.appIsRunning
  }

  @Override
  Boolean3 launchMainActivity(String launchableActivityComponentName) throws DeviceException
  {
    updateSimulatorState(new LaunchMainActivityDeviceAction(launchableActivityComponentName))
    return Boolean3.True
  }

  @Override
  Boolean appIsRunning(String appPackageName) throws DeviceNeedsRebootException, DeviceException
  {
    return this.currentSimulation.packageName == appPackageName && this.currentSimulation.appIsRunning
  }

  @Override
  void clickAppIcon(String iconLabel) throws DeviceNeedsRebootException, DeviceException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  void pushJar(Path jar, String targetFileName = null) throws DroidmateException
  {
  }

  @Override
  void removeJar(Path jar) throws DroidmateException
  {
  }

  @Override
  void installApk(IApk apk) throws DroidmateException
  {
    this.currentSimulation = simulations.findSingle {it.packageName == apk.packageName}
  }

  @Override
  void installApk(Path apk) throws DroidmateException
  {
    // Do nothing, used only to install UiAutomator2-daemon
  }

  @Override
  void uninstallApk(String apkPackageName, boolean ignoreFailure) throws DroidmateException
  {
    findMatchingExceptionSpecAndThrowIfApplies("uninstallApk", apkPackageName)
  }

  @Override
  void clearPackage(String apkPackageName)
  {
    updateSimulatorState(new AdbClearPackageAction(apkPackageName))
  }


  @Override
  void reboot() throws DeviceException
  {
    return
  }

  @Override
  boolean isAvailable()
  {
    return true
  }

  @Override
  boolean uiaDaemonClientThreadIsAlive()
  {
    return true
  }

  @Override
  void setupConnection() throws DeviceException
  {
  }

  @Override
  void removeLogcatLogFile() throws DeviceException
  {
  }

  @Override
  void pullLogcatLogFile() throws DeviceException
  {
  }

  @Override
  List<List<String>> readAndClearMonitorTcpMessages() throws DeviceNeedsRebootException, DeviceException
  {
    return []
  }

  public static AndroidDeviceSimulator build(
    ITimeGenerator timeGenerator = new TimeGenerator(),
    List<String> pkgNames,
    List<IExceptionSpec> exceptionSpecs = [],
    boolean unreliableSimulation = false)
  {
    return new AndroidDeviceSimulator(timeGenerator, pkgNames, "s1-w12->s2 " +
      "s1-w13->s3 " +
      "s2-w22->s2 " +
      "s2-w2h->home", exceptionSpecs, unreliableSimulation)
  }

  @Override
  public String toString()
  {
    return this.class.simpleName
  }

  @Override
  void initModel() throws DeviceException
  {
    // Do nothing
  }
}
