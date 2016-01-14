// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common_android;

import org.droidmate.common_android.guimodel.GuiAction;

import java.io.Serializable;

public class DeviceCommand implements Serializable {

  public String command;
  public GuiAction guiAction;

  public DeviceCommand(String command) {
    this(command, null);
  }

  public DeviceCommand(String command, GuiAction guiAction) {
    this.command = command;
    this.guiAction = guiAction;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (!(o instanceof DeviceCommand)) return false;

    DeviceCommand that = (DeviceCommand) o;

    if (command != null ? !command.equals(that.command) : that.command != null) return false;
    if (guiAction != null ? !guiAction.equals(that.guiAction) : that.guiAction != null) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = command != null ? command.hashCode() : 0;
    result = 31 * result + (guiAction != null ? guiAction.hashCode() : 0);
    return result;
  }

  @Override
  public String toString()
  {
    return "DeviceCommand{" +
      "command='" + command + '\'' +
      ", guiAction=" + guiAction +
      '}';
  }
}
