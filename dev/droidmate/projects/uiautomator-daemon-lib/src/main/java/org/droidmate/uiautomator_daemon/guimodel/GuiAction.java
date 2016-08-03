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

package org.droidmate.uiautomator_daemon.guimodel;

import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiAction implements Serializable
{
  private static final long serialVersionUID = 1L;

  public final Integer clickXCoor;
  public final Integer clickYCoor;
  public final boolean longClick;
  public final String  guiActionCommand;
  public final String  resourceId;
  public final String  textToEnter;

  public GuiAction(int clickXCoor, int clickYCoor, boolean longClick)
  {
    this.clickXCoor = clickXCoor;
    this.clickYCoor = clickYCoor;
    this.longClick = longClick;
    this.guiActionCommand = null;
    this.resourceId = null;
    this.textToEnter = null;
  }

  public GuiAction(String guiActionCommand, String resourceId, String textToEnter)
  {
    this.clickXCoor = null;
    this.clickYCoor = null;
    this.longClick = false;
    this.guiActionCommand = guiActionCommand;
    this.resourceId = resourceId;
    this.textToEnter = textToEnter;

  }

  public GuiAction(String guiActionCommand)
  {
    this.clickXCoor = null;
    this.clickYCoor = null;
    this.longClick = false;
    this.guiActionCommand = guiActionCommand;
    this.resourceId = null;
    this.textToEnter = null;
  }


  @Override
  public String toString()
  {
    if (guiActionCommand == null)
      return clickXCoor + " " + clickYCoor + " long: " + longClick;
    else
      return guiActionCommand;
  }

  // WISH to remove? Maybe used in UiAutomatorDaemon
  public static GuiAction from(String sourceStr)
  {
    assert sourceStr != null;
    /*
      The regex means matches click coordinates in form:
      x_coor y_coor
      for example, click having x=120 and y=560 will be encoded as:
      120 560

      N00b Reference for regexes:
      http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
      http://stackoverflow.com/questions/7641008/java-regex-to-match-any-integer
     */
    Pattern p = Pattern.compile("\\b\\d+\\b \\b\\d+\\b");
    Matcher m = p.matcher(sourceStr);
    boolean coordsString = m.matches();

    if (coordsString)
    {
      String[] splitStr = sourceStr.split(" ");
      return new GuiAction(Integer.parseInt(splitStr[0]), Integer.parseInt(splitStr[1]), /* long click */ false);
    }
    else if (sourceStr.equals(UiautomatorDaemonConstants.guiActionCommand_pressBack))
      return new GuiAction(sourceStr);
    else if (sourceStr.equals(UiautomatorDaemonConstants.guiActionCommand_pressHome))
      return new GuiAction(sourceStr);
    else if (sourceStr.equals(UiautomatorDaemonConstants.guiActionCommand_turnWifiOn))
      return new GuiAction(sourceStr);
    else
      return null;

  }

  public static GuiAction createPressBackGuiAction()
  {
    return new GuiAction(UiautomatorDaemonConstants.guiActionCommand_pressBack);
  }

  public static GuiAction createEnterTextGuiAction(String resourceId, String textToEnter)
  {
    return new GuiAction(null, resourceId, textToEnter);
  }

  public static GuiAction createPressHomeGuiAction()
  {
    return new GuiAction(UiautomatorDaemonConstants.guiActionCommand_pressHome);
  }

  public static GuiAction createTurnWifiOnGuiAction()
  {
    return new GuiAction(UiautomatorDaemonConstants.guiActionCommand_turnWifiOn);
  }

  public static GuiAction createLaunchAppGuiAction(String iconLabel)
  {
    return new GuiAction(UiautomatorDaemonConstants.guiActionCommand_launchApp, iconLabel, null);
  }


  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof GuiAction)) return false;

    GuiAction guiAction = (GuiAction) o;

    if (clickXCoor != null ? !clickXCoor.equals(guiAction.clickXCoor) : guiAction.clickXCoor != null) return false;
    if (clickYCoor != null ? !clickYCoor.equals(guiAction.clickYCoor) : guiAction.clickYCoor != null) return false;
    return guiActionCommand != null ? guiActionCommand.equals(guiAction.guiActionCommand) : guiAction.guiActionCommand == null;

  }

  @Override
  public int hashCode()
  {
    int result = clickXCoor != null ? clickXCoor.hashCode() : 0;
    result = 31 * result + (clickYCoor != null ? clickYCoor.hashCode() : 0);
    result = 31 * result + (guiActionCommand != null ? guiActionCommand.hashCode() : 0);
    return result;
  }
}
