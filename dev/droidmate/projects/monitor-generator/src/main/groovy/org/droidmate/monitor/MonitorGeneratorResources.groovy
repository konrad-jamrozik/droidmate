// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

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
  }
}