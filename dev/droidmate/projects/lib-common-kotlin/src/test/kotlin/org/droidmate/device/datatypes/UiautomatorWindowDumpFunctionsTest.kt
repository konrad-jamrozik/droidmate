// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import com.konradjamrozik.Resource
import org.junit.Test
import org.w3c.dom.Attr
import org.w3c.dom.Node
import org.xmlunit.builder.Input
import kotlin.test.assertFalse

class UiautomatorWindowDumpFunctionsTest {

  /* 
    Implemented with the help of: 
    http://stackoverflow.com/questions/141993/best-way-to-compare-2-xml-documents-in-java
    https://github.com/xmlunit/user-guide/wiki/Migrating-from-XMLUnit-1.x-to-2.x
    https://github.com/xmlunit/user-guide/wiki
  
   */
  @Test
  fun strips_avd_frame_from_gui_dump() {

    val dump_with_frame = Resource("fixtures/window_dumps/nexus7_2013_avd_api_23_home_dm_no_frame.xml").text
    val dump_raw = Resource("fixtures/window_dumps/nexus7_2013_avd_api_23_home_raw.xml").text

    // Act 
    val stripped_frame = stripAVDframe(dump_with_frame)

    val diff = org.xmlunit.builder.DiffBuilder
      .compare(Input.fromString(dump_raw))
      .withTest(Input.fromString(stripped_frame))
      .withAttributeFilter { attr: Attr -> attr.name != "bounds" }
      .withNodeFilter { node: Node -> depth(node) <= 9 }
      .ignoreWhitespace()
      .build()

    assertFalse(diff.hasDifferences(), diff.toString())
  }

  private fun depth(node: Node): Int {
    var depth = 1
    var currNode = node
    while (currNode.parentNode != null) {
      currNode = currNode.parentNode
      depth++
    }
    return depth
  }
}

