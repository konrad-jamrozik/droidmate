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

package org.droidmate.uiautomator_daemon;

import org.droidmate.uiautomator_daemon.guimodel.GuiAction;

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
    return guiAction != null ? guiAction.equals(that.guiAction) : that.guiAction == null;

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
