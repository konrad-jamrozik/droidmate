// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration.model

import org.droidmate.common_android.Constants
import org.droidmate.exceptions.UnknownDeviceException

/**
 * Provides a class to acquire device specific objects using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: ConcreteCreator
 *
 * @author Nataniel Borges Jr.
 */
public class DeviceModelHelper
{

  /**
   * Create an #IDeviceModel based on the device. <br/>
   * To create a default device use {@link org.droidmate.common_android.Constants#DEVICE_DEFAULT}.
   * The default device is a Google Nexus 7.
   *
   * @param deviceModel Device manufacturer + model as returned by
   * {@link org.droidmate.uiautomatordaemon.UiAutomatorDaemonDriver#getDeviceModel()}
   * *
   * @return Device specific issues handler
   * @throws UnknownDeviceException If the device model is not mapped to any device
   */
  public static IDeviceModel build(String deviceModel) throws UnknownDeviceException
  {
    IDeviceModel result

    switch (deviceModel)
    {
      case Constants.DEVICE_GOOGLE_NEXUS_7:
        result = new Nexus7Model()
        break
      case Constants.DEVICE_SAMSUNG_GALAXY_S3_GT_I9300:
        result = new SamsungGalaxyS3Model()
        break
      case Constants.DEVICE_DEFAULT:
        result = new Nexus7Model()
        break
      default:
        throw new UnknownDeviceException()
    }

    return result
  }
}