// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logcat

import org.droidmate.common.logcat.Api
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class ApiTest
{

  @Test
  public void "Gets unique string on non-content-prefix uri"()
  {
    // Before this test passed, only "content://" was allowed.
    String offendingVal = "android.resource://com.twitter.android/2130837752"

    def api = new Api("ContentResolver", "someMethod", "void", ["android.net.Uri"],
      [offendingVal],
      "1", "dalvik.system.VMStack.getThreadStackTrace(Native Method)->dalvik.system.NativeStart.main(Native Method)")

    // Act
    api.getUniqueString()
  }

}
