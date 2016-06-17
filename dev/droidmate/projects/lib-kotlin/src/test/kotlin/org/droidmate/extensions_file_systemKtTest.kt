// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import java.nio.file.Paths

class extensions_file_systemKtTest {

  @Test
  fun withExtensionTest() {
    val fixture = Paths.get("/some/path/xyz.txt")
    
    // Act
    val actual = fixture.withExtension("newext")
   
    assertThat(actual.parent.toString(), equalTo(fixture.parent.toString()))
    assertThat(actual.fileName.toString(), equalTo("xyz.newext"))
  }
}



