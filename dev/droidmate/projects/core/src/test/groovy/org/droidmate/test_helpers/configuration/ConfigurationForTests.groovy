// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.test_helpers.configuration

import org.droidmate.configuration.Configuration
import org.droidmate.configuration.ConfigurationBuilder
import org.droidmate.filesystem.MockFileSystem
import org.droidmate.init.InitConstants

import java.nio.file.FileSystem
import java.nio.file.FileSystems

class ConfigurationForTests
{

  private FileSystem   fs       = FileSystems.default
  private List<String> argsList = []

  private static final List<String> zeroedTestConfig = [
    Configuration.pn_randomSeed, "0",
    Configuration.pn_uiautomatorDaemonWaitForWindowUpdateTimeout, "50",
    Configuration.pn_launchActivityDelay, "0",
    Configuration.pn_monitorServerStartTimeout, "0",
    Configuration.pn_monitorServerStartQueryDelay, "0",
    Configuration.pn_checkAppIsRunningRetryDelay, "0",
    // Commented out, as there are no tests simulating rebooting. However, sometimes I am manually testing real-world rebooting.
//    Configuration.pn_checkDeviceAvailableAfterRebootFirstDelay, "0",
//    Configuration.pn_checkDeviceAvailableAfterRebootLaterDelays, "0",
//    Configuration.pn_waitForCanRebootDelay, "0",
    Configuration.pn_clearPackageRetryDelay, "0",
    Configuration.pn_getValidGuiSnapshotRetryDelay, "0",
    Configuration.pn_stopAppSuccessCheckDelay, "0",
    Configuration.pn_closeANRDelay, "0",
  ]

  ConfigurationForTests()
  {
    this.argsList = zeroedTestConfig + [Configuration.pn_droidmateOutputDir, InitConstants.test_temp_dir_name]
  }

  public Configuration get()
  {
    new ConfigurationBuilder().build(this.argsList as String[], this.fs)
  }

  public ConfigurationForTests withMockFileSystem()
  {
    this.withFileSystem(new MockFileSystem([]).fs)
    return this
  }

  ConfigurationForTests withFileSystem(FileSystem fs)
  {
    this.fs = fs
    return this
  }

  ConfigurationForTests forDevice() {
    this.setArg([Configuration.pn_useApkFixturesDir, "true"])
    return this
  }

  ConfigurationForTests setArgs(List<String> args) {

    assert args.size() > 0
    assert args.size() % 2 == 0

    this.setArg(args.take(2))

    if (args.drop(2).size() > 0)
      this.setArgs(args.drop(2))

    return this
  }

  void setArg(List<String> argNameAndVal)
  {
    assert argNameAndVal.size() == 2

    int index = this.argsList.findIndexOf { it == argNameAndVal[0] }

    if (index != -1)
    {
      this.argsList.remove(index) // arg name
      this.argsList.remove(index) // arg val
    }

    this.argsList += argNameAndVal

  }
}
