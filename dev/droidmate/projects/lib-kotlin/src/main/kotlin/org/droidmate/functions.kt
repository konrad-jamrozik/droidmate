// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate

import com.konradjamrozik.Resource
import org.droidmate.common.BuildConstants
import java.nio.file.Path
import java.nio.file.Paths

val Resource.extractedPath: Path get() {
  val resDir = Paths.get(BuildConstants.getDir_name_temp_extracted_resources())
  return this.extractTo(resDir).toAbsolutePath()
}

val Resource.extractedPathString: String get() {
  return this.extractedPath.toString()
}

val Resource.text: String get() {
  return this.extractedPath.text
}