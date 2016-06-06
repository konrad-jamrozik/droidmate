// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report.chart

import com.konradjamrozik.Resource
import org.droidmate.common.BuildConstants
import org.junit.Test
import org.zeroturnaround.exec.ProcessExecutor
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class ChartingTest {
  
  @Test
  fun chartsWithGnuplot() {
    
    val processExecutor = ProcessExecutor()
      .exitValueNormal()
      .readOutput(true)
      .timeout(5, TimeUnit.SECONDS)
      .destroyOnExit()

    val plotTemplatePath = preparePlotTemplate()

    val variableBindings = "var_output_file_name='tmp_toremove';var_data_file_name='data.txt'"
    val result = processExecutor.command("gnuplot", "-e", variableBindings, plotTemplatePath).execute()
    println(result.outputString())
  }

  private fun preparePlotTemplate(): String {
    val resDir = Paths.get(BuildConstants.getDir_name_temp_extracted_resources())
    val plotTemplate = Resource("plot_template.plt").extractTo(resDir)
    val plotTemplatePath = plotTemplate.toAbsolutePath().toString()
    return plotTemplatePath
  }
}