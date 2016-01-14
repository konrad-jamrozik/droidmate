// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import groovy.transform.Immutable

@Immutable
class LaunchMainActivityDeviceAction extends AndroidDeviceAction
{
   String launchableActivityComponentName

  @Override
  public String toString()
  {
    return "${this.class.simpleName}{$launchableActivityComponentName}"
  }
}
