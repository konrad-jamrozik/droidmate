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
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartFrame
import org.jfree.chart.util.ExportUtils
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.junit.Test
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.VectorGraphicsEncoder
import org.knowm.xchart.XYChart
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.style.Styler
import org.zeroturnaround.exec.ProcessExecutor
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class ChartingTest {

  @Test
  fun chartsWIthXChart() {

    val xData: DoubleArray = doubleArrayOf(0.0, 1.0, 2.0)
    val yData: DoubleArray = doubleArrayOf(2.0, 1.0, 0.0)

    val chart: XYChart = XYChartBuilder()
      .theme(Styler.ChartTheme.Matlab)
      .xAxisTitle("Xaxis")
      .yAxisTitle("Yaxis")
      .build()

    chart.addSeries("ser1", xData, yData)

    VectorGraphicsEncoder.saveVectorGraphic(
      chart, "./Sample_Chart",
      VectorGraphicsEncoder.VectorGraphicsFormat.PDF);

    val manualInspection = false
    if (manualInspection) {
      SwingWrapper(chart).displayChart();
      System.`in`.read()
    }
  }

  /**
   * Example code: https://github.com/jfree/jfreechart/blob/master/src/main/java/org/jfree/chart/demo/TimeSeriesChartDemo1.java#L70
   * Javadoc: http://www.jfree.org/jfreechart/api/javadoc/index.html
   */
  @Test
  fun chartsWithJFreeChart() {

    val ser1 = XYSeries("xyseries_name")
    ser1.add(0, 3)
    ser1.add(1, 6)
    ser1.add(2, 10)
    ser1.add(3, 8)

    val dataset = XYSeriesCollection(ser1)
    val chart = ChartFactory.createTimeSeriesChart("Chart title", "X axis label", "Y axis label", dataset)

    ExportUtils.writeAsPDF(chart, 900, 600, File("jfreechart.pdf"))
    
    val manualInspection = false
    if (manualInspection) {
      val frame = ChartFrame("frame title", chart)
      frame.size = java.awt.Dimension(1500, 1000)
      frame.isVisible = true
      System.`in`.read()
    }
  }
  
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