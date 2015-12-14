// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common_android;

/** Holds constants used by at least two of projects being part of DroidMate.
 */
public class Constants
{

  // Why this is constant and not a cmd line parameter? See comment in Configuration class.
  public static final int AUTADDON_SERVER_PORT  = 59777;
  public static final int UIADAEMON_SERVER_PORT = 59778;

  // End of DUPLICATION WARNING

  public static final String UIADAEMON_SERVER_START_TAG = "uiautomator-daemon_server_start_tag";
  public static final String UIADAEMON_SERVER_START_MSG = "UiAutomator Daemon server started successfully";

  public static final String DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP = "get_uiautomator_window_hierarchy_dump";
  public static final String DEVICE_COMMAND_GET_IS_ORIENTATION_LANDSCAPE          = "get_is_orientation_landscape";
  public static final String DEVICE_COMMAND_PERFORM_ACTION                        = "perform_action";
  public static final String DEVICE_COMMAND_STOP_UIADAEMON                        = "stop_uiadaemon";

  /**
   * Method name to be called when initializing {@code UiAutomatorDaemon} through adb.<br/>
   * <br/>
   * Name format according to help obtained by issuing {@code adb shell uiautomator runtest} in terminal.
   */
  public static final String uiaDaemon_initMethodName = "org.droidmate.uiautomatordaemon.UiAutomatorDaemon#init";

  public static final String guiActionCommand_pressBack  = "press_back";
  public static final String guiActionCommand_pressHome  = "press_home";
  public static final String guiActionCommand_turnWifiOn = "turn_wifi_on";
  @SuppressWarnings("unused") // Used in org.droidmate.uiautomatordaemon.UiAutomatorDaemonDriver.performAction()
  public static final String guiActionCommand_launchApp  = "launch_app";

  public static final String uiaDaemonParam_waitForGuiToStabilize      = "wait_for_gui_to_stabilize";
  public static final String uiaDaemonParam_waitForWindowUpdateTimeout = "wait_for_window_update_timeout";
  public static final String uiaDaemonParam_tcpPort                    = "uiadaemon_server_tcp_port";
  public static final String uiaDaemon_logcatTag                       = "org.droidmate.uiad";

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
