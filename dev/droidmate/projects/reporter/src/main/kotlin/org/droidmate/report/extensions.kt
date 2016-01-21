// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.collect.Table
import java.nio.file.Files
import java.nio.file.Path

fun <R, C, V> Table<R, C, V>.writeOut(file: Path) {
  val cellsString = this.cellSet().joinToString { it.toString() }
  Files.write(file, cellsString.toByteArray())
}