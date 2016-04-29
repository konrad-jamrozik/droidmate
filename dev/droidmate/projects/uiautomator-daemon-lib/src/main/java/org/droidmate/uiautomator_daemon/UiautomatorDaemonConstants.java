// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomator_daemon;

public class UiautomatorDaemonConstants
{

  // To understand why this is constant and not a cmd line parameter, see comment in
  // org.droidmate.configuration.ConfigurationBuilder.bindAndValidate()
  public static final int UIADAEMON_SERVER_PORT = 59800;

  public static final String logcatLogFileName = "droidmate_logcat.txt";

  public static final String deviceLogcatTagPrefix = "droidmate/";
  public static final String uiaDaemon_logcatTag = deviceLogcatTagPrefix + "uiad";

  // End of DUPLICATION WARNING
  
  public static final String DEVICE_SAMSUNG_GALAXY_S3_GT_I9300 = "samsung-GT-I9300";
  public static final String DEVICE_GOOGLE_NEXUS_7             = "asus-Nexus 7";
  /**
    Obtained from emulator with following settings:
   <pre>
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
   </pre>
   */
  public static final String DEVICE_GOOGLE_NEXUS_7_EMU_x86     = "unknown-Android SDK built for x86";
  
  public static final String DEVICE_GOOGLE_NEXUS_10            = "samsung-Nexus 10";
  public static final String DEVICE_DEFAULT                    = "default";

  public static final String UIADAEMON_SERVER_START_TAG = "uiautomator-daemon_server_start_tag";
  public static final String UIADAEMON_SERVER_START_MSG = "UiAutomator Daemon server started successfully";

  public static final String DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP = "get_uiautomator_window_hierarchy_dump";
  public static final String DEVICE_COMMAND_GET_IS_ORIENTATION_LANDSCAPE          = "get_is_orientation_landscape";
  public static final String DEVICE_COMMAND_PERFORM_ACTION                        = "perform_action";
  public static final String DEVICE_COMMAND_STOP_UIADAEMON                        = "stop_uiadaemon";
  public static final String DEVICE_COMMAND_GET_DEVICE_MODEL                      = "get_device_model";

  /**
   * Method name to be called when initializing {@code UiAutomatorDaemon} through adb.<br/>
   * <br/>
   * Name format according to help obtained by issuing {@code adb shell uiautomator runtest} in terminal.
   */
  public static final String uiaDaemon_initMethodName = "org.droidmate.uiautomator_daemon.UiAutomatorDaemon#init";

  public static final String guiActionCommand_pressBack  = "press_back";
  public static final String guiActionCommand_pressHome  = "press_home";
  public static final String guiActionCommand_turnWifiOn = "turn_wifi_on";
  public static final String guiActionCommand_launchApp  = "launch_app";

  public static final String uiaDaemonParam_waitForGuiToStabilize      = "wait_for_gui_to_stabilize";
  public static final String uiaDaemonParam_waitForWindowUpdateTimeout = "wait_for_window_update_timeout";
  public static final String uiaDaemonParam_tcpPort                    = "uiadaemon_server_tcp_port";

  // !!! DUPLICATION WARNING !!!
  // These values are duplicated in Instrumentation library from Philipp.
  // Has to be equivalent to:
  // - de.uds.infsec.instrumentation.Instrumentation#TAG and
  // - <Instrumentation project dir>/jni/utils/log.h#_LOG_TAG
  public static final String instrumentation_redirectionTag = "Instrumentation";
  // end of DUPLICATION WARNING

  // !!! DUPLICATION WARNING !!!
  // org.droidmate.uieventstologcat.UIEventsToLogcatOutputter#tag
  public static final String uiEventTag = "UIEventsToLogcat";
  // end of DUPLICATION WARNING

  // !!! DUPLICATION WARNING !!!
  // org.droidmate.uia_manual_test_cases.TestCases#tag
  public static final String uiaTestCaseTag = "UiaTestCase";
  // end of DUPLICATION WARNING
  
}
