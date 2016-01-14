// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.test_suites

import org.droidmate.tests_misc.GroovyTest
import org.droidmate.tests_misc.JavaTest
import org.droidmate.tests_misc.LogbackTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite)
@Suite.SuiteClasses([
  JavaTest,
  GroovyTest,
  LogbackTest,
])
class ThirdPartyAPIsTestSuite
{
}
