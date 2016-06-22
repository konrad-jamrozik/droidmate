// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import org.junit.Test
import kotlin.test.assertEquals

class extensions_collectionsKtTest {

  @Test
  fun uniqueItemsWithFirstOccurrenceIndexTest() {

    // KJA test: uniqueItemsWithFirstOccurrenceIndexTest
    val inputDataFixture = emptyList<String>()
    val expected = emptyMap<String, Int>()
    
    // Act
    val actual: Map<String, Int> = inputDataFixture.uniqueItemsWithFirstOccurrenceIndex(
      extractItems = { emptyList<String>() }, 
      extractUniqueString = { it }
    )
    assertEquals(expected, actual)
  }
}

