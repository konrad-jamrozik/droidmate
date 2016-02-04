// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.test_base

import ch.qos.logback.classic.Level
import groovy.transform.TypeChecked
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.common.SysCmdExecutor
import org.droidmate.configuration.Configuration
import org.droidmate.logging.LogbackAppenders
import org.droidmate.logging.LogbackUtilsRequiringLogbackLog
import org.junit.Before

@TypeChecked
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
    // WISH maybe better solution is to use @Rule: https://edgblog.wordpress.com/2013/10/21/a-junit-rule-to-turn-test-logging-onoff/
    LogbackAppenders.setThresholdLevelOfStdStreamsAppenders(stdoutAppendersLogLevelForTesting)
    fixtures = new FilesystemTestFixtures(new AaptWrapper(Configuration.default, new SysCmdExecutor()))
  }

  @Before
  void setUp()
  {
    LogbackUtilsRequiringLogbackLog.cleanLogsDir()
  }
}
