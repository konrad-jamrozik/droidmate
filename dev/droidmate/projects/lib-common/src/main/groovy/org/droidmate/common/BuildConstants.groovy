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
  static Properties properties = loadProperties("buildConstants.properties")

  static String apk_inliner_param_input      = safeGetProperty(properties, "apk_inliner_param_input")
  static String apk_inliner_param_output_dir = safeGetProperty(properties, "apk_inliner_param_output_dir")

  private static Properties loadProperties(String fileName)
  {
    Properties properties = new Properties()
    new ResourcePath(fileName).path.withInputStream {properties.load(it)}
    return properties
  }

  private static String safeGetProperty(Properties properties, String key)
  {
    String value = properties.getProperty(key)
    assert value?.size() > 0
    return value
  }
}
