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

package org.droidmate.test_base

import ch.qos.logback.classic.Level
import org.droidmate.common.BuildConstants
import org.droidmate.logging.LogbackAppenders
import org.droidmate.logging.LogbackUtilsRequiringLogbackLog
import org.junit.Before

class DroidmateGroovyTestCase extends GroovyTestCase
{
  /*
    Used for profiling the JUnit test runs with VisualVM. Uncomment, run the tests with -Xverify:none JVM option and make sure
    that in those 5 seconds you will select the process in VisualVM, click the "profiler" tab and start CPU profiling.
    For more, see Konrad's OneNote / Reference / Technical / Java / Profiling.

   */
//  static {
//    println "Waiting for profiler for 5 seconds"
//    Thread.sleep(5000)
//    println "Done waiting!"
//  }


  public static Level                  stdoutAppendersLogLevelForTesting = Level.ERROR
  public static FilesystemTestFixtures fixtures
  static {
    Locale.setDefault(BuildConstants.locale)
    // WISH maybe better solution is to use @Rule: https://edgblog.wordpress.com/2013/10/21/a-junit-rule-to-turn-test-logging-onoff/
    LogbackAppenders.setThresholdLevelOfStdStreamsAppenders(stdoutAppendersLogLevelForTesting)
    fixtures = FilesystemTestFixtures.build()
  }

  @Before
  void setUp()
  {
    LogbackUtilsRequiringLogbackLog.cleanLogsDir()
  }
}
