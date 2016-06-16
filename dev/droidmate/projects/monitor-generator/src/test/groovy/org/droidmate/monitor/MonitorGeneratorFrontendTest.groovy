// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor

import groovy.transform.TypeChecked
import org.droidmate.common.BuildConstants
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static groovy.transform.TypeCheckingMode.SKIP

@TypeChecked(SKIP)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class MonitorGeneratorFrontendTest
{
  /**
   * Running this test as a part of a regression test suite is redundant, full rebuild will run the monitor-generator anyway.
   *
   * Use this test when working on the class.
   */
  @Test
  public void "Generates DroidMate monitor"()
  {
    Path actualMonitorJava = Paths.get(BuildConstants.monitor_generator_output_relative_path_api23)
    assert Files.notExists(actualMonitorJava) || Files.isWritable(actualMonitorJava)

    MonitorGeneratorFrontend.handleException = { Exception e -> throw e }

    // Act
    MonitorGeneratorFrontend.main(["api23"] as String[])

    assert Files.isRegularFile(actualMonitorJava)
    String actualText = actualMonitorJava.text
    assert !actualText.contains("public class MonitorJavaTemplate")
    assert actualText.contains("public class Monitor")
  }
}

