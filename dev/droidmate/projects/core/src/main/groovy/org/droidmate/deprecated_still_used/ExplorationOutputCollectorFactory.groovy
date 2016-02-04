// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.deprecated_still_used

import org.droidmate.misc.ITimeProvider

@Deprecated
class ExplorationOutputCollectorFactory implements IExplorationOutputCollectorFactory
{

  private final ITimeProvider timeProvider
  private final IStorage      storage

  ExplorationOutputCollectorFactory(ITimeProvider timeProvider, IStorage storage)
  {
    this.storage = storage
    this.timeProvider = timeProvider
  }

  @Override
  IExplorationOutputCollector create(String appPackageName)
  {
    return new ExplorationOutputCollector(appPackageName, timeProvider, storage)
  }
}
