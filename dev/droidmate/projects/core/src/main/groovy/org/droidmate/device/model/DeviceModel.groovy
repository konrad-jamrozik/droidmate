// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

import groovy.util.logging.Slf4j
import org.droidmate.exceptions.UnknownDeviceException
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants

/**
 * Please see {@link DeviceModel#build(java.lang.String)}.
 * 
 * @author Nataniel Borges Jr. (inception)
 * @author Konrad Jamrozik (refactoring)
 */
@Slf4j
public class DeviceModel
{

  /**
   * <p>
   * Create an {@link IDeviceModel} based on the string obtained from <pre>org.droidmate.uiautomator_daemon.UiAutomatorDaemonDriver#getDeviceModel()</pre>
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
    /*
     Obtained from emulator with following settings:
        Name: Nexus_7_2012_API_19
        CPU/ABI: Intel Atom (x86)
        Target: Android 4.4.2 (API level 19)
        Skin: nexus_7
        hw.device.name: Nexus 7
        hw.device.manufacturer: Google
        AvdId: Nexus_7_2012_API_19
        avd.ini.displayname: Nexus 7 (2012) API 19
        hw.ramSize: 1024
        hw.gpu.enabled: yes
     */
      case "unknown-Android SDK built for x86":
        result = new Nexus7_API19_Model()
        break
      case UiautomatorDaemonConstants.DEVICE_GOOGLE_NEXUS_7:
        result = new Nexus7_API19_Model()
        break
      case "samsung-Nexus 10":
        result = new Nexus10Model()
        break
      case UiautomatorDaemonConstants.DEVICE_SAMSUNG_GALAXY_S3_GT_I9300:
        result = new GalaxyS3Model()
        break
      default:
        result = buildDefault()
        log.warn("Unrecognized device model of $deviceModel. Using the default ${result.class.simpleName}.")
    }

    return result
  }
  
  public static IDeviceModel buildDefault()
  {
    return new Nexus7_API19_Model()
  }
}