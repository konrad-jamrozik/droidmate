// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor

import java.nio.file.Files
import java.nio.file.Path

class MonitorSrcFile
{

 MonitorSrcFile(Path path, String contents)
 {
   assert contents?.size() > 0
   assert path != null
   Path absolutePath = path.toAbsolutePath()
   assert Files.notExists(absolutePath) || Files.isWritable(absolutePath)
   assert absolutePath.fileName.toString().endsWith(".java")

   Files.createDirectories(absolutePath.parent)
   Files.write(absolutePath, contents.getBytes())
 }
}
