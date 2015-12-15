// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logcat

import org.droidmate.common.exploration.datatypes.Widget

public interface IUiaTestActionLogcatMessage extends ITimeFormattedLogcatMessage
{
  String getActionType()
  String getComment()
  Widget getWidget()
}
