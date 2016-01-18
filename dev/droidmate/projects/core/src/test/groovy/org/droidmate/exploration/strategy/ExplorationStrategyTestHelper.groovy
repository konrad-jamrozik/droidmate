// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.strategy

import org.droidmate.configuration.Configuration
import org.droidmate.test_helpers.configuration.ConfigurationForTests

class ExplorationStrategyTestHelper
{

  static IExplorationStrategy buildStrategy(
    String pkgName, Integer actionsLimit, Integer resetEveryNthExplorationForward)
  {
    Configuration cfg = new ConfigurationForTests().setArgs([
      Configuration.pn_actionsLimit, "$actionsLimit",
      Configuration.pn_resetEveryNthExplorationForward, "$resetEveryNthExplorationForward",
    ]).get()

    IExplorationStrategy strategy = ExplorationStrategy.build(pkgName, cfg)
    return strategy

  }
}
