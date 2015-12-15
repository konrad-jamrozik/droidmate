// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.frontend

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.storage.IStorage2
import org.droidmate.storage.Storage2

import java.nio.file.Files
import java.nio.file.Path

class DroidmateOutputDir
{

  final Path path

  DroidmateOutputDir(Path path)
  {
    this.path = path
    assert Files.isDirectory(path) || !Files.exists(path)
  }

  public void clearContentsOrCreate()
  {
    if (Files.exists(path))
    {
      Files.list(path).each {
        if (Files.isDirectory(it))
          it.deleteDir()
        else
          Files.delete(it)
      }
    } else
    {
      Files.createDirectory(path)
    }
  }

  public IApkExplorationOutput2 readOutput()
  {
    IStorage2 storage = new Storage2(path)
    def out = ExplorationOutput2.from(storage)
    assert out.size() == 1
    IApkExplorationOutput2 apkOut = out[0]
    return apkOut
  }

}
