// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.deprecated

import com.google.common.base.Stopwatch
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.IStorage
import org.droidmate.misc.ITimeProvider

import java.util.concurrent.TimeUnit

@Deprecated
class IntermediateOutputSaver implements IIntermediateOutputSaver
{

  private static final String interimPart  = "_interim_"
  private static final int    saveInterval = 15

  private final ITimeProvider timeProvider
  private final IStorage      storage

  Stopwatch intermediateSaveStopwatch
  int       intermediateSavesCount

  IntermediateOutputSaver(ITimeProvider timeProvider, IStorage storage)
  {
    this.timeProvider = timeProvider
    this.storage = storage
  }

  @Override
  void init()
  {
    this.intermediateSaveStopwatch = Stopwatch.createStarted()
    this.intermediateSavesCount = 0
  }

  @Override
  boolean save(IApkExplorationOutput output)
  {
    if (intermediateSaveStopwatch.elapsed(TimeUnit.MINUTES) >= saveInterval)
    {
      intermediateSaveStopwatch.reset()
      intermediateSaveStopwatch.start()
      intermediateSavesCount++
      output.explorationEndTime = timeProvider.now
      storage.serialize(output, "$interimPart${(intermediateSavesCount) * saveInterval}min")

      if (intermediateSavesCount > 1)
        storage.delete("$interimPart${(intermediateSavesCount - 1) * saveInterval}min")
      return true

    } else
      return false
  }
}
