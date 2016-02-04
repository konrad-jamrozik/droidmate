// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import groovy.transform.Immutable
import org.droidmate.deprecated_still_used.VerifiableDeviceAction

@Deprecated
@Immutable
class VerifiableDeviceActions extends ArrayList<VerifiableDeviceAction>
{
  public static VerifiableDeviceActions newEmptyVerifiableDeviceActions()
  {
    return new VerifiableDeviceActions()
  }
}
