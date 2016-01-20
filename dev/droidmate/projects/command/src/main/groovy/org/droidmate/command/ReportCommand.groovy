// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command

import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.ThrowablesCollection
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.exploration.output.DroidmateOutputDir
import org.droidmate.report.ExplorationOutput2Report

class ReportCommand extends DroidmateCommand
{


  @Override
  void execute(Configuration cfg) throws ThrowablesCollection
  {
    // KJA current work
    ExplorationOutput2 out = new DroidmateOutputDir(cfg.droidmateOutputDirPath).readOutput()
    new ExplorationOutput2Report(out).report()
  }
}
