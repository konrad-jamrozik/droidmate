// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

// "unused" warning is suppressed because vals in this project are being used in the 'droidmate' project gradle build scripts
// as well as in the projects builts by it.
@file:Suppress("unused")

package org.droidmate.buildsrc

import com.github.konrad_jamrozik.OS
import com.github.konrad_jamrozik.asEnvDir
import com.github.konrad_jamrozik.resolveDir
import com.github.konrad_jamrozik.resolveRegularFile

private val android_platform_version = "19"
private val build_tools_version = "19.1.0"
private val exeExt = if (OS.isWindows) ".exe" else ""

//region Values based directly on system environment variables
val jarsigner = "JAVA8_HOME".asEnvDir.resolveRegularFile("bin/jarsigner$exeExt")
val jdk7_rt_jar = "JAVA7_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
val jdk6_rt_jar = "JAVA6_HOME".asEnvDir.resolveRegularFile("jre/lib/rt.jar")
private val android_sdk_dir = "ANDROID_HOME".asEnvDir
//endregion

//region Android SDK components
val aapt_command = android_sdk_dir.resolveRegularFile("build-tools/$build_tools_version/aapt$exeExt")
val adb_command = android_sdk_dir.resolveRegularFile("platform-tools/adb$exeExt")
private val android_platform_dir = android_sdk_dir.resolveDir("platforms/android-$android_platform_version")
var uiautomator_jar = android_platform_dir.resolveRegularFile("uiautomator.jar")
val android_jar = android_platform_dir.resolveRegularFile("android.jar")
//endregion
