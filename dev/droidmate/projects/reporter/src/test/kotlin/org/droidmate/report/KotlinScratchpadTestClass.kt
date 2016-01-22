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
import java.util.*

class KotlinScratchpadTestClass {

  @Test
  fun KotlinScratchpadTest() {

    val l: MutableList<Int> = ArrayList(listOf(1,2,3))
    l.add(4)
    val l2 = l.toList()
    l.add(5)
    l2.forEach { println(it) }
  }
}