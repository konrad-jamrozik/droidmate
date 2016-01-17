// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.data_aggregators

import org.droidmate.android_sdk.ApkTestHelper
import org.droidmate.test_base.FilesystemTestFixtures

class ExplorationOutput2TestHelper
{

  public static ExplorationOutput2 buildTrivialFixture()
  {
    def out = new ExplorationOutput2()

    def apkOut = new ApkExplorationOutput2(
      ApkTestHelper.build(
        FilesystemTestFixtures.apkFixture_simple_packageName,
        FilesystemTestFixtures.apkFixture_simple_launchableActivityComponentName,
        FilesystemTestFixtures.apkFixture_simple_packageName + "1",
        "applicationLabel")
    )

    out.add(apkOut)

    return out
  }


}
