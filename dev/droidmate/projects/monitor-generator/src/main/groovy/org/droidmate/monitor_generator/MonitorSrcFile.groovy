// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor_generator

import java.nio.file.Files
import java.nio.file.Path

class MonitorSrcFile
{

 MonitorSrcFile(Path path, String contents)
 {
   assert contents?.size() > 0
   assert path != null
   assert Files.notExists(path) || Files.isWritable(path)
   assert path.fileName.toString().endsWith(".java")

   Files.createDirectories(path.parent)
   Files.write(path, contents.getBytes())
 }
}
