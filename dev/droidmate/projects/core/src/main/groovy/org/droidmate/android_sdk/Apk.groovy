// Copyright (c) 2012-2016 Saarland University
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
import org.droidmate.exceptions.LaunchableActivityNameProblemException

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
  final String applicationLabel

  public static Apk build(IAaptWrapper aapt, Path path)
  {
    assert aapt != null
    assert path != null
    assert Files.isRegularFile(path)

    String packageName, launchableActivityName, launchableActivityComponentName, applicationLabel
    try
    {
      (packageName, launchableActivityName, launchableActivityComponentName, applicationLabel) = aapt.getMetadata(path)
    } catch (LaunchableActivityNameProblemException e)
    {
      log.warn("! While getting metadata for ${path.toString()}, got an: $e Returning null apk.")
      assert e.isFatal
      return null
    }

    if ([launchableActivityName, launchableActivityComponentName].any {it == null})
    {
      assert [launchableActivityName, launchableActivityComponentName].every { it == null }
      log.debug("$Apk.simpleName class instance for ${path.toString()} has null launchableActivityName and thus also " +
        "launchableActivityComponentName.")
    }

    return new Apk(path, packageName, launchableActivityName, launchableActivityComponentName, applicationLabel)
  }

  Apk(Path path, String packageName, String launchableActivityName, String launchableActivityComponentName, String applicationLabel)
  {
    String fileName = path.fileName.toString()
    String absolutePath = path.toAbsolutePath().toString()

    assert fileName?.size() > 0
    assert fileName.endsWith(".apk")
    assert absolutePath?.size() > 0
    assert packageName?.size() > 0
    assert applicationLabel?.size() > 0

    this.fileName = fileName
    this.absolutePath = absolutePath
    this.packageName = packageName
    this.launchableActivityName = launchableActivityName
    this.launchableActivityComponentName = launchableActivityComponentName
    this.applicationLabel = applicationLabel
  }
}


