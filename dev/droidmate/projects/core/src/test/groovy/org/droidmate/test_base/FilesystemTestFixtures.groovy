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

import com.konradjamrozik.ResourcePath
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.common.BuildConstants
import org.droidmate.common.SysCmdExecutor
import org.droidmate.configuration.Configuration

import java.nio.file.Path

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
  public final WindowDumpFixtures windowDumps = new WindowDumpFixtures()

  static FilesystemTestFixtures build()
  {
    return new FilesystemTestFixtures(new AaptWrapper(Configuration.default, new SysCmdExecutor()))
  }

  FilesystemTestFixtures(IAaptWrapper aapt)
  {
    apks = new ApkFixtures(aapt)
    f_aaptBadgingDump = new ResourcePath("fixtures/f_aaptBadgingDump.txt").path.text
    // WISH this fixture still contains the old package name of monitor having "monitor_generator.generated" in it, for example,
    // in a stack trace of a device log:
    // org.droidmate.monitor_generator.generated.Monitor.redir_android_app_Activity_onResume0(Monitor.java:733)
    // Instead it should have prefix "org.droidmate.monitor.Monitor.redir" as given by 
    // org.droidmate.common.logcat.Api.monitorRedirectionPrefix
    f_monitoredSer2 = new ResourcePath("fixtures/serialized_results/2016 May 05 2257 org.droidmate.fixtures.apks.monitored.ser2").path
  }

  public class ApkFixtures
  {

    public final Apk gui
    public final Apk monitoredInlined_api19
    public final Apk monitoredInlined_api23

    ApkFixtures(IAaptWrapper aapt)
    {
      gui = Apk.build(aapt,
        new ResourcePath("${BuildConstants.apk_fixtures}/GuiApkFixture-debug.apk").path)

      monitoredInlined_api19 = Apk.build(aapt,
        new ResourcePath("${BuildConstants.apk_fixtures}/${BuildConstants.monitored_inlined_apk_fixture_api19_name}").path)
      monitoredInlined_api23 = Apk.build(aapt,
        new ResourcePath("${BuildConstants.apk_fixtures}/${BuildConstants.monitored_inlined_apk_fixture_api23_name}").path)
    }
  }

  public class WindowDumpFixtures
  {

    public final String f_app_stopped_dialogbox
    public final String f_app_stopped_OK_disabled
    public final String f_nexus7_home_screen
    // f_tsa = "Fixture of TestSubjectApp"
    public final String f_tsa_mainAct
    public final String f_tsa_emptyAct
    public final String f_tsa_1button
    public final String f_chrome_offline
    public final String f_complActUsing_dialogbox

    WindowDumpFixtures()
    {
      // @formatter:off
      f_app_stopped_dialogbox   = new ResourcePath("fixtures/window_dumps/f_app_stopped_dialogbox_nexus7vert.xml").path.text
      f_app_stopped_OK_disabled = new ResourcePath("fixtures/window_dumps/f_app_stopped_OK_disabled.xml").path.text
      f_nexus7_home_screen      = new ResourcePath("fixtures/window_dumps/f_nexus7_home_screen.xml").path.text
      f_tsa_mainAct             = new ResourcePath("fixtures/window_dumps/f_tsa_mainAct_4Jan14.xml").path.text
      f_tsa_emptyAct            = new ResourcePath("fixtures/window_dumps/f_tsa_empty_activity.xml").path.text
      f_tsa_1button             = new ResourcePath("fixtures/window_dumps/f_tsa_1button.xml").path.text
      f_chrome_offline          = new ResourcePath("fixtures/window_dumps/f_chrome_offline_nexus7vert.xml").path.text
      f_complActUsing_dialogbox = new ResourcePath("fixtures/window_dumps/f_complete_action_using.xml").path.text
      // @formatter:on
    }
  }
}
