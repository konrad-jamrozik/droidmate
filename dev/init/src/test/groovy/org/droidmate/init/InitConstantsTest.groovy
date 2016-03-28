// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.init

import groovy.transform.TypeChecked
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.Path
import java.nio.file.Paths

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class InitConstantsTest
{

  @Test
  void "Initializes static constants"()
  {
      // WISH this is hackish. Running the static init block is crucial for proper DM configuration, but it is done only when
      // this test is run. There should be a separate gradle task on which "build" depends that runs the local-config-files-setting
      // logic that is now run in the static init.

      // This instruction is present here to force static init block of the class to run.
      InitConstants.build_tools_version
      assert true // No exception was thrown during static initialization of the tested class.
  }


}