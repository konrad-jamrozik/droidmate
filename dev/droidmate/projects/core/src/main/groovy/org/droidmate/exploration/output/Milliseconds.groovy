// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

class Milliseconds implements IMilliseconds
{
  int value
  int tickSizeInMs

  private final int printWidth

  Milliseconds(int value, int printWidth, int tickSizeInMs)
  {
    this.printWidth = printWidth
    this.value = value
    this.tickSizeInMs = tickSizeInMs
  }

  @Override
  public String print()
  {
    return sprintf("%$printWidth.1f", ((double) value) / 1000)
  }
}
