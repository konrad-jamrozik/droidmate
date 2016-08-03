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

public class UiautomatorWindowHierarchyDumpDeviceResponse extends DeviceResponse
{
  /**
   * This field contains string representing the contents of the file returned by
   *  {@code android.support.test.uiautomator.UiAutomatorTestCase.getUiDevice().dumpWindowHierarchy();}<br/>
   *  as well as <br/>
   *  {@code android.support.test.uiautomator.UiAutomatorTestCase.getUiDevice().dumpDisplayWidth();}<br/>
   *  {@code android.support.test.uiautomator.UiAutomatorTestCase.getUiDevice().dumpDisplayHeight();}
   */
  public final String windowHierarchyDump;
  public final int displayWidth;
  public final int displayHeight;

  public UiautomatorWindowHierarchyDumpDeviceResponse(String windowHierarchyDump, int displayWidth, int displayHeight)
  {
    this.windowHierarchyDump = windowHierarchyDump;
    this.displayWidth = displayWidth;
    this.displayHeight = displayHeight;
  }

}
