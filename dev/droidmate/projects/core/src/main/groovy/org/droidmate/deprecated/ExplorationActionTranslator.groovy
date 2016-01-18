// Copyright (c) 2012-2016 Saarland University
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
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.*

import static VerifiableDeviceActions.newEmptyVerifiableDeviceActions
import static org.droidmate.deprecated_still_used.VerifiableDeviceAction.*

// WISH the functionality of this class should be encapsulated in ExplorationAction.toVerifiableDeviceActions(). Maybe this class will become a subcomponent of ExplorationAction.
@TypeChecked
@Slf4j
@Deprecated
class ExplorationActionTranslator implements IExplorationActionToVerifiableDeviceActionsTranslator
{
  private final String appPackageName
  private final String appLaunchableActivityComponentName
  private final boolean softReset


  ExplorationActionTranslator(
    String appPackageName,
    String appLaunchableActivityComponentName,
    boolean softReset)
  {
    this.appPackageName = appPackageName
    this.appLaunchableActivityComponentName = appLaunchableActivityComponentName
    this.softReset = softReset
  }

  @Override
  VerifiableDeviceActions translate(ExplorationAction explAction)
  {
    assert explAction != null

    def outVerDevActs = newEmptyVerifiableDeviceActions()
    switch (explAction.class)
    {
      case WidgetExplorationAction:
        outVerDevActs << newClickGuiVerifiableDeviceAction((explAction as WidgetExplorationAction))
        break

      case PressBackExplorationAction:
        outVerDevActs << newPressBackToAppVerifiableDeviceAction(appPackageName)
        break

      case EnterTextExplorationAction:
        outVerDevActs << newEnterTextVerifiableDeviceAction(
          (explAction as EnterTextExplorationAction).widget.resourceId,
          (explAction as EnterTextExplorationAction).textToEnter,
          appPackageName)
        break

      case ResetAppExplorationAction:
        addVerifiableActionsResettingAppPackageAndNavigatingToHomeScreen(outVerDevActs)
        outVerDevActs << newTurnWifiOnVerifiableDeviceAction()
        outVerDevActs << newLaunchActivityVerifiableDeviceAction(appLaunchableActivityComponentName, appPackageName)
        break

      case TerminateExplorationAction:
        addVerifiableActionsResettingAppPackageAndNavigatingToHomeScreen(outVerDevActs)
        break

      default:
        throw new UnexpectedIfElseFallthroughError("No translation available for exploration action class of ${explAction.class.name}")
    }

    log.debug("Translated: $explAction ->")// [${outVerDevActs*.description.join(" : ")}]")
    outVerDevActs.each {
      log.debug("  ${it.description},")
    }
    return outVerDevActs
  }

  private void addVerifiableActionsResettingAppPackageAndNavigatingToHomeScreen(VerifiableDeviceActions outVerDevActs)
  {
    if (!softReset)
      outVerDevActs << newResetPackageVerifiableDeviceAction(appPackageName)
    // WISH maybe soft reset should do "adb shell am force-stop pkg_name"
    outVerDevActs << newPressHomeVerifiableDeviceAction()
  }
}