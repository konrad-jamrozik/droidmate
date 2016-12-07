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
package org.droidmate.test_tools.filesystem

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.droidmate.android_sdk.IApk
import org.droidmate.test_tools.android_sdk.ApkTestHelper

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

import static org.droidmate.configuration.Configuration.defaultApksDir

class MockFileSystem
{

  final FileSystem fs
  final List<IApk> apks

  MockFileSystem(List<String> appNames)
  {
    appNames.each { assert !it.endsWith(".apk") }

    def apkNames = appNames.collect { it + ".apk" }

    def res = this.build(apkNames)
    this.fs = res.first
    List<String> filePaths = res.second

    this.apks = filePaths.collect {
      ApkTestHelper.build(this.fs.getPath(it))}
  }

  private Tuple2<FileSystem, List<String>> build(List<String> apkNames)
  {
    FileSystem fs = Jimfs.newFileSystem(Configuration.unix())

    Path apksDir = fs.getPath(defaultApksDir)

    Files.createDirectories(apksDir)

    List<String> apkFilePaths = apkNames.collect {
      def apkFilePath = Files.createFile(apksDir.resolve(it))
      apkFilePath.toAbsolutePath().toString()
    }
    return [fs, apkFilePaths]
  }
}
