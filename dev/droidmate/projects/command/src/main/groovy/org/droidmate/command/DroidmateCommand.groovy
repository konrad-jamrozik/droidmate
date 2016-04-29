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
import org.droidmate.misc.TimeProvider

@Slf4j
abstract class DroidmateCommand
{

  abstract void execute(Configuration cfg) throws ThrowablesCollection

  public static DroidmateCommand build(
    boolean processUiaTestCasesLogs, boolean extractDataFromPreviousRun, boolean report, boolean inline, Configuration cfg)
  {
    assert [processUiaTestCasesLogs, extractDataFromPreviousRun, report, inline].count { it } <= 1

    if (processUiaTestCasesLogs)
      return ProcessUiaTestCasesLogsCommand.build(cfg)
    else if (extractDataFromPreviousRun)
      return ExtractDataFromPreviousRunCommand.build(cfg)
    else if (report)
      return new ReportCommand()
    else if (inline)
      return InlineCommand.build()
    else
      return ExploreCommand.build(new TimeProvider(), cfg)
  }
}
