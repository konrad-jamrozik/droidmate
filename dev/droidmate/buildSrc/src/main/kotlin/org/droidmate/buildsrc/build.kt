// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.buildsrc

import com.github.konrad_jamrozik.asEnvDir
import com.github.konrad_jamrozik.resolveRegularFile

val jdk6rtJar = "JAVA6_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
val jdk7rtJar = "JAVA7_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
val androidSdkDir = "ANDROID_HOME".asEnvDir

