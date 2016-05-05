// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

import java.awt.*

/**
 * Provides an interface with device specific methods using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: Product
 *
 * @author Nataniel Borges Jr.
 */
public interface IDeviceModel extends Serializable
{

  /**
   * Get the name of the top level package on the device's home screen
   */
  String getAndroidLauncherPackageName()


  /**
   * Get the size of the device screen. Currently used only for testing purposes.
   */
  Dimension getDeviceDisplayDimensionsForTesting()
}
