// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.misc

import java.time.LocalDateTime

class TimeGenerator implements ITimeGenerator
{
  private LocalDateTime time = LocalDateTime.now()

  @Override
  LocalDateTime getNow()
  {
    return shiftAndGet(milliseconds: 10)
  }

  @Override
  LocalDateTime shiftAndGet(Map<String, Integer> timeShift)
  {
    assert timeShift?.milliseconds != null

    time = time.plusNanos(timeShift.milliseconds * 1000000)

    return time
  }
}

