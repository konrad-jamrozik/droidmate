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

package org.droidmate.test_suites


import org.droidmate.report.ReporterTestSuite
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
  AaptWrapperTest,
  AdbWrapperTest,
  DeviceTest,
  ExplorationTestSuite,
  ReporterTestSuite
])
class UnitTestSuite
{
}


