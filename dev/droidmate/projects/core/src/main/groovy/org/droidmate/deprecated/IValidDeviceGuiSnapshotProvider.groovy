// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.UiautomatorWindowDumpValidationException

@Deprecated
interface IValidDeviceGuiSnapshotProvider
{

  IDeviceGuiSnapshot getValidGuiSnapshot(IExplorableAndroidDevice device) throws UiautomatorWindowDumpValidationException
}
