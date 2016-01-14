// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.common.logging.Markers
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.MissingGuiSnapshot
import org.droidmate.device.datatypes.ValidationResult
import org.droidmate.exceptions.UiautomatorWindowDumpValidationException

@Deprecated
@TypeChecked
@Slf4j
class ValidUiautomatorWindowDumpProvider implements IValidDeviceGuiSnapshotProvider
{

  private int attemptsToObtainValidWindowDump
  private int delayBetweenAttemptsToObtainValidGuiSnapshot // ms

  private int attemptsLeft


  ValidUiautomatorWindowDumpProvider(
    int attemptsToObtainValidWindowDump,
    int delayBetweenAttemptsToObtainValidGuiSnapshot)
  {
    this.attemptsToObtainValidWindowDump = attemptsToObtainValidWindowDump
    this.delayBetweenAttemptsToObtainValidGuiSnapshot = delayBetweenAttemptsToObtainValidGuiSnapshot
    assert this.attemptsToObtainValidWindowDump >= 1
    assert this.delayBetweenAttemptsToObtainValidGuiSnapshot >= 0
  }

  @Override
  IDeviceGuiSnapshot getValidGuiSnapshot(IExplorableAndroidDevice device) throws UiautomatorWindowDumpValidationException
  {
    // log.debug("Getting valid GUI snapshot")
    attemptsLeft = attemptsToObtainValidWindowDump
    attemptsLeft--
    assert attemptsLeft >= 0


    IDeviceGuiSnapshot snapshot = device.getGuiSnapshot()
    ValidationResult vres = snapshot.validationResult

    while (!vres.valid && attemptsLeft > 0)
    {
      log.debug("Failed to obtain a valid GUI snapshot. Validation failure reason: ${vres.description}. " +
        "Making next attempt out of remaining $attemptsLeft.")

      attemptsLeft--
      waitForNextValidGuiSnapshotAttempt()
      snapshot = device.getGuiSnapshot()
      vres = snapshot.validationResult
    }
    assert attemptsLeft >= 0

    if (!vres.valid)
    {
      assert attemptsLeft == 0

      log.warn("Failed to obtain a valid GUI snapshot. Exhausted all attempts. Validation failure reason: ${vres.description}. " +
        "Returning synthetic 'missing GUI snapshot'. $LogbackConstants.err_log_msg")
      log.error(Markers.exceptions, "Exhausted all attempts at getting valid GUI snapshot:\n", new UiautomatorWindowDumpValidationException(snapshot.windowHierarchyDump, vres.description))

      return new MissingGuiSnapshot()
    }

    assert snapshot?.validationResult?.valid
    return snapshot
  }

  private void waitForNextValidGuiSnapshotAttempt()
  {
    Thread.sleep(delayBetweenAttemptsToObtainValidGuiSnapshot)
  }

  private static void logValidationErrorAndThrowException(String windowHierarchyDump, String exceptionMessage)
  {
    def ex = new UiautomatorWindowDumpValidationException(windowHierarchyDump, exceptionMessage)
    log.error("Throwing $ex")
    throw ex
  }
}
