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
import com.github.konrad_jamrozik.resolveDir
import com.github.konrad_jamrozik.resolveRegularFile

val jdk6_rt_jar = "JAVA6_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
val jdk7_rt_jar = "JAVA7_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
val android_sdk_dir = "ANDROID_HOME".asEnvDir

val android_platform_version = "19"
val build_tools_version = "19.1.0"
val android_platform_dir = android_sdk_dir.resolveDir("platforms/android-$android_platform_version")
val android_jar = android_platform_dir.resolveRegularFile("android.jar")
