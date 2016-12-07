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

import org.apache.commons.io.FilenameUtils
import org.droidmate.android_sdk.Apk

import java.nio.file.Path
import java.nio.file.Paths

class ApkTestHelper
{

  static Apk build(String name)
  {
    assert name?.size() > 0
    assert !name.endsWith(".apk")

    return new Apk(
      Paths.get("/path/to/${name}.apk"),
      "${name}.pkg_name",
      "${name}_lActName",
      "${name}_lActCompName",
      "${name}_applicationLabel")
  }

   static Apk build(String packageName, String launchableActivityName, String launchableActivityComponentName, String applicationLabel)
  {
    Path path = Paths.get("/path/to/${packageName}.apk")
    return new Apk(
      path,
      packageName,
      launchableActivityName,
      launchableActivityComponentName,
      applicationLabel)
  }

   static Apk build(Path path)
  {
    assert path?.toString()?.size() > 0
    String name = FilenameUtils.getBaseName(path.fileName.toString())

    return new Apk(
      path,
      "${name}.pkg_name",
      "${name}_lActName",
      "${name}_lActCompName",
      "${name}_applicationLabel")
  }


}
