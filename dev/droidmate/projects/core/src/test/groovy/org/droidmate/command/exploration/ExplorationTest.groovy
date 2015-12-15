// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command.exploration

import org.droidmate.android_sdk.ApkTestHelper
import org.droidmate.android_sdk.IApk
import org.droidmate.common.logcat.MonitoredInlinedApkFixtureApiLogs
import org.droidmate.configuration.Configuration
import org.droidmate.device_simulation.AndroidDeviceSimulator
import org.droidmate.device_simulation.DeviceSimulation
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.IDeviceWithReadableLogs
import org.droidmate.exploration.device.RobustDevice
import org.droidmate.init.InitConstants
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.misc.ITimeGenerator
import org.droidmate.misc.TimeGenerator
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.ExplorationImplAug2015
import org.droidmate.test_suite_categories.RequiresDevice
import org.droidmate.test_suite_categories.RequiresSimulator
import org.droidmate.tools.DeviceTools
import org.droidmate.tools.IDeviceTools
import org.droidmate.tools.SingleApkFixture
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class ExplorationTest extends DroidmateGroovyTestCase
{

  // it fails because the device simulator now always uses UnreliableGuiSnapshot and the code doesn't handle it yet.
  // after it passes 'app has stopped' dialog box, work on providing invalid gui snapshots, within the retry attempts, of course.
  @Category([RequiresSimulator,ExplorationImplAug2015])
  @Test
  void "Runs on simulator"()
  {
    String simulatorSpec = "s1-w1->s1"
    runOnSimulator(simulatorSpec)
  }

  @Category([RequiresSimulator,ExplorationImplAug2015])
  @Test
  void "Supports external app displayed after explored app reset"()
  {
    String simulatorSpec = "s1-w1->chrome"
    runOnSimulator(simulatorSpec)
  }

  @Category([RequiresSimulator,ExplorationImplAug2015])
  @Test
  void "Supports external app displayed after exploration termination"()
  {
    String simulatorSpec = "s1-w1->chrome"
    Configuration cfg = new ConfigurationForTests().setArgs([Configuration.pn_actionsLimit, "1"]).get()
    runOnSimulator(simulatorSpec, cfg)
  }

  @Category([RequiresDevice, ExplorationImplAug2015])
  @Test
  void "Collects monitored API calls logs during device exploration"()
  {
    Configuration cfg = new ConfigurationForTests().forDevice().setArgs([
      Configuration.pn_apksNames          , "[$InitConstants.monitored_inlined_apk_fixture_name]",
      Configuration.pn_widgetIndexes      , "[0]",
    ]).get()

    // Configuration cfg = new ConfigurationBuilder().build(args)
    IDeviceTools deviceTools = new DeviceTools(cfg)

    IApk apk = new SingleApkFixture(deviceTools.aapt, cfg)

    Exploration exploration = Exploration.build(cfg)

    IApkExplorationOutput2 out = null
    deviceTools.deviceDeployer.withSetupDevice(0) {IDeviceWithReadableLogs device ->
      deviceTools.apkDeployer.withDeployedApk(device, apk) {IApk deployedApk ->

        // Act
        out = exploration.tryRun(deployedApk, new RobustDevice(device,
          cfg.monitorServerStartTimeout,
          cfg.monitorServerStartQueryInterval,
          cfg.clearPackageRetryAttempts,
          cfg.clearPackageRetryDelay,
          cfg.getValidGuiSnapshotRetryAttempts,
          cfg.getValidGuiSnapshotRetryDelay)
        )

      }
    }

    MonitoredInlinedApkFixtureApiLogs apiLogs = new MonitoredInlinedApkFixtureApiLogs(extractApiLogsList(out.actRess))
    apiLogs.assertCheck()
  }

  private void runOnSimulator(String simulatorSpec)
  {
    def cfg = new ConfigurationForTests().get()
    runOnSimulator(simulatorSpec, cfg)
  }

  private void runOnSimulator(String simulatorSpec, Configuration cfg)
  {
    ITimeGenerator timeGenerator = new TimeGenerator()

    def apk = ApkTestHelper.build("mock_app1")
    def simulator = new AndroidDeviceSimulator(timeGenerator, [apk.packageName], simulatorSpec)
    def simulatedDevice = new RobustDevice(simulator,
      cfg.monitorServerStartTimeout,
      cfg.monitorServerStartQueryInterval,
      cfg.clearPackageRetryAttempts,
      cfg.clearPackageRetryDelay,
      cfg.getValidGuiSnapshotRetryAttempts,
      cfg.getValidGuiSnapshotRetryDelay)

    Exploration exploration = Exploration.build(cfg, timeGenerator)

    // Act
    def out2 = exploration.tryRun(apk, simulatedDevice)

    if (!out2.noException)
      out2.exception.printStackTrace()

    assert out2.noException

    def out2Simulation = new DeviceSimulation(out2)
    def expectedSimulation = simulator.currentSimulation
    out2Simulation.assertEqual(expectedSimulation)

  }

  private List<List<IApiLogcatMessage>> extractApiLogsList(List<RunnableExplorationActionWithResult> actions)
  {
    return actions.collect {RunnableExplorationActionWithResult pair -> return pair.result.deviceLogs.apiLogsOrEmpty}
  }
}