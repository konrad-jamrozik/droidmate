// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.util.logging.Slf4j
import org.droidmate.common.Assert
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.misc.ITimeProvider

import static org.droidmate.common.logging.Markers.exceptions

// KJA to remove next
@Slf4j
@Deprecated
class ExplorationOutputCollector implements IExplorationOutputCollector
{
  private final String appPackageName
  private final ITimeProvider timeProvider
  private final IStorage      storage

  // WISH add field: apk file name string (if multiple versions of same app)


  ExplorationOutputCollector(String appPackageName, ITimeProvider timeProvider, IStorage storage)
  {
    this.timeProvider = timeProvider
    this.appPackageName = appPackageName
    this.storage = storage
  }

  @Override
  IApkExplorationOutput collect(Closure targetClosure)
  {
    return collect(null as String, targetClosure)
  }

  @Override
  IApkExplorationOutput collect(String uiaTestCaseName, Closure targetClosure)
  {
    Assert.checkClosureParameterSignatures(targetClosure, IApkExplorationOutput)
    IApkExplorationOutput collectedOutput = ApkExplorationOutput.create(appPackageName: appPackageName, uiaTestCaseName: uiaTestCaseName)
    assert (uiaTestCaseName != null) == (collectedOutput.annotations != null)
    assert collectedOutput?.annotations?.testCaseName == uiaTestCaseName

    try
    {
      targetClosure(collectedOutput)
      collectedOutput.completed = true

    } catch (Exception e)
    {
      collectedOutput.caughtException = e
      log.error("Abrupt exploration end. Caught exception thrown during exploration of $appPackageName. Exception message: ${e.message}\n" +
        "Serializing exploration output.\n$LogbackConstants.err_log_msg")
      log.error(exceptions, "$appPackageName processing failed with an exception:\n", e)
    }
    finally
    {
      assert collectedOutput != null
      assert (uiaTestCaseName != null) == collectedOutput.isUiaTestCase

      // The field might be non-null if the exception was thrown after the field was assigned, i.e. after the exploration loop.
      if (collectedOutput.explorationEndTime == null)
        collectedOutput.explorationEndTime = timeProvider.now
      collectedOutput.verifyCompletedDataIntegrity()

      if (uiaTestCaseName == null)
      {
        // WISH include in the name the amount of time that has passed.
        String exceptionSuffix = collectedOutput.caughtException != null ? "_collection_exception" : ""
        storage.serialize(collectedOutput, exceptionSuffix)
      }
      // WISH uncomment after the wish above is fulfilled.
      // storage.delete("$ExplorationExecutor.interimPart")
    }

    return collectedOutput
  }

}
