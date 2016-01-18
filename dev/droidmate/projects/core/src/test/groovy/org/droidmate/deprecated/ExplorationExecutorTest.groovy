// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import groovy.transform.TypeChecked
import org.droidmate.android_sdk.IApk
import org.droidmate.common.logcat.MonitoredInlinedApkFixtureApiLogs
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.Storage
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.init.InitConstants
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.RequiresDevice
import org.droidmate.tools.DeviceTools
import org.droidmate.tools.IDeviceTools
import org.droidmate.tools.SingleApkFixture
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

@Deprecated
@SuppressWarnings("GroovyAssignabilityCheck")
@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class ExplorationExecutorTest extends DroidmateGroovyTestCase
{

  @Deprecated
  @Category(RequiresDevice)
  // @Ignore("New implementation is tested with a test of the same name in class ExploratorTest.")
  @Test
  void "Collects monitored API calls logs during device exploration"()
  {
    Configuration cfg = new ConfigurationForTests().forDevice().setArgs([
      Configuration.pn_apksNames          , "[$InitConstants.monitored_inlined_apk_fixture_name]" as String,
      Configuration.pn_widgetIndexes      , "[0]"
    ]).get()

    IDeviceTools deviceTools = new DeviceTools(cfg)

    IApk apk = new SingleApkFixture(deviceTools.aapt, cfg)

    ExplorationExecutor explorationExecutor = ExplorationExecutor.build(cfg, new Storage(cfg.droidmateOutputDirPath))

    IApkExplorationOutput out = null
    deviceTools.deviceDeployer.withSetupDevice(0) {IRobustDevice device ->
      deviceTools.apkDeployer.withDeployedApk(device, apk) {IApk deployedApk ->

        // Act
        out = explorationExecutor.tryExploreAndSerialize(deployedApk.packageName, deployedApk.launchableActivityComponentName, device)
      }
    }

    MonitoredInlinedApkFixtureApiLogs apiLogs = new MonitoredInlinedApkFixtureApiLogs(out?.apiLogs)
    apiLogs.assertCheck()
  }
}

