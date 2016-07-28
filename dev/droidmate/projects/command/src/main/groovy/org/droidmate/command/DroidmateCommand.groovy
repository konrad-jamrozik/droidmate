// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command

import groovy.util.logging.Slf4j
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.ThrowablesCollection

@Slf4j
abstract class DroidmateCommand
{

  abstract void execute(Configuration cfg) throws ThrowablesCollection

  public static DroidmateCommand build(
    boolean report, boolean inline, Configuration cfg)
  {
    assert [report, inline].count {it} <= 1

    if (report)
      return new ReportCommand()
    else if (inline)
      return InlineCommand.build()
    else
      return ExploreCommand.build(cfg)
  }
}
