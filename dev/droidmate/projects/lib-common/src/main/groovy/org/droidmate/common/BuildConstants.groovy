// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.common

import com.konradjamrozik.Resource
import com.konradjamrozik.ResourcePath

/**
 * This class contains fields whose values are necessary both by the compiled classes and by gradle build scripts compiling the 
 * classes.
 *
 * The values of these fields come originally from the Gradle-special "buildSrc" project. The values have to be copied here, as 
 * buildSrc is not distributed with the binary and thus any dependencies on it from the compiled classes would cause runtime 
 * "NoClassDefFoundError" error.
 */
class BuildConstants
{

  static Map<String, String> properties = loadProperties("buildConstants.properties")

  static String aapt_command                                = safeGetProperty(properties, "aapt_command")
  static String adb_command                                 = safeGetProperty(properties, "adb_command")
  static String apk_fixtures                                = safeGetProperty(properties, "apk_fixtures")
  static String apk_inliner_param_input                     = safeGetProperty(properties, "apk_inliner_param_input")
  static String apk_inliner_param_output_dir                = safeGetProperty(properties, "apk_inliner_param_output_dir")
  static String apk_inliner_param_input_default             = safeGetProperty(properties, "apk_inliner_param_input_default")
  static String apk_inliner_param_output_dir_default        = safeGetProperty(properties, "apk_inliner_param_output_dir_default")
  static String apks_dir                                    = safeGetProperty(properties, "apks_dir")
  static String appguard_apis_txt                           = safeGetProperty(properties, "appguard_apis_txt")
  static String AVD_dir_for_temp_files                      = safeGetProperty(properties, "AVD_dir_for_temp_files")
  static String jarsigner                                   = safeGetProperty(properties, "jarsigner")
  static String monitor_generator_res_name_monitor_template = safeGetProperty(properties, "monitor_generator_res_name_monitor_template")
  static String monitor_generator_output_relative_path      = safeGetProperty(properties, "monitor_generator_output_relative_path")
  static String monitored_inlined_apk_fixture_name          = safeGetProperty(properties, "monitored_inlined_apk_fixture_name")
  static String test_temp_dir_name                          = safeGetProperty(properties, "test_temp_dir_name")

  private static Map<String, String> loadProperties(String fileName)
  {
    String text = new Resource(fileName, /* allowAmbiguity */ true).text
    Map<String, String> out = [:]
    text.splitEachLine("=") {assert it.size() == 2; out.put(it[0], it[1])}
    return out
  }

  private static String safeGetProperty(Map<String, String> properties, String key)
  {
    assert properties.containsKey(key)
    String value = properties[key]
    assert value?.size() > 0
    return value
  }
}
