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
import org.droidmate.exceptions.DeviceNeedsRebootException
import org.droidmate.exceptions.TcpServerUnreachableException

public interface ISerializableTCPClient<InputToServerT extends Serializable, OutputFromServerT extends Serializable>
{

  OutputFromServerT queryServer(InputToServerT input, int port) throws DeviceNeedsRebootException, TcpServerUnreachableException, DeviceException
}
