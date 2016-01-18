// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.android_sdk

public class AndroidDeviceDescriptor {

  public final String deviceSerialNumber
  public final boolean isEmulator

  public AndroidDeviceDescriptor(String deviceSerialNumber, boolean isEmulator) {
    this.deviceSerialNumber = deviceSerialNumber
    this.isEmulator = isEmulator
  }
}
