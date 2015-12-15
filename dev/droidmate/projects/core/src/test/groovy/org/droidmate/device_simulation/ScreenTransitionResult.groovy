// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import com.google.common.base.MoreObjects
import groovy.transform.Canonical
import org.droidmate.logcat.ITimeFormattedLogcatMessage

@Canonical
class ScreenTransitionResult implements IScreenTransitionResult
{

  IGuiScreen                             screen
  ArrayList<ITimeFormattedLogcatMessage> logs

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
      .add("logs", logs*.messagePayload.collect { it.truncate(100) })
      .add("screen", screen)
      .toString()
  }
}
