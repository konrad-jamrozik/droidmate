// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.android_sdk

import org.apache.commons.io.FilenameUtils

import java.nio.file.Path
import java.nio.file.Paths

public class ApkTestHelper
{

  public static Apk build(String name)
  {
    assert name?.size() > 0
    assert !name.endsWith(".apk")

    return new Apk(
      Paths.get("/path/to/${name}.apk"),
      "${name}.pkg_name",
      "${name}_lActName",
      "${name}_lActCompName")
  }

  public static Apk build(String packageName, String launchableActivityName, String launchableActivityComponentName)
  {
    Path path = Paths.get("/path/to/${packageName}.apk")
    return new Apk(
      path,
      packageName,
      launchableActivityName,
      launchableActivityComponentName)
  }

  public static Apk build(Path path)
  {
    assert path?.toString()?.size() > 0
    String name = FilenameUtils.getBaseName(path.fileName.toString())

    return new Apk(
      path,
      "${name}.pkg_name",
      "${name}_lActName",
      "${name}_lActCompName")
  }


}
