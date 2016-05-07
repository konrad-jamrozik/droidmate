// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
import org.droidmate.test_base.FilesystemTestFixtures
import org.junit.Test
import kotlin.test.assertEquals

class FunctionsTest {

  @Test
  fun strips_avd_frame_from_gui_dump() {

    val fixtures = FilesystemTestFixtures.build()
    val dump_with_frame = fixtures.windowDumps.f_nexus7_2013_avd_api_23_home_dm_no_frame
    val dump_without_frame = fixtures.windowDumps.f_nexus7_2013_avd_api_23_home_raw
    
    // KJA current work
    assertEquals(dump_without_frame, stripAVDframe(dump_with_frame))
  }
}

