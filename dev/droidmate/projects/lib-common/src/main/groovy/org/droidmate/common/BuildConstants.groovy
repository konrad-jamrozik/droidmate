// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.common

import com.konradjamrozik.ResourcePath

class BuildConstants
{

  static Map<String, String> properties = loadProperties("buildConstants.properties")

  static String apk_inliner_param_input              = safeGetProperty(properties, "apk_inliner_param_input")
  static String apk_inliner_param_output_dir         = safeGetProperty(properties, "apk_inliner_param_output_dir")
  static String apk_inliner_param_input_default      = safeGetProperty(properties, "apk_inliner_param_input_default")
  static String apk_inliner_param_output_dir_default = safeGetProperty(properties, "apk_inliner_param_output_dir_default")
  static String AVD_dir_for_temp_files               = safeGetProperty(properties, "AVD_dir_for_temp_files")
  static String jarsigner                            = safeGetProperty(properties, "jarsigner")
  static String apk_fixtures                         = safeGetProperty(properties, "apk_fixtures")

  private static Map<String, String> loadProperties(String fileName)
  {
    String text = new ResourcePath(fileName).path.text
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
