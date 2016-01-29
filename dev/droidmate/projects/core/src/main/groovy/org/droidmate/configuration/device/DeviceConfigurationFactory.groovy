// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration.device

import org.droidmate.common_android.Constants
import org.droidmate.exceptions.ConfigurationException

/**
 * Created by Nataniel Borges Jr. on 27/01/2016.
 */
class DeviceConfigurationFactory implements IDeviceConfigurationFactory
{
  private String deviceModel

  private DeviceConfigurationFactory(String deviceModel)
  {
    this.deviceModel = deviceModel
  }

  @Override
  public IDeviceSpecificConfiguration getConfiguration()
  {
    IDeviceSpecificConfiguration result

    // WISH Borges replace for constant
    switch (this.deviceModel)
    {
      case Constants.DEVICE_GOOGLE_NEXUS_7:
        result = new Nexus7Configuration()
        break
      case Constants.DEVICE_SAMSUNG_GALAXY_S3_GT_I9300:
        result = new SamsungGalaxyS3Configuration()
        break
      case Constants.DEVICE_DEFAULT:
        result = new Nexus7Configuration()
        break
      default:
        throw new ConfigurationException("Device not configured")
    }

    return result
  }
}