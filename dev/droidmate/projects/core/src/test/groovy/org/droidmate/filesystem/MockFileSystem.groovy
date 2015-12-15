// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.filesystem

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.droidmate.android_sdk.ApkTestHelper
import org.droidmate.android_sdk.IApk
import org.droidmate.init.InitConstants

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

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

    Path apksDir = fs.getPath(InitConstants.apks_dir)

    Files.createDirectories(apksDir)

    List<String> apkFilePaths = apkNames.collect {
      def apkFilePath = Files.createFile(apksDir.resolve(it))
      apkFilePath.toAbsolutePath().toString()
    }
    return [fs, apkFilePaths]
  }
}
