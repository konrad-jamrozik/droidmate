// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomator2daemon;

import org.droidmate.common_android.DeviceCommand;
import org.droidmate.common_android.DeviceResponse;
import org.droidmate.common_android.UiAutomatorDaemonException;

public interface IUiAutomatorDaemonDriver  {

    DeviceResponse executeCommand(DeviceCommand deviceCommand) throws UiAutomatorDaemonException;

}
