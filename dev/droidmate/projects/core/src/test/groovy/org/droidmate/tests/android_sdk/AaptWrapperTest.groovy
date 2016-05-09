// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tests.android_sdk

import groovy.transform.TypeChecked
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.Apk
import org.droidmate.common.SysCmdExecutor
import org.droidmate.configuration.Configuration
import org.droidmate.test_base.DroidmateGroovyTestCase
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

    Apk ignoredApk = fixtures.apks.monitoredInlined_api19

    // Act
    String launchableActivityName = sut.getLaunchableActivityComponentName(Paths.get(ignoredApk.absolutePath))

    assert launchableActivityName == expectedLaunchableActivityName
  }

  //region Helper methods
  private static String getAaptBadgingDump()
  {
    String aaptBadgingDump = fixtures.f_aaptBadgingDump
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
