// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.tools

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.android_sdk.IApk
import org.droidmate.common.logging.Markers

import java.nio.file.Files
import java.nio.file.Path

@Slf4j
class ApksProvider implements IApksProvider
{

  IAaptWrapper aapt

  ApksProvider(IAaptWrapper aapt)
  {
    this.aapt = aapt
  }

  public List<Apk> getApks(Path apksDir, int apksLimit = 0, List<String> apksNames = [])
  {
    assert Files.isDirectory(apksDir)
    assert apksLimit >= 0

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
