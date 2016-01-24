// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.droidmate.test_suite_categories.ExcludedFromFastRegressionTests
import org.junit.experimental.categories.Categories
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Categories::class)
@Categories.ExcludeCategory(ExcludedFromFastRegressionTests::class)
// The scratchpad test class is added to avoid org.junit.runner.manipulation.NoTestsRemainException
@Suite.SuiteClasses(ExplorationOutput2ReportTest::class, KotlinScratchpadTestClass::class)
class ReporterTestSuite
{
}