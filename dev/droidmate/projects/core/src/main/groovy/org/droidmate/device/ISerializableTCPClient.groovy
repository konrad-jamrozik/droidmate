// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device

import org.droidmate.exceptions.DeviceException

public interface ISerializableTCPClient<InputToServerT extends Serializable, OutputFromServerT extends Serializable>
{
  Boolean isServerReachable(IDevicePort port) throws DeviceException

  OutputFromServerT queryServer(InputToServerT input, IDevicePort port) throws DeviceException
}
