// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

// "unused" warning is suppressed because vals in this project are being used in the 'droidmate' project gradle build scripts
// as well as in the 'droidmate' project itself.
@file:Suppress("unused")

package org.droidmate.buildsrc

import com.konradjamrozik.OS
import com.konradjamrozik.asEnvDir
import com.konradjamrozik.resolveDir
import com.konradjamrozik.resolveRegularFile
import org.zeroturnaround.exec.ProcessExecutor
import java.io.ByteArrayOutputStream
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

private val exeExt = if (OS.isWindows) ".exe" else ""

//region Values directly based on system environment variables
val java_home = "JAVA_HOME".asEnvDir
private val android_sdk_dir = "ANDROID_HOME".asEnvDir
//endregion

val jarsigner_relative_path = "bin/jarsigner$exeExt"
val jarsigner = java_home.resolveRegularFile(jarsigner_relative_path)

//region Android SDK components
private val build_tools_version = "23.0.3"
val aapt_command_relative = "build-tools/$build_tools_version/aapt$exeExt"
val adb_command_relative = "platform-tools/adb$exeExt"
val aapt_command = android_sdk_dir.resolveRegularFile(aapt_command_relative)
val adb_command = android_sdk_dir.resolveRegularFile(adb_command_relative)
private val android_platform_version = "23"
private val android_platform_dir = android_sdk_dir.resolveDir("platforms/android-$android_platform_version")
val uiautomator_jar = android_platform_dir.resolveRegularFile("uiautomator.jar")
val android_jar = android_platform_dir.resolveRegularFile("android.jar")
//endregion

val monitor_generator_res_name_monitor_template = "monitorTemplate.txt"
val monitor_generator_output_relative_path = "temp/generated_Monitor.java"

val apk_inliner_param_input_default = Paths.get("input-apks")
val apk_inliner_param_output_dir_default = Paths.get("output-apks")
val apk_inliner_param_input = "-input"
val apk_inliner_param_output_dir = "-outputDir"
val AVD_dir_for_temp_files = "/data/local/tmp/"

/** This string makes an assumption that the apk inliner adds the "-inlined" suffix. */
val monitored_inlined_apk_fixture_name = "MonitoredApkFixture-debug-inlined.apk"
/**
 * Denotes name of directory containing apk fixtures for testing. The handle to this path is expected to be obtained
 * in following ways:
 *
 * From a build.gradle script:
 *
 *   new File(sourceSets.test.resources.srcDirs[0], <this_var_reference>)
 *
 * From compiled source code:
 *
 *   Paths.get(ClassLoader.getSystemResource(<this_var_reference>).toUri())
 */
val apk_fixtures = "fixtures/apks"

val test_temp_dir_name = "temp_dir_for_tests"

val appguard_apis_txt = "appguard_apis.txt"

/**
 * Directory for resources extracted from jars in the classpath.
 *
 * Some resources have to be extracted to a directory. For example, an .apk file that is inside a .jar needs to be pushed
 * to a device.
 */
val dir_name_temp_extracted_resources = "temp_extracted_resources"

fun executeCommand(commandName: String, commandContent: String): Int {

  val cmd = if (OS.isWindows) "cmd /c " else ""
  val commandString = cmd + commandContent

  println("=========================")
  println("Executing command named: $commandName")
  println("Command string:")
  println(commandString)

  val err = ByteArrayOutputStream()
  val out = ByteArrayOutputStream()
  val process = ProcessExecutor()
    .readOutput(true)
    .redirectOutput(out)
    .redirectError(err)
    .timeout(120, TimeUnit.SECONDS)

  print("executing...")
  val result = process.commandSplit(commandString).execute()
  println(" DONE")

  println("return code: ${result.exitValue}")
  val stderrContent = err.toString(Charsets.UTF_8.toString())
  val stdoutContent = out.toString(Charsets.UTF_8.toString())

  if (stderrContent != "") {
    println("----------------- stderr:")
    println(stderrContent)
    println("----------------- /stderr")
  } else
    println("stderr is empty")

  if (stdoutContent != "") {
    if (result.exitValue == 0)
      println("stdout is ${stdoutContent.length} chars long")
    else {
      println("----------------- stdout:")
      println(stdoutContent)
      println("----------------- /stderr")
    }
  } else
    println("stdout is empty")

  println("=========================")
  return result.exitValue
}