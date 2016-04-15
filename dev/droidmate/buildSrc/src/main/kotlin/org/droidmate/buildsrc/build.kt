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

// KJA rename: use underscores
val jdk6rtJar = "JAVA6_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
// KJA rename: use underscores
val jdk7rtJar = "JAVA7_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
// KJA rename: use underscores
val androidSdkDir = "ANDROID_HOME".asEnvDir

val android_platform_version = "19"
val build_tools_version = "19.1.0"
// KJA regular dir, not file
val android_platform_dir = androidSdkDir.resolve("platforms/android-$android_platform_version")
val android_jar = android_platform_dir.resolveRegularFile("android.jar")
