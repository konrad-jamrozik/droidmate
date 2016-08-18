// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

package org.droidmate.logcat

import org.droidmate.apis.Api
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
