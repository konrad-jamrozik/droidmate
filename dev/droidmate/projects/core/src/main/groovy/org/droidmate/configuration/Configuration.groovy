// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import org.droidmate.common_android.Constants
import org.droidmate.exceptions.ConfigurationException
import org.droidmate.frontend.DroidmateFrontend
import org.droidmate.init.InitConstants
import org.droidmate.init.LocalInitConstants
import org.droidmate.lib_android.MonitorJavaTemplate

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths

/**
 * <p>
 * <i> --- This doc was last reviewed on 22 May 2014.</i>
 * </p><p>
 *
 * Holds all the configuration data of DroidMate. The configuration is obtained from command line arguments with help
 * of {@link ConfigurationBuilder}. This happens just before DroidMate injects dependencies with Guice,
 * at DroidMate bootstrap in {@link DroidmateFrontend DroidmateFrontend}.
 * </p><p>
 * The configuration is required to setup most (if not all) classes (components) of Droidmate.
 *
 * </p><p>
 * The <it>@Parameter</it> annotations are dictated by the underlying {@code jCommander} library [1].
 *
 * </p><p>
 * [1]: http://jcommander.org/
 *
 * </p>
 *
 * @see ConfigurationBuilder
 */
// Explanation of @SuppressWarnings("UnnecessaryQualifiedReference"):
// This is a workaround for https://issues.apache.org/jira/browse/GROOVY-3278, which appears in @Parameter "names" argument declaration.
// Solution adapted from: http://stackoverflow.com/a/29042946/986533
@SuppressWarnings("UnnecessaryQualifiedReference")
@Parameters(separators = " =")
public class Configuration implements IConfiguration
{
  //region Instance construction logic

  /** The raw args (as given to {@code public static void main(String[] args)}) from which this configuration was obtained. */
  private String[] args

  public static Configuration getDefault() throws ConfigurationException
  {
    return getDefault(FileSystems.getDefault())
  }

  public static Configuration getDefault(FileSystem fs) throws ConfigurationException
  {
    return new ConfigurationBuilder().build(new String[0], fs)
  }

  public String[] getArgs()
  {
    return args
  }

  public Configuration(String[] args)
  {
    this.args = args
  }
  //endregion

  //region Cmd line parameters names and defaults

  // @formatter:off
  public static final String pn_actionsLimit                                 = "-actionsLimit"
  public static final String pn_alwaysClickFirstWidget                       = "-alwaysClickFirstWidget"
  public static final String pn_apksNames                                    = "-apksNames"
  public static final String pn_apksDir                                      = "-apksDir"
  public static final String pn_apksLimit                                    = "-apksLimit"
  public static final String pn_appGuardOnlyApis                             = "-appGuardOnlyApis"
  public static final String pn_clearPackageRetryAttempts                    = "-clearPackageRetryAttempts"
  public static final String pn_clearPackageRetryDelay                       = "-clearPackageRetryDelay"
  public static final String pn_compareRuns                                  = "-compare"
  public static final String pn_delayAfterLaunchingActivity                  = "-launchActivityDelay"
  public static final String pn_deployRawApks                                = "-deployRawApks"
  public static final String pn_device                                       = "-device"
  public static final String pn_droidmateOutputDir                           = "-droidmateOutputDirPath"
  public static final String pn_exploreInteractively                         = "-exploreInteractively"
  public static final String pn_getValidGuiSnapshotRetryAttempts             = "-getValidGuiSnapshotRetryAttempts"
  public static final String pn_getValidGuiSnapshotRetryDelay                = "-getValidGuiSnapshotRetryDelay"
  public static final String pn_checkAppIsRunningRetryAttempts               = "-checkAppIsRunningRetryAttempts"
  public static final String pn_checkAppIsRunningRetryDelay                  = "-checkAppIsRunningRetryDelay"
  public static final String pn_logWidgets                                   = "-logWidgets"
  public static final String pn_monitorServerStartQueryInterval              = "-monitorServerStartQueryInterval"
  public static final String pn_monitorServerStartTimeout                    = "-monitorServerStartTimeout"
  public static final String pn_randomSeed                                   = "-randomSeed"
  public static final String pn_resetEveryNthExplorationForward              = "-resetEvery"
  public static final String pn_splitCharts                                  = "-splitCharts"
  public static final String pn_socketTimeout                                = "-socketTimeout"
  public static final String pn_softReset                                    = "-softReset"
  public static final String pn_timeLimit                                    = "-timeLimit"
  public static final String pn_uiautomatorDaemonServerStartTimeout          = "-uiautomatorDaemonServerStartTimeout"
  public static final String pn_uiautomatorDaemonServerStartQueryInterval    = "-uiautomatorDaemonServerStartQueryInterval"
  public static final String pn_uiautomatorDaemonTcpPort                     = "-tcpPort"
  public static final String pn_uiautomatorDaemonWaitForGuiToStabilize       = "-waitForGuiToStabilize"
  public static final String pn_uiautomatorDaemonWaitForWindowUpdateTimeout  = "-waitForWindowUpdateTimeout"
  public static final String pn_uninstallApk                                 = "-uninstallApk"
  public static final String pn_useApkFixturesDir                            = "-useApkFixturesDir"
  public static final String pn_widgetIndexes                                = "-widgetIndexes"
  // @formatter:on
  //endregion

  public static final String defaultDroidmateOutputDir              = "." + File.separator + "dev1"
  public static final int    defaultActionsLimit                    = 10
  public static final int    defaultResetEveryNthExplorationForward = 0

  //region Cmd line parameters

  @Parameter(names = [Configuration.pn_actionsLimit, "-actions", "-clicks"], description =
    "How many actions the GUI exploration strategy can conduct before terminating.")
  public Integer actionsLimit = defaultActionsLimit

  @Parameter(names = [Configuration.pn_alwaysClickFirstWidget, "-clickFirst"], description =
    "Should the exploration strategy always click the first widget instead of its default more complex behavior")
  public boolean alwaysClickFirstWidget = false

  @Parameter(names = [Configuration.pn_apksLimit, "-limit"],
    description = "Limits the number of apks on which DroidMate will run. 0 means no limit.")
  public int apksLimit = 0

  @Parameter(names = [Configuration.pn_apksNames, "-apks", "-apps"], listConverter = ListOfStringsConverter.class,
    description = "Filters apps on which DroidMate will be run. Supply full file names, separated by commas, surrounded by square brackets. If the list is empty, it will run on all the apps in the apks dir. Example value: [app1.apk, app2.apk]")
  public List<String> apksNames = new ArrayList<>()

  @Parameter(names = [Configuration.pn_apksDir],
    description = "Directory containing the apks to be processed by DroidMate.")
  public String apksDirName = InitConstants.apks_dir

  @Parameter(names = [Configuration.pn_appGuardOnlyApis], arity = 1)
  public boolean appGuardOnlyApis = true

  @Parameter(names = [Configuration.pn_checkAppIsRunningRetryAttempts])
  public int checkAppIsRunningRetryAttempts = 4

  @Parameter(names = [Configuration.pn_checkAppIsRunningRetryDelay])
  public int checkAppIsRunningRetryDelay = 5000

  @Parameter(names = [Configuration.pn_clearPackageRetryAttempts], arity = 1)
  public int clearPackageRetryAttempts = 3

  @Parameter(names = [Configuration.pn_clearPackageRetryDelay], arity = 1)
  public int clearPackageRetryDelay = 1000

  @Parameter(names = [Configuration.pn_compareRuns], arity = 1)
  public boolean compareRuns = false

  @Parameter(names = [Configuration.pn_delayAfterLaunchingActivity])
  public int delayAfterLaunchingActivity = 5000

  @Parameter(names = [Configuration.pn_droidmateOutputDir], description =
    "Path to the directory that will contain DroidMate exploration output.")
  public String droidmateOutputDir = defaultDroidmateOutputDir

  @Parameter(names = [Configuration.pn_deployRawApks], arity = 1,
    description = "Deploys apks to device in 'raw' form, that is, without instrumenting them. Will deploy them raw even if instrumented version is available from last run.")
  public boolean deployRawApks = false

  @Parameter(names = [Configuration.pn_device])
  public int deviceIndex = 0

  @Parameter(names = ["-displayHelp", "-help", "-h", "-?"], help = true, description =
    "Displays command line parameters description.")
  public boolean displayHelp

  @Parameter(names = [Configuration.pn_exploreInteractively], description =
    "Determines if the user should be asked for confirmation before the exploration driver conducts next action on the Android device. If yes, the information about the action about to be taken will be also displayed.")
  public boolean exploreInteractively = false

  @Parameter(names = ["-extractAdditionalData", "-ead"], arity = 1)
  public boolean extractAdditionalData = false

  @Parameter(names = ["-extractData"], description =
    "If present, instead of normal run, DroidMate will deserialize previous exploration output and extract data from it, for example it will extract input data for pgfplots charting tool.")
  public Boolean extractData = false

  @Parameter(names = ["-extractSaturationCharts", "-esc"], arity = 1)
  public boolean extractSaturationCharts = false

  @Parameter(names = ["-extractSummaries"], arity = 1)
  public boolean extractSummaries = true

  @Parameter(names = [Configuration.pn_getValidGuiSnapshotRetryAttempts])
  public int getValidGuiSnapshotRetryAttempts = 10

  @Parameter(names = [Configuration.pn_getValidGuiSnapshotRetryDelay])
  public int getValidGuiSnapshotRetryDelay = 2000

  @Parameter(names = [Configuration.pn_logWidgets])
  public boolean logWidgets = false

  @Parameter(names = ["-logLevel"], description =
    "Logging level of the entirety of application. Possible values, comma separated: trace, debug, info.")
  String logLevel = "trace"

  @Parameter(names = [Configuration.pn_monitorServerStartQueryInterval])
  public int monitorServerStartQueryInterval = 2000

  @Parameter(names = [Configuration.pn_monitorServerStartTimeout])
  public int monitorServerStartTimeout = 20000

  @Parameter(names = ["-outputAppGuardCharts"], arity = 1)
  public boolean outputAppGuardCharts = false

  public Path uiaTestCasesLogsDirPath = Paths.get("./uia_test_cases_logs")

  @Parameter(names = [Configuration.pn_uninstallApk], arity = 1)
  public boolean uninstallApk = true

  @Parameter(names = ["-processUiaLogs"])
  public boolean processUiaTestCasesLogs = false

  @Parameter(names = [Configuration.pn_randomSeed, "-seed"], description =
    "The seed for a random generator used by a random-clicking GUI exploration strategy. If null, a seed will be randomized.")
  public Long randomSeed = null

  @Parameter(names = ["-removeHardCodedApis"], arity = 1)
  public boolean removeHardCodedApis = true

  @Parameter(names = [Configuration.pn_resetEveryNthExplorationForward])
  public int resetEveryNthExplorationForward = defaultResetEveryNthExplorationForward

  @Parameter(names = ["-saturationChartsHours"], description = "The time span covered by the saturation charts data, in hours.")
  public Double saturationChartsHours = 2.0

  @Parameter(names = [Configuration.pn_splitCharts], arity = 1)
  public boolean splitCharts = false

  @Parameter(names = [Configuration.pn_socketTimeout], arity = 1)
  // Has to be hefty as "turn_wifi_on" device action can take 20+ seconds.
  // Also, starting activity might take some times.
  public int socketTimeout = 3 * 60 * 1000 // ms

  @Parameter(names = [Configuration.pn_softReset], arity = 1)
  public boolean softReset = false

  @Parameter(names = [Configuration.pn_uiautomatorDaemonServerStartTimeout], description =
    "How long DroidMate should wait, in milliseconds, for message on logcat confirming that UiAutomatorDaemonServer has started on android (virtual) device.")
  public int uiautomatorDaemonServerStartTimeout = 20000

  @Parameter(names = [Configuration.pn_uiautomatorDaemonServerStartQueryInterval], description =
    "How often DroidMate should query, in milliseconds, for message on logcat confirming that UiDaemonServer has started on android (virtual) device.")
  public int uiautomatorDaemonServerStartQueryInterval = 2000

  @Parameter(names = [Configuration.pn_uiautomatorDaemonWaitForGuiToStabilize], arity = 1, description =
    "Should the uiautomator-daemon wait for GUI state to stabilize after each click performed on the android device. Setting this to false will drastically speedup the clicking process, but will probably result in new clicks being sent while the results of previous one are still being processed.")
  public boolean uiautomatorDaemonWaitForGuiToStabilize = true

  /* Empirical evaluation shows that setting this to 600 will sometimes cause DroidMate to consider GUI stable while it
     actually isn't, yet.
     For more, see: org.droidmate.uiautomatordaemon.UiAutomatorDaemonDriver.waitForGuiToStabilize
   */
  @Parameter(names = [Configuration.pn_uiautomatorDaemonWaitForWindowUpdateTimeout], arity = 1)
  public int uiautomatorDaemonWaitForWindowUpdateTimeout = 1200 // ms

  @Parameter(names = [Configuration.pn_uiautomatorDaemonTcpPort], description =
    "TCP port used by DroidMate to communicate with the android (virtual) device.")
  public int uiautomatorDaemonTcpPort = Constants.UIADAEMON_SERVER_PORT

  @Parameter(names = [Configuration.pn_useApkFixturesDir], arity = 1)
  public boolean useApkFixturesDir = false


  @Parameter(names = [Configuration.pn_timeLimit])
  public int timeLimit = 0

  @Parameter(names = [Configuration.pn_widgetIndexes], listConverter = ListOfIntegersConverter.class,
    description = "Makes the exploration strategy to choose widgets to click that have the indexes as provided by this parameter, in sequence. The format is: [<first widget index>,<second widget index>,...<nth widget index>], starting indexing at 0. Example: [0,7,3]")
  public List<Integer> widgetIndexes = new ArrayList<>()

  @Parameter(names = ["-widgetUniqueStringWithFieldPrecedence"], arity = 1)
  public boolean widgetUniqueStringWithFieldPrecedence = true

  //endregion

  //region Values set by ConfigurationBuilder

  public Path droidmateOutputDirPath

  public Path apksDirPath

  public File monitorApk

  /** The command of the aapt tool from the Android SDK. Configured by {@link ConfigurationBuilder}. */
  public String aaptCommand

  /** The command of the adb tool from the Android SDK. Configured by {@link ConfigurationBuilder}. */
  public String adbCommand

  public File androidSdkDir = LocalInitConstants.android_sdk_dir

  public File appGuardApisList

  /**
   * Jar with uiautomator-daemon location on the file system. The jar is to be deployed on the android (virtual) device
   * to enable GUI actions execution.
   */
  public File uiautomatorDaemonJar

  //endregion
}
