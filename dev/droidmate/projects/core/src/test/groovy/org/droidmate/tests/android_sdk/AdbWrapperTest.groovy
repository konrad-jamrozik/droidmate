// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tests.android_sdk

import groovy.transform.TypeChecked
import org.droidmate.android_sdk.AdbWrapper
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class AdbWrapperTest extends DroidmateGroovyTestCase
{
  @Test
  void "Removes adb started message if present"()
  {
    String sep = System.lineSeparator()

    String stdout = "* daemon not running. starting it now on port 5037 *$sep" +
      "* daemon started successfully *$sep" +
      "List of devices attached $sep$sep"
    String stderr = ""
    String[] stdStreams = [stdout, stderr]

    // act
    AdbWrapper.removeAdbStartedMsgIfPresent(stdStreams)

    assert stdStreams[0] == "List of devices attached $sep$sep".toString()
    assert stdStreams[1] == ""
  }

}
