// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.test_suites

import org.droidmate.command.uia_test_cases.UiaTestCaseLogsProcessorTest
import org.droidmate.report.ExplorationOutput2ReportTest
import org.droidmate.tests.android_sdk.AaptWrapperTest
import org.droidmate.tests.android_sdk.AdbWrapperTest
import org.droidmate.tests.configuration.ConfigurationBuilderTest
import org.droidmate.tests.device.DeviceTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/*
  N00b Reference:

  How test suites work:
  https://github.com/junit-team/junit/wiki/Aggregating-tests-in-suites
*/

@RunWith(Suite)
@Suite.SuiteClasses([
  ConfigurationBuilderTest,
  UiaTestCaseLogsProcessorTest,
  AaptWrapperTest,
  AdbWrapperTest,
  DeviceTest,
  ExplorationTestSuite,
  ExplorationOutput2ReportTest
])
class UnitTestSuite
{
}


