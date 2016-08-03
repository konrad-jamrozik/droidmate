// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

package org.droidmate.exploration.strategy

import groovy.util.logging.Slf4j
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.datatypes.IGuiState
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.actions.ExplorationAction

import static org.droidmate.exploration.actions.ExplorationAction.*

@Slf4j
class ForwardExplorationSpecialCases implements IForwardExplorationSpecialCases
{

  private static final String user       = "debugg7"
  private static final String pass       = "qwer////"
  public static final  int    loginDelay = 3000 // ms

  int snapchatLoginAttempts     = 5
  int snapchatLoginAttemptsLeft = snapchatLoginAttempts

  Step lastStep = Step.UNKNOWN

  private enum Step {
    // @formatter:off
    // After app reset, when we still have to determine if login is necessary at all
    UNKNOWN,
    // Step 1
    CLICK_TO_GO_TO_LOGIN_ENTRY_PAGE,
    // Step 2
    ENTER_USER,
    // Step 3
    ENTER_PASS,
    // Step 4
    LOG_INTO_THE_APP,
    // If login succeeds
    NONE,
    // If login fails
    CLICK_TRY_AGAIN_POPUP,
    // If all login attempts have been exhausted or unknown GUI state is encountered
    TERMINATE
    // @formatter:on
  }

  @Override
  List process(IGuiState guiState, String packageName)
  {
    /*
    At this time special case processing is turned off. It was being used only for Snapchat.
    Snapchat can no longer be explored due to remote update that prevents using any but the newer versions.
    The never versions have integrity check of bytecode.
    The check makes Snapchat unusable after startup if it was inlined.
    Thus, it can no longer be explored by DroidMate, as it required inlining.

    Furthermore, only this code used "EnterTextExplorationAction". Its handling is not implemented in the code.
    To be exact, it is not handled in org.droidmate.exploration.actions.RunnableExplorationAction.from() method.
    This is because code has been refactored after Snapchat stopped working.

    Thus, the code is turned off to prevent triggering the bug https://hg.st.cs.uni-saarland.de/issues/995.
     */
//    def currentStep = updateLoginStep(guiState, packageName, lastStep)
//
//    def outExplAction = proceedWithSnapchatLogin(guiState, currentStep)
//
//    lastStep = currentStep
//    return [outExplAction != null, outExplAction]
    return [false, null]
  }

  Step updateLoginStep(IGuiState guiState, String packageName, Step lastLoginStep)
  {
    Step currentStep
    switch (lastLoginStep)
    {
      case Step.UNKNOWN:
        currentStep = initiateLoginIfNecessary(guiState, packageName)
        break

      case Step.CLICK_TO_GO_TO_LOGIN_ENTRY_PAGE:
        assert onLoginEntryPage(guiState, packageName)
        currentStep = Step.ENTER_USER
        break

      case Step.ENTER_USER:
        assert onLoginEntryPage(guiState, packageName)
        currentStep = Step.ENTER_PASS
        break

      case Step.ENTER_PASS:
        assert onLoginEntryPage(guiState, packageName)
        currentStep = Step.LOG_INTO_THE_APP
        break

      case Step.LOG_INTO_THE_APP:
        currentStep = evaluateLoginResult(guiState, packageName)
        assert currentStep in [Step.NONE, Step.CLICK_TRY_AGAIN_POPUP, Step.TERMINATE]
        break

      case Step.NONE:
        currentStep = onLandingPage(guiState, packageName) ? Step.CLICK_TO_GO_TO_LOGIN_ENTRY_PAGE : Step.NONE
        assert !onLoginEntryPage(guiState, packageName)
        assert !onTryAgainPopup(guiState, packageName)
        break

      case Step.CLICK_TRY_AGAIN_POPUP:
        assert onLoginEntryPage(guiState, packageName)
        currentStep = Step.ENTER_USER
        break

      default:
        throw new UnexpectedIfElseFallthroughError()
    }
    return currentStep
  }

  Step initiateLoginIfNecessary(IGuiState guiState, String packageName)
  {
    // True when exploring other apps.
    if (!inSnapchat(packageName))
      return Step.NONE

    if (onLandingPage(guiState, packageName))
      return Step.CLICK_TO_GO_TO_LOGIN_ENTRY_PAGE

    assert !onLoginEntryPage(guiState, packageName)
    assert !onTryAgainPopup(guiState, packageName)

    assert inSnapchat(packageName)
    return Step.NONE
  }

  private ExplorationAction proceedWithSnapchatLogin(IGuiState guiState, Step currentStep)
  {
    assert currentStep != Step.UNKNOWN
    ExplorationAction action

    switch (currentStep)
    {
      case Step.CLICK_TO_GO_TO_LOGIN_ENTRY_PAGE:
        Widget w = guiState.widgets.find {it.text == "LOG IN"}
        assert w != null
        action = newWidgetExplorationAction(w)
        break

      case Step.ENTER_USER:
        if (guiState.widgets.any {it.resourceId == "com.snapchat.android:id/login_username_email"})
        // Works for Snapchat from March 2014 / CCS 2014
          action = newEnterTextExplorationAction(user, "com.snapchat.android:id/login_username_email")
        else
        {
          assert guiState.widgets.any {it.resourceId == "com.snapchat.android:id/login_username_email_field"}
          // Works for Snapchat from February 2015
          action = newEnterTextExplorationAction(user, "com.snapchat.android:id/login_username_email_field")
        }
        break

      case Step.ENTER_PASS:
        if (guiState.widgets.any {it.resourceId == "com.snapchat.android:id/login_password"})
        // Works for Snapchat from March 2014 / CCS 2014
          action = newEnterTextExplorationAction(pass, "com.snapchat.android:id/login_password")
        else
        {
          assert guiState.widgets.any {it.resourceId == "com.snapchat.android:id/login_password_field"}
          // Works for Snapchat from February 2015
          action = newEnterTextExplorationAction(pass, "com.snapchat.android:id/login_password_field")
        }
        break

      case Step.LOG_INTO_THE_APP:
        // Works for both versions of snapchat
        Widget w = guiState.widgets.find {it.text == "LOG IN"}
        assert w != null
        action = newWidgetExplorationAction(w, loginDelay)
        break

      case Step.NONE:
        action = null
        break

      case Step.CLICK_TRY_AGAIN_POPUP:
        // Works for both versions of snapchat
        Widget w = guiState.widgets.find {it.text == "Try again"}
        assert w != null
        action = newWidgetExplorationAction(w)
        break

      case Step.TERMINATE:
        action = newTerminateExplorationAction()
        break

      case Step.UNKNOWN:
        assert false: "Current step cannot be 'unknown'"
        break

      default:
        throw new UnexpectedIfElseFallthroughError()

    }
    assert action != null || currentStep == Step.NONE
    return action
  }

  private static boolean loginSucceeded(IGuiState guiState, String packageName)
  {
    return inSnapchat(packageName) && !onAnyLoginScreen(guiState, packageName)
  }

  private static boolean inSnapchat(String packageName)
  {
    return packageName == "com.snapchat.android"
  }

  private static boolean onAnyLoginScreen(IGuiState guiState, String packageName)
  {
    return onLandingPage(guiState, packageName) || onLoginEntryPage(guiState, packageName) || onTryAgainPopup(guiState, packageName)
  }

  Step evaluateLoginResult(IGuiState guiState, String packageName)
  {
    if (loginSucceeded(guiState, packageName))
    {
      snapchatLoginAttemptsLeft = snapchatLoginAttempts
      return Step.NONE
    } else
    {
      snapchatLoginAttemptsLeft--
      log.warn("Failed to login to snapchat. Making another attempt. Attempts left after this one: $snapchatLoginAttemptsLeft")

      if (snapchatLoginAttemptsLeft == 0)
      {
        log.warn("Aborting exploration of Snapchat: all $snapchatLoginAttempts hard-coded login attempts failed.\n" +
          "Details: expected to start the hard-coded login process, but the current GUI state doesn't have the expected " +
          "'SIGN UP' button on it.\n" +
          "Possible reason: previous login attempt failed because wifi is disabled and snapchat displays the login data entry " +
          "login screen instead of the initial one with 'SIGN UP' button. To confirm this is the case, please investigate the " +
          "logs from the run.")
        return Step.TERMINATE
      }

      if (onTryAgainPopup(guiState, packageName))
        return Step.CLICK_TRY_AGAIN_POPUP
      else
      {
        log.warn("Ended up in unknown GUI state after making an attempt to log into snapchat. $guiState")
        return Step.TERMINATE
      }
    }
  }

  private static boolean onLandingPage(IGuiState guiState, String packageName)
  {
    return inSnapchat(packageName) && guiState.widgets.any {it.text == "LOG IN"} &&
      guiState.widgets.any {it.text == "SIGN UP"}
  }

  private static boolean onLoginEntryPage(IGuiState guiState, String packageName)
  {
    return inSnapchat(packageName) && guiState.widgets.any {it.text in ["LOG IN", "Log In"]} &&
      !guiState.widgets.any {it.text == "SIGN UP"}
  }

  private static boolean onTryAgainPopup(IGuiState guiState, String packageName)
  {
    return inSnapchat(packageName) && guiState.widgets.any {it.text == "Try again"}
  }

}
