// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.test_suites_based_on_category

import org.droidmate.test_suite_categories.ExcludedFromFastRegressionTests
import org.droidmate.test_suites.RegressionTestSuite
import org.junit.experimental.categories.Categories
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Categories)
@Categories.ExcludeCategory(ExcludedFromFastRegressionTests)
@Suite.SuiteClasses([
  RegressionTestSuite
])
class FastRegressionTestSuite
{
}
