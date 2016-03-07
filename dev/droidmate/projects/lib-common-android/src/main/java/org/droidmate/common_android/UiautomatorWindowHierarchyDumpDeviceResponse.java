// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common_android;

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
