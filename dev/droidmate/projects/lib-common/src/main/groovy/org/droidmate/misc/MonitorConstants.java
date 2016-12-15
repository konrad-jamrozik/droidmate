// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.misc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MonitorConstants
{
  // WISH known limitation: if running droidmate with multiple devices, each will have the same set of server ports.
  // Suggested fix: make monitor.java read the port number from a .txt file; deploy different .txt file to each device.
  public static final  List<Integer> serverPorts      = Arrays.asList(59701,59702,59703,59704);
  public static final  String        tag_api          = "Monitor_API_method_call";
  
  public static final  String tag_prefix = "droidmate/monit/";
  public static final  String tag_srv    = tag_prefix + "server";
  public static final  String tag_run    = tag_prefix + "srv_run";
  // mjt == MonitorJavaTemplate
  public static final  String tag_mjt    = tag_prefix + "mjt";

  
  public static final  String loglevel                = "i";
  public static final  String msg_ctor_start          = "ctor(): entering";
  public static final  String msg_ctor_success        = "ctor(): startMonitorTCPServer(): SUCCESS port: ";
  public static final  String msg_ctor_failure        = "! ctor(): startMonitorTCPServer(): FAILURE";
  
  /**
   * <p>
   * Example full message:
   * </p><p>
   * {@code Monitor initialized for package org.droidmate.fixtures.apks.monitored}
   * </p>
   */
  public static final String msgPrefix_init_success         = "init(): SUCCESS for package ";
  public static final String srvCmd_connCheck               = "connCheck";
  public static final String srvCmd_get_logs                = "getLogs";
  public static final String srvCmd_get_time                = "getTime";
  public static final String srvCmd_close                   = "close";
  
  public static final String monitor_time_formatter_pattern = "yyyy-MM-dd HH:mm:ss.SSS";
  // !!! DUPLICATION WARNING !!! with org.droidmate.buildsrc.locale
  // BuildConstants.getLocale() is not used here as monitor_time_formatter_locale is used in android device, and BuildConstants
  // requires Groovy, which is not available on the device.
  //public static final Locale monitor_time_formatter_locale  = BuildConstants.getLocale();
  public static final Locale monitor_time_formatter_locale  = Locale.US;
}
