// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.test_suites

import org.droidmate.command.exploration.ExplorationTest
import org.droidmate.common.logcat.TimeFormattedLogcatMessageTest
import org.droidmate.device.datatypes.UiautomatorWindowDumpFunctionsTest
import org.droidmate.device.datatypes.UiautomatorWindowDumpTest
import org.droidmate.exploration.strategy.ExplorationStrategyTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite)
@Suite.SuiteClasses([
  TimeFormattedLogcatMessageTest,
  UiautomatorWindowDumpFunctionsTest,
  UiautomatorWindowDumpTest,
  ExplorationStrategyTest,
  ExplorationTest
])
class ExplorationTestSuite
{
}
