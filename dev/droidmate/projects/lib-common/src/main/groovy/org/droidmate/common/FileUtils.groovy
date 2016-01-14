// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common

import java.nio.file.FileSystem
import java.nio.file.Path

import static java.nio.file.Files.*

class FileUtils
{

   static Path findFileInAncestors(String searchedFileName, FileSystem fs)
  {
    Path startingDir = fs.getPath("").toRealPath()
    Path searchedDir = startingDir
    Path iniFile = searchedDir.resolve(searchedFileName)
    if (!isRegularFile(iniFile))
      iniFile = null

    while (iniFile == null && searchedDir.parent != null)
    {
      searchedDir = searchedDir.parent
      iniFile = (Path) list(searchedDir).find { Path candidateFile ->
        candidateFile.fileName.toString() == searchedFileName
      }
    }

    if (iniFile == null)
      throw new DroidmateException("Failed to find $searchedFileName in $startingDir or any of its ancestors.")
    else
      return iniFile

  }

  static void validateDirectory(Path dir)
  {
    if (!isDirectory(dir))
      throw new DroidmateException("Directory $dir doesn't exist or is not a directory. \n" +
        "Expected path: ${dir.toAbsolutePath()}");
  }

  static void validateDirectory(File dir)
  {
    if (!dir.isDirectory())
      throw new DroidmateException("Directory $dir doesn't exist or is not a directory. \n" +
        "Expected path: ${dir.absolutePath}");
  }

}
