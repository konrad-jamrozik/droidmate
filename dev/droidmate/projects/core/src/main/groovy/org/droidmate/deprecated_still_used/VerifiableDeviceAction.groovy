// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.transform.Canonical
import org.droidmate.device.datatypes.AppHasStoppedDialogBoxGuiState
import org.droidmate.device.datatypes.GuiState
import org.droidmate.device.datatypes.IAndroidDeviceAction
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exploration.actions.WidgetExplorationAction

import static org.droidmate.device.datatypes.AndroidDeviceAction.*

@Deprecated
@Canonical
class VerifiableDeviceAction
{

  public static final String closeAppHasStoppedDescription =
    "<click on 'OK' of 'app has stopped' dialog box, expect any screen except 'app has stopped' dialog box>"
  public static final String launchActivityDescriptionPrefix = "<launch "

  String               description
  IAndroidDeviceAction action
  Closure<Boolean>     verifierOfResultingGuiSnapshot
  int                  delay = 0


  public static VerifiableDeviceAction newResetPackageVerifiableDeviceAction(String appPackageName)
  {
    return new VerifiableDeviceAction(
      description: "<reset $appPackageName, expect any screen except app's>",
      action: newResetPackageDeviceAction(appPackageName),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> !snapshot.guiState.belongsToApp(appPackageName)})
  }

  public static VerifiableDeviceAction newLaunchActivityVerifiableDeviceAction(
    String appLaunchableActivityComponentName, String appPackageName)
  {
    new VerifiableDeviceAction(
      description: "$launchActivityDescriptionPrefix$appLaunchableActivityComponentName, expect app screen>",
      action: newLaunchActivityDeviceAction(appLaunchableActivityComponentName),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> snapshot.guiState.belongsToApp(appPackageName)})
  }

  public static VerifiableDeviceAction newClickGuiVerifiableDeviceAction(WidgetExplorationAction clickedWidgetExplorationAction)
  {
    boolean lc = clickedWidgetExplorationAction.longClick
    return new VerifiableDeviceAction(
      description: "<${lc ? "long " : ""}click on ${clickedWidgetExplorationAction.toShortString()}, no expectations>",
      action: newClickGuiDeviceAction(clickedWidgetExplorationAction.widget, lc),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> true},
      delay: clickedWidgetExplorationAction.delay
    )
  }

  public static VerifiableDeviceAction newClickOKOnAppHasStoppedDialogGuiVerifiableDeviceAction(
    GuiState appHasStoppedDialog)
  {
    assert appHasStoppedDialog instanceof AppHasStoppedDialogBoxGuiState

    def gs = appHasStoppedDialog as AppHasStoppedDialogBoxGuiState

    return new VerifiableDeviceAction(
      description: closeAppHasStoppedDescription,
      action: newClickGuiDeviceAction(gs.OKWidget),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> !snapshot.guiState.appHasStoppedDialogBox})
  }

  public static VerifiableDeviceAction newPressBackToHomeScreenVerifiableDeviceAction()
  {

    return new VerifiableDeviceAction(
      description: "<press 'back', expect home screen>",
      action: newPressBackDeviceAction(),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> snapshot.guiState.isHomeScreen()})
  }

  public static VerifiableDeviceAction newPressBackToAppVerifiableDeviceAction(String appPackageName)
  {
    return new VerifiableDeviceAction(
      description: "<click 'back', expect app screen>",
      action: newPressBackDeviceAction(),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> snapshot.guiState.belongsToApp(appPackageName)})
  }

  public
  static VerifiableDeviceAction newEnterTextVerifiableDeviceAction(String resourceId, String textToEnter, String appPackageName)
  {
    return new VerifiableDeviceAction(
      description: "<enter text in widget with resourceId $resourceId, expect app screen>",
      action: newEnterTextDeviceAction(resourceId, textToEnter),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> snapshot.guiState.belongsToApp(appPackageName)})
  }


  public static VerifiableDeviceAction newPressHomeVerifiableDeviceAction()
  {
    return new VerifiableDeviceAction(
      description: "<press home, expect home screen>",
      action: newPressHomeDeviceAction(),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> snapshot.guiState.isHomeScreen()})
  }

  public static VerifiableDeviceAction newTurnWifiOnVerifiableDeviceAction()
  {
    return new VerifiableDeviceAction(
      description: "<turn wifi on, expect home screen>",
      action: newTurnWifiOnDeviceAction(),
      verifierOfResultingGuiSnapshot: {IDeviceGuiSnapshot snapshot -> snapshot.guiState.isHomeScreen()})
  }


  /*
    The custom equals() and hashCode() ensure that the verifierOfResultingGuiSnapshot closure won't be included.
   */
  boolean equals(o)
  {
    if (this.is(o)) return true
    if (!(o instanceof VerifiableDeviceAction)) return false

    VerifiableDeviceAction that = (VerifiableDeviceAction) o

    if (action != that.action) return false

    return true
  }

  int hashCode()
  {
    return action.hashCode()
  }
}
