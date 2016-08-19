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

package org.droidmate.tests

import com.konradjamrozik.Resource
import org.droidmate.Extensions_ResourceKt
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.configuration.Configuration
import org.droidmate.misc.BuildConstants
import org.droidmate.misc.SysCmdExecutor

class ApkFixtures
{
  public static String apkFixture_simple_packageName = "org.droidmate.fixtures.apks.simple"

  public final Apk gui
  public final Apk monitoredInlined_api19
  public final Apk monitoredInlined_api23
  
  static ApkFixtures build()
  {
    return new ApkFixtures(new AaptWrapper(Configuration.default, new SysCmdExecutor()))
  }

  ApkFixtures(IAaptWrapper aapt)
  {
    gui = Apk.build(aapt, Extensions_ResourceKt.getExtractedPath(new Resource("${BuildConstants.apk_fixtures}/GuiApkFixture-debug.apk")))
    monitoredInlined_api19 = Apk.build(aapt, Extensions_ResourceKt.getExtractedPath(new Resource("${BuildConstants.apk_fixtures}/${BuildConstants.monitored_inlined_apk_fixture_api19_name}")))
    monitoredInlined_api23 = Apk.build(aapt, Extensions_ResourceKt.getExtractedPath(new Resource("${BuildConstants.apk_fixtures}/${BuildConstants.monitored_inlined_apk_fixture_api23_name}")))
  }
}
