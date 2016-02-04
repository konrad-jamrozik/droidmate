// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException

@Deprecated
class DeviceGuiSnapshotVerificationException extends DeviceException
{

  private static final long serialVersionUID = 1

  DeviceGuiSnapshotVerificationException(VerifiableDeviceAction verifiableDeviceAction, IDeviceGuiSnapshot guiSnapshot)
  {
    super(["Failed to verify the expected GUI snapshot after performing an action on device.",
           "The verifiable device action: ${verifiableDeviceAction.description}",
           "The GUI snapshot that failed verification: ${guiSnapshot}"]
      .join("\n"))
  }
}