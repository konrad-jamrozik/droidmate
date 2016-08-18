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
package org.droidmate.tools

import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.misc.BuildConstants
import org.droidmate.configuration.Configuration

class SingleApkFixture
{
  @SuppressWarnings("GrFinalVariableAccess")
  @Delegate
  final Apk apk

  SingleApkFixture(IAaptWrapper aapt, Configuration cfg)
  {
    assert aapt != null
    assert cfg != null
    assert cfg.useApkFixturesDir
    assert cfg.apksNames == [BuildConstants.monitored_inlined_apk_fixture_api19_name]

    ApksProvider apksProvider = new ApksProvider(aapt)

    List<Apk> apks = apksProvider.getApks(cfg.apksDirPath, cfg.apksLimit, cfg.apksNames)
    assert apks.size() == 1

    this.apk = apks.first()
  }


}
