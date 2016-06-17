// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import java.nio.file.Path

abstract class DataFile(val file: Path) : IDataFile, Path by file {

  override val path: Path get() = file
}