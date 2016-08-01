// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate

import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class TimeDiffWithToleranceTest {
  @Test
  fun `warnIfBeyond test`() {

    val diff = TimeDiffWithTolerance(Duration.ofMillis(3000))

    // Act
    val result1 = diff.warnIfBeyond(LocalDateTime.now(), LocalDateTime.now().minusSeconds(4), "First element", "2nd item")
    val result2 = diff.warnIfBeyond(LocalDateTime.now(), LocalDateTime.now().minusSeconds(2), "Item1", "Thing2")

    assertThat(result1, equalTo(true))
    assertThat(result2, equalTo(false))
  }

}