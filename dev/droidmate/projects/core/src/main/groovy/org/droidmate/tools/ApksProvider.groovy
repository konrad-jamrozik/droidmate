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

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.android_sdk.IApk
import org.droidmate.logging.Markers

import java.nio.file.Files
import java.nio.file.Path

// KJA handle:
// pkg: /data/local/tmp/com.google.android.keep_v2.3.02-inlined.apk
// Failure [INSTALL_FAILED_UPDATE_INCOMPATIBLE]

// KJA handle 2016-12-14 03:54:38.726 ERROR org.droidmate.frontend.ExceptionHandler  An ApkExplorationException was thrown during DroidMate run, pertaining to com.apusapps.launcher_v1.4.0-inlined.apk: org.droidmate.android_sdk.ApkExplorationException: org.droidmate.android_sdk.DeviceException: Failed to ensure home screen is displayed. Pressing 'home' button didn't help. Instead, ended with GUI state of: GuiState <pkg=android Widgets count = 18>.
// Full window hierarchy dump:

@Slf4j
class ApksProvider implements IApksProvider
{
  IAaptWrapper aapt

  ApksProvider(IAaptWrapper aapt)
  {
    this.aapt = aapt
  }

  List<Apk> getApks(Path apksDir, int apksLimit = 0, List<String> apksNames = [], boolean shuffle = false)
  {
    assert Files.isDirectory(apksDir)
    assert apksLimit >= 0
    
    log.info("Reading input apks from ${apksDir.toAbsolutePath().toString()}")

    List<Path> apks = Files.list(apksDir)
      .findAll {it.toString().endsWith(".apk")}
      .sort()

    if (!(apksNames.empty))
    {
      apks = apks.findAll {Path apk -> apk.fileName.toString() in apksNames}
      assert apksNames.every {it in (apks.collect {it.fileName.toString()})}
    }

    assert apksLimit <= apks.size()
    if (apksLimit != 0)
      apks = apks.take(apksLimit)

    if (apks.size() == 0)
      log.warn("No apks found! Apks were expected to be found in: {}", apksDir.toAbsolutePath().toString())

    Collection<IApk> builtApks = apks.findResults {Apk.build(aapt, it)}

    builtApks.findAll { !it.inlined }.each { log.info("Following input apk is not inlined: $it.fileName")}
    
    if (shuffle)
      Collections.shuffle(builtApks as List<IApk>)

    logApksUsedIntoRunData(builtApks)
    
    return builtApks
  }

  private void logApksUsedIntoRunData(Collection<IApk> apks)
  {
    log.info(Markers.runData, "Used input apks file paths:")
    log.info(Markers.runData, "")

    apks.each {log.info(Markers.runData, it.absolutePath)}

    log.info(Markers.runData, "")
    log.info(Markers.runData, "--------------------------------------------------------------------------------")
  }

}
