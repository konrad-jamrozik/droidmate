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
package org.droidmate.android_sdk

import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import org.droidmate.logging.Markers

import java.nio.file.Files
import java.nio.file.Path

// Suppresses warnings incorrectly caused by assertion checks in ctor.
@SuppressWarnings("GrFinalVariableAccess")
@Canonical
@Slf4j
class Apk implements IApk, Serializable
{

  private static final long serialVersionUID = 1

  transient final Path path
  final String fileName
  final String fileNameWithoutExtension
  final String absolutePath
  final String packageName
  final String launchableActivityName
  final String launchableActivityComponentName
  final String applicationLabel

   static Apk build(IAaptWrapper aapt, Path path)
  {
    assert aapt != null
    assert path != null
    assert Files.isRegularFile(path)

    String packageName, launchableActivityName, launchableActivityComponentName, applicationLabel
    try
    {
      (packageName, launchableActivityName, launchableActivityComponentName, applicationLabel) = aapt.getMetadata(path)
    } catch (LaunchableActivityNameProblemException | NotEnoughDataToStartAppException e)
    {
      log.warn(Markers.appHealth, "! While getting metadata for ${path.toString()}, got an: $e Returning null apk.")
      assert !(e instanceof LaunchableActivityNameProblemException) || ((e as LaunchableActivityNameProblemException).isFatal)
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
    assert path != null
    String fileName = path.fileName.toString()
    String absolutePath = path.toAbsolutePath().toString()

    assert fileName?.size() > 0
    assert fileName.endsWith(".apk")
    assert absolutePath?.size() > 0
    assert packageName?.size() > 0

    this.path = path
    this.fileName = fileName
    this.fileNameWithoutExtension = fileName.withoutExtension()
    this.absolutePath = absolutePath
    this.packageName = packageName
    this.launchableActivityName = launchableActivityName
    this.launchableActivityComponentName = launchableActivityComponentName
    this.applicationLabel = applicationLabel
    
    assert this.launchableActivityName?.length() > 0 || this.applicationLabel?.length() > 0
  }

  @Override
  Boolean getInlined()
  {
    this.fileName.endsWith("-inlined.apk")
  }

  @Override
  String toString() {
    return this.fileName
  }
}


