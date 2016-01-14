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

class TimeProvider implements ITimeProvider
{
  @Override
  LocalDateTime getNow()
  {
    return LocalDateTime.now()
  }
}
