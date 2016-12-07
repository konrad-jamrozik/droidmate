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
package org.droidmate.test_tools.android_sdk

import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.android_sdk.IApk
import org.droidmate.misc.DroidmateException

import java.nio.file.Path

class AaptWrapperStub implements IAaptWrapper
{

  final List<IApk> apks

  AaptWrapperStub(List<IApk> apks)
  {
    this.apks = apks
  }

  @Override
  String getPackageName(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String getLaunchableActivityName(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String getLaunchableActivityComponentName(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String getApplicationLabel(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }


  @Override
  List<String> getMetadata(Path path) throws DroidmateException
  {
    def apk = apks.findSingle {it.absolutePath == path.toAbsolutePath().toString()}
    return [apk.packageName, apk.launchableActivityName, apk.launchableActivityComponentName, apk.applicationLabel]
  }
}
