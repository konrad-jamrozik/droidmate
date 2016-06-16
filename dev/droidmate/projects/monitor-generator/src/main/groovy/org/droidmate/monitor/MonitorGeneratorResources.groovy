// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor

import com.konradjamrozik.ResourcePath
import org.droidmate.common.BuildConstants
import org.droidmate.common.IConfiguration

import java.nio.file.Path
import java.nio.file.Paths

import static java.nio.file.Files.isWritable
import static java.nio.file.Files.notExists

@SuppressWarnings("GrFinalVariableAccess")
class MonitorGeneratorResources implements IConfiguration
{

  final Path monitorSrcTemplatePath
  final Path monitorSrcOutPath
  final Path appguardApis

  final Path jellybeanPublishedApiMapping
  final Path jellybeanStaticMethods
  final Path appguardLegacyApis

  final AndroidAPI androidApi

  MonitorGeneratorResources(String[] args)
  {
    if (args.contains("api19"))
      this.androidApi = AndroidAPI.API_19
    else if (args.contains("api23"))
      this.androidApi = AndroidAPI.API_23
    else
      throw new IllegalStateException()

    Path monitorSrcOut

    if (this.androidApi == AndroidAPI.API_19)
    {
      monitorSrcOut = Paths.get(BuildConstants.monitor_generator_output_relative_path_api19)
    } else if (this.androidApi == AndroidAPI.API_23)
    {
      monitorSrcOut = Paths.get(BuildConstants.monitor_generator_output_relative_path_api23)
    } else
      throw new IllegalStateException()

    assert monitorSrcOut != null
    assert notExists(monitorSrcOut) || isWritable(monitorSrcOut)
    this.monitorSrcOutPath = monitorSrcOut

    Path monitorSrcTemplatePath = new ResourcePath(BuildConstants.monitor_generator_res_name_monitor_template).path
    this.monitorSrcTemplatePath = monitorSrcTemplatePath

    Path appguardApis = new ResourcePath("appguard_apis.txt").path
    this.appguardApis = appguardApis

    Path jellybeanPublishedApiMapping = new ResourcePath("legacy/jellybean_publishedapimapping_modified.txt").path
    this.jellybeanPublishedApiMapping = jellybeanPublishedApiMapping

    Path jellybeanStaticMethods = new ResourcePath("legacy/jellybean_publishedapimapping_static_methods_list.txt").path
    this.jellybeanStaticMethods = jellybeanStaticMethods

    Path appguardLegacyApis = new ResourcePath("legacy/appguard_legacy_apis.txt").path
    this.appguardLegacyApis = appguardLegacyApis
  }
}