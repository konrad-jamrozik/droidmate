// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.common.Utils
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.DeviceGuiSnapshotVerificationException
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.VerifiableDeviceAction
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.device.datatypes.AppHasStoppedDialogBoxGuiState
import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.device.datatypes.IGuiState
import org.droidmate.exceptions.DeviceException

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.device.datatypes.AndroidDeviceAction.*

@Deprecated
@TypeChecked
@Slf4j
class VerifiableDeviceActionsExecutor implements IVerifiableDeviceActionsExecutor
{

  private final IValidDeviceGuiSnapshotProvider validGuiSnapshotProvider
  private final IExplorableAndroidDevice       device
  private final IApkExplorationOutput           explorationOutput
  private final Configuration                   config
  private final boolean                         logWidgets
  private final Reader                          userInputReader
  private final PrintWriter                     programOutputPrintWriter


  VerifiableDeviceActionsExecutor(IValidDeviceGuiSnapshotProvider validGuiSnapshotProvider,
                                  Configuration config,
                                  boolean logWidgets,
                                  IExplorableAndroidDevice device,
                                  IApkExplorationOutput explorationOutput,
                                  Reader userInputReader,
                                  PrintWriter programOutputPrintWriter)
  {
    this.validGuiSnapshotProvider = validGuiSnapshotProvider
    this.config = config
    this.logWidgets = logWidgets
    this.device = device
    this.explorationOutput = explorationOutput
    this.programOutputPrintWriter = programOutputPrintWriter
    this.userInputReader = userInputReader

  }

  @Override
  IGuiState executeAndVerify(VerifiableDeviceActions verifiableDeviceActions) throws DeviceException
  {
    assert verifiableDeviceActions?.size() > 0

    IGuiState currentGuiState = validGuiSnapshotProvider.getValidGuiSnapshot(device).guiState
    IDeviceGuiSnapshot lastExecutedAndVerifiedGuiSnapshot = null

    verifiableDeviceActions.each {VerifiableDeviceAction verifiableDeviceAction ->

      if (!isPressBackOrPressHomeActionOnHomeScreen(verifiableDeviceAction.action, currentGuiState))
      {
        lastExecutedAndVerifiedGuiSnapshot = performActionAndVerifyResultingGui(verifiableDeviceAction)
        currentGuiState = lastExecutedAndVerifiedGuiSnapshot.guiState
      } else
      {
        lastExecutedAndVerifiedGuiSnapshot = validGuiSnapshotProvider.getValidGuiSnapshot(device)
        if (verifiableDeviceAction.action == newPressBackDeviceAction())
          log.debug("'press back' device action on home screen was ignored")
        if (verifiableDeviceAction.action == newPressHomeDeviceAction())
          log.debug("'press home' device action on home screen was ignored")

      }
    }

    assert lastExecutedAndVerifiedGuiSnapshot.validationResult.valid
    explorationOutput.guiSnapshots << lastExecutedAndVerifiedGuiSnapshot
    return currentGuiState
  }

  @TypeChecked(SKIP)
  private IDeviceGuiSnapshot performActionAndVerifyResultingGui(VerifiableDeviceAction verifiableDeviceAction) throws DeviceException
  {
    verifiableDeviceAction.with {

      log.debug("Performing verifiable device action: ${description}")

      interactiveExplorationPrompt(getPerformActionPrompt(action))

      if (verifiableDeviceAction.description.startsWith(launchActivityDescriptionPrefix))
      {
        /* If we try to launch main activity and we get adb wrapper timeout, then lets try to close all "app has stopped"
        dialog boxes and try to relaunch the activity, multiple times. */
        Utils.attempt(5, log, 'Launching main activity', this.&getRidOfAppHasStoppedDialogBoxes) {
          device.perform(action)
          return true
        }
      } else
        device.perform(action)

      if (delay > 0)
      {
        log.trace("Delaying after performed action for ${delay}ms")
        sleep(delay)
      }
    }

    getRidOfAppHasStoppedDialogBoxes()

    interactiveExplorationPrompt(validGuiSnapshotPromptString)

    def guiSnapshot = validGuiSnapshotProvider.getValidGuiSnapshot(device)

    log.debug("Retrieved valid GUI snapshot to be verified: $guiSnapshot")
    if (logWidgets) {guiSnapshot.guiState.widgets.each {log.debug(it.toShortString())}}

    boolean verificationSucceeded = verifiableDeviceAction.verifierOfResultingGuiSnapshot.call(guiSnapshot)

    (guiSnapshot, verificationSucceeded) = waitForMainActivityToAppearIfNecessary(verificationSucceeded, verifiableDeviceAction, guiSnapshot)

    interactiveExplorationPrompt(getGuiSnapshotVerificationResultPrompt(verificationSucceeded))

    /* WISH This can be caused by some unknown case: the last line of the log given below means it is not home screen or any other known screen.
    WISH save the xml representation and screenshot of the failed screen.

    2015-02-22 05:24:13.827 TRACE o.d.e.VerifiableDeviceActionsExecutor    - Making 1/5 attempt at 'Launching main activity'
    2015-02-22 05:25:13.856 WARN  o.d.e.VerifiableDeviceActionsExecutor    - Got exception from failed 1/5 attempt of executing 'Launching main activity'. Exception msg: Executing 'adb shell am start <INTENT>' failed. Oh my. Please see .\dev1\logs\exceptions.txt log for details on the exception.
    2015-02-22 05:25:13.889 TRACE o.d.e.VerifiableDeviceActionsExecutor    - Recovering from a failed attempt 1/5 attempt at 'Launching main activity'
    2015-02-22 05:25:15.998 TRACE o.d.e.VerifiableDeviceActionsExecutor    - Making 2/5 attempt at 'Launching main activity'
    2015-02-22 05:25:29.281 DEBUG o.d.e.VerifiableDeviceActionsExecutor    - Retrieved valid GUI snapshot to be verified: Uia window dump of android.
    2015-02-22 05:25:29.375 DEBUG o.d.exploration.ApiLogcatLogsReader      - Current API logs read count: 64
    2015-02-22 05:25:29.452 ERROR o.d.e.ExplorationOutputCollector         - Abrupt exploration end. Caught exception thrown during exploration of com.cleanmaster.security. Exception message: Failed to verify the expected GUI snapshot after performing an action on device.
    The verifiable device action: <launch com.cleanmaster.security/ks.cm.antivirus.main.SplashActivity, expect app screen>
    The GUI snapshot that failed verification: Uia window dump of android.

    Also, from warnings.txt in the same run:

    2015-02-22 05:25:54.920 WARN  o.d.exploration.ExplorationExecutor      - An exploration process for com.ebay.mobile is about to start (next instruction: instantiating the data collector) but the device doesn't display home screen. Instead, its GUI state is: <GuiState pkg=android Widgets count = 22>. Continuing the exploration nevertheless, hoping that the first "reset app" exploration action will force the device into the home screen.
     */
    if (!verificationSucceeded)
    {
      def e = new DeviceGuiSnapshotVerificationException(verifiableDeviceAction, guiSnapshot)
      log.warn(e.message + "\nContinuing with exploration nonetheless")

    }

    // log.trace("Returning GUI snapshot resulting from performing verifiable device action.")
    return guiSnapshot
  }

  // This is needed for com.adobe.reader-inlined, if I remember correctly. Maybe com.ebay.mobile-inlined, but probably adobe.
  private List waitForMainActivityToAppearIfNecessary(boolean verificationSucceeded, VerifiableDeviceAction verifiableDeviceAction, IDeviceGuiSnapshot guiSnapshot)
  {
    // log.trace("Waiting for main activity to appear, if necessary.")
    int waitSeconds = 5
    if (!verificationSucceeded && verifiableDeviceAction.description.startsWith(VerifiableDeviceAction.launchActivityDescriptionPrefix) && guiSnapshot.guiState.homeScreen)
    {
      log.warn("Got home screen instead of a launched main activity. Waiting $waitSeconds seconds and retrying!")
      sleep(waitSeconds * 1000)
      guiSnapshot = validGuiSnapshotProvider.getValidGuiSnapshot(device)
      verificationSucceeded = verifiableDeviceAction.verifierOfResultingGuiSnapshot.call(guiSnapshot)
    }
    return [guiSnapshot, verificationSucceeded]
  }

  void getRidOfAppHasStoppedDialogBoxes()
  {
    // log.trace("Getting rid of 'app has stopped' dialog boxes, if necessary")

    def guiSnapshot = validGuiSnapshotProvider.getValidGuiSnapshot(device)
    if (guiSnapshot.guiState.isAppHasStoppedDialogBox())

      Utils.attempt(5, log, "getting rid of 'app has stopped' dialog boxes", null) {

        if (guiSnapshot.guiState.isAppHasStoppedDialogBox())
          device.perform(newClickGuiDeviceAction((guiSnapshot.guiState as AppHasStoppedDialogBoxGuiState).OKWidget))

        guiSnapshot = validGuiSnapshotProvider.getValidGuiSnapshot(device)
        device.clearLogcat()

        return !guiSnapshot.guiState.isAppHasStoppedDialogBox()
      }
  }

  private static boolean isPressBackOrPressHomeActionOnHomeScreen(IAndroidDeviceAction action, IGuiState gs)
  {
    assert action != null
    assert gs != null
    return (action == newPressBackDeviceAction() || action == newPressHomeDeviceAction()) && (gs.isHomeScreen())
  }

  private void interactiveExplorationPrompt(String promptMessage)
  {
    assert promptMessage?.size() > 0

    if (config.exploreInteractively)
    {
      programOutputPrintWriter.printf promptMessage
      userInputReader.readLine()
    }
  }

  protected static String getPerformActionPrompt(IAndroidDeviceAction action)
  {
    return "Following device action will be executed next:\n" +
      action.toString() + "\n" +
      "Press any key to continue.\n"
  }

  private static String validGuiSnapshotPromptString = "The device will be now queried for valid GUI snapshot.\n" +
    "Press any key to continue.\n"


  protected static String getGuiSnapshotVerificationResultPrompt(boolean verificationSucceeded)
  {
    return "The verification of GUI snapshot obtained after performing last device action returned value: " +
      "$verificationSucceeded\n" +
      "Press any key to continue.\n"

  }

}
