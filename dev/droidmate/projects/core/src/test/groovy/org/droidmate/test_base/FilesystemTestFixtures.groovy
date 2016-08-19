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

package org.droidmate.test_base

import com.konradjamrozik.Resource
import com.konradjamrozik.ResourcePath
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.misc.BuildConstants
import org.droidmate.misc.SysCmdExecutor
import org.droidmate.configuration.Configuration

import java.nio.file.Path
import java.nio.file.Paths

/**
 * <p>
 * This class provides access to fixtures located on the file system, like apks, windows dumps, etc.
 *
 * </p>
 */
class FilesystemTestFixtures
{

  // !!! DUPLICATION WARNING !!!
  // These values have to be the same as the ones of the apk fixture file represented by apkName_simple variable
  // (search the source code for the variable name to find its defining class).
  public static String apkFixture_simple_packageName                     = "org.droidmate.fixtures.apks.simple"
  public static String apkFixture_simple_launchableActivityComponentName =
    "org.droidmate.fixtures.apks.simple/org.droidmate.fixtures.apks.simple.MainActivity"
  // end of DUPLICATION WARNING

  public String f_aaptBadgingDump
  /**
   * The metadata to the run used for this fixture is located in directory located in the same dir as this fixture.
   * In addition, the run configuration is codified in IntelliJ run config of "Explore fixture: f_monitoredSer2"
   */
  public Path   f_monitoredSer2

  public final ApkFixtures        apks

  static FilesystemTestFixtures build()
  {
    return new FilesystemTestFixtures(new AaptWrapper(Configuration.default, new SysCmdExecutor()))
  }

  FilesystemTestFixtures(IAaptWrapper aapt)
  {
    apks = new ApkFixtures(aapt)

    // KJA consider using org.droidmate.extractedPath
    f_aaptBadgingDump = new Resource("fixtures/f_aaptBadgingDump.txt").extractTo(Paths.get(BuildConstants.dir_name_temp_extracted_resources)).text
    // KJA consider using org.droidmate.extractedPath
    f_monitoredSer2 = new Resource("fixtures/serialized_results/2016 May 05 2257 org.droidmate.fixtures.apks.monitored.ser2").extractTo(Paths.get(BuildConstants.dir_name_temp_extracted_resources))
  }

  public class ApkFixtures
  {

    public final Apk gui
    public final Apk monitoredInlined_api19
    public final Apk monitoredInlined_api23

    ApkFixtures(IAaptWrapper aapt)
    {
      gui = Apk.build(aapt,
        // KJA consider using org.droidmate.extractedPath
        new Resource("${BuildConstants.apk_fixtures}/GuiApkFixture-debug.apk").extractTo(Paths.get(BuildConstants.dir_name_temp_extracted_resources)))

      monitoredInlined_api19 = Apk.build(aapt,
        // KJA consider using org.droidmate.extractedPath
        new Resource("${BuildConstants.apk_fixtures}/${BuildConstants.monitored_inlined_apk_fixture_api19_name}").extractTo(Paths.get(BuildConstants.dir_name_temp_extracted_resources)))
      monitoredInlined_api23 = Apk.build(aapt,
        // KJA consider using org.droidmate.extractedPath
        new Resource("${BuildConstants.apk_fixtures}/${BuildConstants.monitored_inlined_apk_fixture_api23_name}").extractTo(Paths.get(BuildConstants.dir_name_temp_extracted_resources)))
    }
  }
}
