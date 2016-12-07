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

package org.droidmate.tests.android_sdk

import groovy.transform.TypeChecked
import org.droidmate.android_sdk.AdbWrapper
import org.droidmate.test_tools.DroidmateGroovyTestCase
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

    // Act
    AdbWrapper.removeAdbStartedMsgIfPresent(stdStreams)

    assert stdStreams[0] == "List of devices attached $sep$sep".toString()
    assert stdStreams[1] == ""
  }

}
