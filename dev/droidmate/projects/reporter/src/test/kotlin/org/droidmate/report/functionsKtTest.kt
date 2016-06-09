// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.konradjamrozik.Resource
import org.droidmate.common.BuildConstants
import org.junit.Test
import java.nio.file.Paths

class functionsKtTest {

  @Test
  fun plots() {
    
    val testTempDir = Paths.get(BuildConstants.getTest_temp_dir_name())
    plot(dataFilePath = Resource("plot_test_data_fixture.txt").extractTo(testTempDir).toAbsolutePath().toString(),
      outputFilePath = testTempDir.resolve("plot_test_output.pdf").toAbsolutePath().toString())
  }
}