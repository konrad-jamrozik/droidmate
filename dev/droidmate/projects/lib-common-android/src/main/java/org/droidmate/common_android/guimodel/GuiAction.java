// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common_android.guimodel;

import org.droidmate.common_android.Constants;

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

  public GuiAction(String resourceId, String textToEnter)
  {
    this.clickXCoor = null;
    this.clickYCoor = null;
    this.longClick = false;
    this.guiActionCommand = null;
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
    else if (sourceStr.equals(Constants.guiActionCommand_pressBack))
      return new GuiAction(sourceStr);
    else if (sourceStr.equals(Constants.guiActionCommand_pressHome))
      return new GuiAction(sourceStr);
    else if (sourceStr.equals(Constants.guiActionCommand_turnWifiOn))
      return new GuiAction(sourceStr);
    else
      return null;

  }

  public static GuiAction createPressBackGuiAction()
  {
    return new GuiAction(Constants.guiActionCommand_pressBack);
  }

  public static GuiAction createEnterTextGuiAction(String resourceId, String textToEnter)
  {
    return new GuiAction(resourceId, textToEnter);
  }

  public static GuiAction createPressHomeGuiAction()
  {
    return new GuiAction(Constants.guiActionCommand_pressHome);
  }

  public static GuiAction createTurnWifiOnGuiAction()
  {
    return new GuiAction(Constants.guiActionCommand_turnWifiOn);
  }



  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof GuiAction)) return false;

    GuiAction guiAction = (GuiAction) o;

    if (clickXCoor != null ? !clickXCoor.equals(guiAction.clickXCoor) : guiAction.clickXCoor != null) return false;
    if (clickYCoor != null ? !clickYCoor.equals(guiAction.clickYCoor) : guiAction.clickYCoor != null) return false;
    if (guiActionCommand != null ? !guiActionCommand.equals(guiAction.guiActionCommand) : guiAction.guiActionCommand != null)
      return false;

    return true;
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
