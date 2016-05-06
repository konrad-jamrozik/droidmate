// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.google.common.jimfs.Jimfs
import java.nio.file.FileSystem

fun mockFs(): FileSystem = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix())