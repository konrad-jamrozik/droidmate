// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.android_sdk

import groovy.transform.Canonical
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Path

// Suppresses warnings incorrectly caused by assertion checks in ctor.
@SuppressWarnings("GrFinalVariableAccess")
@Canonical
@Slf4j
class Apk implements IApk, Serializable
{

  private static final long serialVersionUID = 1

  final String fileName
  final String absolutePath
  final String packageName
  final String launchableActivityName
  final String launchableActivityComponentName

  public static Apk build(IAaptWrapper aapt, Path path)
  {
    assert aapt != null
    assert path != null
    assert Files.isRegularFile(path)

    def (String packageName, String launchableActivityName, String launchableActivityComponentName) = aapt.getMetadata(path)

    if ([packageName, launchableActivityName, launchableActivityComponentName].any {it == null})
    {
      log.warn("Failed to build apk from ${path.toString()} because some of its metadata is null: " +
        "packageName=$packageName, " +
        "launchableActivityName=$launchableActivityName, " +
        "launchableActivityComponentName=$launchableActivityComponentName. Skipping the apk.")
      return null
    }
    else
    {
      return new Apk(path, packageName, launchableActivityName, launchableActivityComponentName)
    }
  }

  Apk(Path path, String packageName, String launchableActivityName, String launchableActivityComponentName)
  {
    String fileName = path.fileName.toString()
    String absolutePath = path.toAbsolutePath().toString()

    assert fileName?.size() > 0
    assert fileName.endsWith(".apk")
    assert absolutePath?.size() > 0
    assert packageName?.size() > 0
    assert launchableActivityName?.size() > 0
    assert launchableActivityComponentName?.size() > 0

    this.fileName = fileName
    this.absolutePath = absolutePath
    this.packageName = packageName
    this.launchableActivityName = launchableActivityName
    this.launchableActivityComponentName = launchableActivityComponentName
  }

}


