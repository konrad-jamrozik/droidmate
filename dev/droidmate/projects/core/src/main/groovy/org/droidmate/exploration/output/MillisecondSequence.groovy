// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

class MillisecondSequence implements IPrintable
{
  @Delegate
  List<IMilliseconds> millisecondSequence

  private final int timeTicks
  private final int timeTickSize

  MillisecondSequence(int timeTicks, int timeTickSizeInMs)
  {
    this.timeTicks = timeTicks
    this.timeTickSize = timeTickSizeInMs
    this.millisecondSequence = (0..timeTicks).collect { new Milliseconds(it * timeTickSizeInMs, this.print().size(), timeTickSizeInMs)}
  }

  @Override
  public String print()
  {
    return "seconds_passed"
  }
}
