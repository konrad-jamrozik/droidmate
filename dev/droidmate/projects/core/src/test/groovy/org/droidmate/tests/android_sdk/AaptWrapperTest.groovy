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
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.Apk
import org.droidmate.misc.SysCmdExecutor
import org.droidmate.configuration.Configuration
import org.droidmate.tests.ApkFixtures
import org.droidmate.tests.DroidmateGroovyTestCase
import org.droidmate.tests.FixturesKt
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.Path
import java.nio.file.Paths

import static groovy.transform.TypeCheckingMode.SKIP

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class AaptWrapperTest extends DroidmateGroovyTestCase
{
  @Test
  public void "Gets launchable activity component name from badging dump"()
  {
    String aaptBadgingDump = aaptBadgingDump

    // Act
    String launchableActivityName = AaptWrapper.tryGetLaunchableActivityComponentNameFromBadgingDump(aaptBadgingDump)

    assert launchableActivityName == expectedLaunchableActivityName
  }

  @TypeChecked(SKIP)
  @Test
  public void "Gets launchable activity component name"()
  {

    AaptWrapper sut = new AaptWrapper(Configuration.default, new SysCmdExecutor())
    sut.metaClass.aaptDumpBadging = {Path _ -> aaptBadgingDump}

    Apk ignoredApk = ApkFixtures.build().monitoredInlined_api19

    // Act
    String launchableActivityName = sut.getLaunchableActivityComponentName(Paths.get(ignoredApk.absolutePath))

    assert launchableActivityName == expectedLaunchableActivityName
  }

  //region Helper methods
  private static String getAaptBadgingDump()
  {
    String aaptBadgingDump = FixturesKt.fixture_aaptBadgingDump
    assert aaptBadgingDump.contains("package: name='com.box.android'")
    assert aaptBadgingDump.contains("launchable-activity: name='com.box.android.activities.SplashScreenActivity'")
    return aaptBadgingDump
  }

  private static String getExpectedLaunchableActivityName()
  {
    return "com.box.android/com.box.android.activities.SplashScreenActivity"
  }

  //endregion Helper methods

}
