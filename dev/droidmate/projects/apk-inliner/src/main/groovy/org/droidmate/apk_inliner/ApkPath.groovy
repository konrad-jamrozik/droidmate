// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apk_inliner

import java.nio.file.Files
import java.nio.file.Path

class ApkPath
{
  @Delegate
  public final Path path

  ApkPath(Path path)
  {
    this.path = path

    assert path != null
    assert Files.isRegularFile(path)
    assert path.fileName.toString().endsWith(".apk")
  }

  public String getName()
  {
    return this.path.fileName.toString()
  }

}
