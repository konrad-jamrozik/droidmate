// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common_android;

import java.io.Serializable;

public class DeviceResponse implements Serializable {

  public Throwable throwable;

  public boolean isNaturalOrientation;
}