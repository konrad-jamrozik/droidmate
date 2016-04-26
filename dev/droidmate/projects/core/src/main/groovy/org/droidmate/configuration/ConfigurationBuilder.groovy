// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.configuration

import ch.qos.logback.classic.Level
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.konradjamrozik.ResourcePath
import groovy.transform.Memoized
import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.apache.commons.lang3.builder.StandardToStringStyle
import org.droidmate.common.FileUtils
import org.droidmate.exceptions.ConfigurationException
import org.droidmate.init.InitConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.management.ManagementFactory
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

import static org.droidmate.common.logging.Markers.runData

/**
 * @see IConfigurationBuilder#build(java.lang.String [ ], java.nio.file.FileSystem)
 */
@Slf4j

class ConfigurationBuilder implements IConfigurationBuilder
{
/**
 * <p>
 * <i> --- This doc was last reviewed on 14 Sep 2013.</i>
 * </p><p>
 *
 * Given command line arguments, returns {@link Configuration} instance, which is being injected to constructors
 * of most of DroidMate classes.
 * </p><p>
 * The arguments are parsed. If request to display help is detected, it is displayed on stdout and the JVM terminates.
 * Otherwise, the arguments are partially validated, e.g. resource files are checked for existence. Some properties
 * are constructed from the arguments (e.g. absolute file paths are constructed from parent path and relative path).
 * Finally, the result is returned.
 *
 * </p><p>
 * The underlying framework used for command line parsing is jCommander [1].
 *
 * </p><p>
 * [1]: http://jcommander.org/
 *
 * </p>
 *
 * @see Configuration
 */
  @Override
  public Configuration build(String[] args, FileSystem fs = FileSystems.default) throws ConfigurationException
  {
    // Groovy bug: the @Memoized annotation causes cryptic compilation error if the args type is String[], so it is List instead.
    return memoizedBuildConfiguration(args as List, fs)
  }

  @Memoized
  private static Configuration memoizedBuildConfiguration(List<String> args, FileSystem fs)
  {
    log.debug("memoizedBuildConfiguration(args, fs)")

    assert args != null

    Configuration config = new Configuration(args as String[])

    JCommander jCommander
    jCommander = populateConfigurationWithArgs(args as String[], config)

    ifRequestedDisplayHelpAndExit(config, jCommander)
    assert !config.displayHelp: "DroidMate was instructed to display help. By now, it should have done it and exited, " +
      "but instead of exiting the code execution reached this assertion."

    config = bindAndValidate(config, InitConstants.build_tools_version, fs)

    assert config != null
    return config
  }

  public static JCommander populateConfigurationWithArgs(
    String[] args, Configuration config)
  {
    JCommander jCommander

    try
    {
      jCommander = new JCommander()
      jCommander.addObject(config)
      jCommander.parse(args)
    }
    catch (ParameterException e)
    {
      throw new ConfigurationException(e)
    }
    return jCommander
  }

  private static void ifRequestedDisplayHelpAndExit(Configuration config, JCommander jCommander)
  {
    if (config.displayHelp)
    {
      log.info("Detected request to display help. Displaying help & terminating.")

      jCommander.usage()

      System.exit(0)
    }
  }

  private static Configuration bindAndValidate(
    Configuration config, String buildToolsVersion, FileSystem fs) throws ConfigurationException
  {
    assert config != null

    try
    {
      setLogbackRootLoggerLoggingLevel(config)
      bindDirsAndResources(config, fs)
      createAndValidateDirs(config)
      validateExplorationSettings(config)
      bindToolsCommands(config, buildToolsVersion)

      // This increment is done so each connected device will have its uiautomator-daemon reachable on a separate port.
      config.uiautomatorDaemonTcpPort += config.deviceIndex

    } catch (ConfigurationException e)
    {
      throw e
    }

    logConfigurationInEffect(config)

    assert config != null
    return config
  }

  static void validateExplorationSettings(Configuration cfg)
  {
    validateExplorationStrategySettings(cfg)

    List<String> apkNames = Files.list(cfg.apksDirPath)
      .findAll {it.toString().endsWith(".apk")}
      .collect {Path it -> it.fileName.toString()}

    if (cfg.deployRawApks && ["inlined", "monitored"].any {apkNames.contains(it)})
      throw new ConfigurationException(
        "DroidMate was instructed to deploy raw apks, while the apks dir contains an apk " +
          "with 'inlined' or 'monitored' in its name. Please do not mix such apk with raw apks in one dir.\n" +
          "The searched apks dir path: ${cfg.apksDirPath.toAbsolutePath().toString()}")
  }

  private static void validateExplorationStrategySettings(Configuration cfg)
  {
    int settingsCount = widgetClickingStrategySettingsCount(cfg)

    if (settingsCount > 1)
      throw new ConfigurationException("Exploration strategy has been configured in too many different ways. Only one of the following expressions can be true:\n" +
        "alwaysClickFirstWidget: ${cfg.alwaysClickFirstWidget}\n" +
        "randomSeed != null: ${cfg.randomSeed != null}\n" +
        "widgetIndexes != null: ${cfg.widgetIndexes != null}")

    if (cfg.randomSeed == null)
    {
      cfg.randomSeed = new Random().nextLong()
      log.info("Generated random seed: $cfg.randomSeed")
    }
  }

  static int widgetClickingStrategySettingsCount(Configuration cfg)
  {
    return [
      cfg.alwaysClickFirstWidget,
      cfg.widgetIndexes?.size() > 0
    ].collect {it ? 1 : 0}.sum() as int
  }


  private static void bindDirsAndResources(Configuration cfg, FileSystem fs) throws ConfigurationException
  {
    cfg.appGuardApisList = new ResourcePath(InitConstants.appGuardApisList.fileName.toString())

    cfg.uiautomatorDaemon = new ResourcePath("uiautomator2-daemon.apk")

    cfg.uiautomatorDaemonTest = new ResourcePath("uiautomator2-daemon-test.apk")

    cfg.monitorApk = new ResourcePath("monitor.apk")

    cfg.droidmateOutputDirPath = fs.getPath(cfg.droidmateOutputDir)

    cfg.reportInputDirPath = fs.getPath(cfg.reportInputDir)

    cfg.reportOutputDirPath = fs.getPath(cfg.reportOutputDir)

    if (cfg.useApkFixturesDir)
      cfg.apksDirPath = new ResourcePath(InitConstants.apk_fixtures).path
    else
      cfg.apksDirPath = fs.getPath(cfg.apksDirName.toString())
  }

  private static void createAndValidateDirs(Configuration cfg)
  {
    if (!Files.exists(cfg.droidmateOutputDirPath))
      Files.createDirectory(cfg.droidmateOutputDirPath)

    FileUtils.validateDirectory(cfg.androidSdkDir)
    FileUtils.validateDirectory(cfg.apksDirPath)
    FileUtils.validateDirectory(cfg.droidmateOutputDirPath)
  }

  private static void setLogbackRootLoggerLoggingLevel(Configuration config) throws ConfigurationException
  {
    ch.qos.logback.classic.Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
    ch.qos.logback.classic.Logger explorationLogger = LoggerFactory.getLogger("org.droidmate.exploration") as ch.qos.logback.classic.Logger

    if (config.logLevel.toLowerCase() in ["info", "debug", "trace"])
    {
      rootLogger.setLevel(Level.toLevel(config.logLevel))
      explorationLogger.setLevel(Level.toLevel(config.logLevel))
    } else
      throw new ConfigurationException(String.format(
        "Unrecognized logging level. Given level: %s. Expected one of levels: info debug trace",
        config.logLevel))
  }

  private static void bindToolsCommands(Configuration config, String buildToolsVersion)
  {
    config.with {

      aaptCommand = FilenameUtils.concat(config.androidSdkDir.toString(), "build-tools/$buildToolsVersion/aapt")
      adbCommand = FilenameUtils.concat(config.androidSdkDir.toString(), "platform-tools/adb")

      if (SystemUtils.IS_OS_WINDOWS)
      {
        aaptCommand += ".exe"
        adbCommand += ".exe"
      }

      if (!new File(aaptCommand).isFile())
        throw new ConfigurationException("$aaptCommand file doesn't exist or is not a file. Expected path: "
          + aaptCommand)

      if (!new File(adbCommand).isFile())
        throw new ConfigurationException("$adbCommand file doesn't exist or is not a file. Expected path: "
          + adbCommand)
    }
  }

  /*
   * To keep the source DRY, we use apache's ReflectionToStringBuilder, which gets the field names and values using
   * reflection.
   */

  private static void logConfigurationInEffect(Configuration config)
  {

    // The customized display style strips the output of any data except the field name=value pairs.
    StandardToStringStyle displayStyle = new StandardToStringStyle()
    displayStyle.setArrayContentDetail(true)
    displayStyle.setUseClassName(false)
    displayStyle.setUseIdentityHashCode(false)
    displayStyle.setContentStart("")
    displayStyle.setContentEnd("")
    displayStyle.setFieldSeparator("\n")

    String configurationDump = new ReflectionToStringBuilder(config, displayStyle).toString()
    configurationDump = configurationDump.tokenize('\n').sort().join("\n")

    log.info(runData, "--------------------------------------------------------------------------------")
    log.info(runData, "")
    log.info(runData, "Working dir:   ${System.getProperty("user.dir")}")
    log.info(runData, "")
    log.info(runData, "JVM arguments: ${readJVMarguments()}")
    log.info(runData, "")
    log.info(runData, "Configuration dump:")
    log.info(runData, "")
    log.info(runData, configurationDump)
    log.info(runData, "")
    log.info(runData, "End of configuration dump")
    log.info(runData, "")
    log.info(runData, "--------------------------------------------------------------------------------")

  }

  /**
   * Based on: http://stackoverflow.com/a/1531999/986533
   */
  private static List<String> readJVMarguments()
  {
    return ManagementFactory.runtimeMXBean.inputArguments
  }

}