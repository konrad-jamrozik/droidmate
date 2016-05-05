// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

import org.droidmate.exceptions.UnknownDeviceException
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants

/**
 * Please see {@link DeviceModel#build(java.lang.String)}.
 * 
 * @author Nataniel Borges Jr.
 */
public class DeviceModel
{

  /**
   * <p>
   * Create an {@link IDeviceModel} based on the string obtained from <pre>org.droidmate.uiautomator_daemon.UiAutomatorDaemonDriver#getDeviceModel()</pre>
   * 
   * </p><p>
   * To create a default device use {@link org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants#DEVICE_DEFAULT}.
   *
   * </p><p>
   * @param deviceModel Device manufacturer + model as returned by {@link org.droidmate.uiautomator_daemon.UiAutomatorDaemonDriver#getDeviceModel()}
   * 
   * </p>
   */
  public static IDeviceModel build(String deviceModel) throws UnknownDeviceException
  {
    IDeviceModel result

    switch (deviceModel)
    {
      case UiautomatorDaemonConstants.DEVICE_GOOGLE_NEXUS_7:
      case UiautomatorDaemonConstants.DEVICE_GOOGLE_NEXUS_7_EMU_x86:
      case UiautomatorDaemonConstants.DEVICE_DEFAULT:
        result = new Nexus7Model()
        break
      case UiautomatorDaemonConstants.DEVICE_GOOGLE_NEXUS_10:
        result = new Nexus10Model()
        break
      case UiautomatorDaemonConstants.DEVICE_SAMSUNG_GALAXY_S3_GT_I9300:
        result = new GalaxyS3Model()
        break
      default:
        throw new UnknownDeviceException(deviceModel)
    }

    return result
  }
  
  public static IDeviceModel buildDefault()
  {
    return build(UiautomatorDaemonConstants.DEVICE_DEFAULT)
  }
}