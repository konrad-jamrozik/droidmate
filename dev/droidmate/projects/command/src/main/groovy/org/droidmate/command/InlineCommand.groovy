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
package org.droidmate.command

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.Apk
import org.droidmate.apk_inliner.ApkInliner
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.ThrowablesCollection
import org.droidmate.misc.SysCmdExecutor
import org.droidmate.tools.ApksProvider

import java.nio.file.Files
import java.nio.file.Path

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
    def apksProvider = new ApksProvider(new AaptWrapper(cfg, new SysCmdExecutor()))
    List<Apk> apks = apksProvider.getApks(cfg.apksDirPath)
    
    if (apks.every { it.inlined } )
    {
      log.warn("No non-inlined apks found. Aborting.")
      return
    }

    Path originalsDir = cfg.apksDirPath.resolve("originals")
    if (originalsDir.createDirIfNotExists())
      log.info("Created directory to hold original apks, before inlining: "+originalsDir.toAbsolutePath().toString())

    apks.findAll { !it.inlined }.each { Apk apk ->

      inliner.inline(apk.path, apk.path.parent)
      log.info("Inlined ${apk.fileName}")
      moveOriginal(apk, originalsDir)
    }
  }

  private void moveOriginal(Apk apk, Path originalsDir)
  {
    Path original = originalsDir.resolve(apk.fileName)

    if (!Files.exists(original))
    {
      Files.move(apk.path, original)
      log.info("Moved $original.fileName to '${originalsDir.fileName}' subdir.")
    } else
    {
      log.info("Skipped moving $original.fileName to '${originalsDir.fileName}' subdir: it already exists there.")
    }
  }
}
