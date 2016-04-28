// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.Apk
import org.droidmate.apk_inliner.ApkInliner
import org.droidmate.common.SysCmdExecutor
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.ThrowablesCollection
import org.droidmate.tools.ApksProvider

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Slf4j
class InlineCommand extends DroidmateCommand
{
  private final ApkInliner inliner

  InlineCommand(ApkInliner inliner)
  {
    this.inliner = inliner
  }

  static InlineCommand build()
  {
    return new InlineCommand(ApkInliner.build())
  }

  @Override
  void execute(Configuration cfg) throws ThrowablesCollection
  {
    log.info("DroidMate was instructed to inline apks.")
    def apksProvider = new ApksProvider(new AaptWrapper(cfg, new SysCmdExecutor()))
    List<Apk> apks = apksProvider.getApks(cfg.apksDirPath)
    
    if (apks.every { it.inlined } )
    {
      log.warn("No not inlined apks found. Aborting.")
      return
    }

    Path originalsDir = cfg.apksDirPath.resolve("originals")
    if (originalsDir.createDirIfNotExists())
      log.info("Created directory to hold original apks, before inlining: "+originalsDir.toAbsolutePath().toString())

    apks.findAll { !it.inlined }.each { Apk apk ->

      makeOriginalCopy(apk, originalsDir)
      // KJA instead of making original copy, move the original to originals dir after inlining is done.
      
      inliner.inline(apk.path, apk.path.parent)
    }
  }

  private void makeOriginalCopy(Apk apk, Path originalsDir)
  {
    Path original = originalsDir.resolve(apk.fileName)

    if (!Files.exists(original))
    {
      Files.copy(apk.path, original, StandardCopyOption.REPLACE_EXISTING)
      log.info("Created copy of $original.fileName in '${originalsDir.fileName}' subdir.")
    } else
    {
      log.info("Skipping creating copy of $original.fileName in '${originalsDir.fileName}' subdir: it already exists.")
    }
  }
}
