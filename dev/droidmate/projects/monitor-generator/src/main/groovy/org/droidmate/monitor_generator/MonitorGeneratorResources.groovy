// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor_generator

import com.github.konrad_jamrozik.ResourcePath
import org.droidmate.common.IConfiguration
import org.droidmate.init.InitConstants

import java.nio.file.Path

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

  MonitorGeneratorResources()
  {
    Path monitorSrcOut = InitConstants.monitor_generator_generated_monitor
    assert monitorSrcOut != null
    assert notExists(monitorSrcOut) || isWritable(monitorSrcOut)
    this.monitorSrcOutPath = monitorSrcOut

    Path monitorSrcTemplatePath = new ResourcePath(InitConstants.monitor_generator_res_name_monitor_template).path
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
