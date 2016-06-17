// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate

import org.droidmate.device.datatypes.UiautomatorWindowDumpFunctionsTest
import org.junit.experimental.categories.Categories
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Categories::class)
@Suite.SuiteClasses(
  UiautomatorWindowDumpFunctionsTest::class,
  extensions_file_systemKtTest::class
)
class LibKotlinTestSuite
{
}