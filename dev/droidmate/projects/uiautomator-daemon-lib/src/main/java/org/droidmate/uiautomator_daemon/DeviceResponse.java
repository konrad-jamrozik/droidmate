// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomator_daemon;

import java.io.Serializable;

public class DeviceResponse implements Serializable {

  public Throwable throwable;

  public boolean isNaturalOrientation;

  public String model;
}