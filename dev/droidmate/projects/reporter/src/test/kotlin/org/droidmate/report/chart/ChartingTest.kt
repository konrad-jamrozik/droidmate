// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report.chart

import org.junit.Test
import javax.swing.JFrame

class ChartingTest {

  @Test
  fun charts() {
    val frame = LinePlotTest()
    frame.isVisible = true
    System.`in`.read()
  }
}

class LinePlotTest : JFrame() {
  init {
    defaultCloseOperation = EXIT_ON_CLOSE;
    setSize(800, 600);
    // Insert rest of the code here
  }
}