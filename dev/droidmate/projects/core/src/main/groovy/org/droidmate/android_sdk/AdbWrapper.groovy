// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.android_sdk

import com.google.common.base.Splitter
import com.google.common.collect.Iterables
import groovy.util.logging.Slf4j
import org.droidmate.common.BuildConstants
import org.droidmate.common.ISysCmdExecutor
import org.droidmate.common.SysCmdExecutorException
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.AdbWrapperException
import org.droidmate.exceptions.NoAndroidDevicesAvailableException
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Provides clean interface for communication with the Android SDK's Android Debug Bridge (ADB) tool.<br/>
 * <br/>
 * <b>Technical notes</b><br/>
 * The ADB tool is usually located in {@code <android sdk path>/platform-tools/adb.}<br/>
 * Reference: http://developer.android.com/tools/help/adb.html
 *
 * @author Konrad Jamrozik
 */
@Slf4j
public class AdbWrapper implements IAdbWrapper
{

  private final Configuration   cfg
  private       ISysCmdExecutor sysCmdExecutor

  AdbWrapper(
    Configuration cfg,
    ISysCmdExecutor sysCmdExecutor)
  {
    this.cfg = cfg
    this.sysCmdExecutor = sysCmdExecutor
  }

  @Override
  public List<AndroidDeviceDescriptor> getAndroidDevicesDescriptors() throws AdbWrapperException
  {
    assert cfg.adbCommand != null

    String[] stdStreams

    String commandDescription = String
      .format("Executing adb (Android Debug Bridge) to get the list of available Android (Virtual) Devices.")

    try
    {
      stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand, "devices")
    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Calling 'adb devices' failed.", e)
    }

    removeAdbStartedMsgIfPresent(stdStreams)

    List<AndroidDeviceDescriptor> deviceDescriptors = parseDeviceInformation(stdStreams[0])

    if (deviceDescriptors.empty)
      throw new NoAndroidDevicesAvailableException()

    assert deviceDescriptors.size() > 0
    return deviceDescriptors
  }

  static void removeAdbStartedMsgIfPresent(String[] stdStreams)
  {
    List<String> stdoutLines = stdStreams[0].split(System.lineSeparator(), -1) as List<String>
    stdoutLines = stdoutLines.findAll {String it ->
      !it.startsWith("* daemon not running") && !it.startsWith("* daemon started successfully")
    } as List<String>
    stdStreams[0] = stdoutLines.join(System.lineSeparator())
  }

  /**
   * @param adbDevicesCmdStdout Standard output of call to {@code "<android sdk>/platform-tools/adb devices"}
   *
   * @return List of pairs describing the serial number and type (real device/emulator) of each device visible to adb.
   */
  private static List<AndroidDeviceDescriptor> parseDeviceInformation(String adbDevicesCmdStdout)
  {
    Iterable<String> entries = Splitter.on('\n').omitEmptyStrings().trimResults().split(adbDevicesCmdStdout)
    entries = Iterables.skip(entries, 1) // Remove the "List of devices attached" header.

    List<AndroidDeviceDescriptor> deviceDescriptors = new ArrayList<AndroidDeviceDescriptor>()
    for (String entry : entries)
    {
      String deviceSerialNumber = Iterables.getFirst(Splitter.on('\t').split(entry), null)
      if (deviceSerialNumber.startsWith("emulator"))
        deviceDescriptors.add(new AndroidDeviceDescriptor(deviceSerialNumber, true))
      else
        deviceDescriptors.add(new AndroidDeviceDescriptor(deviceSerialNumber, false))
    }

    return deviceDescriptors
  }

  @Override
  public void installApk(String deviceSerialNumber, Path apkToInstall)
    throws AdbWrapperException
  {
    assert cfg.adbCommand != null

    String commandDescription = String
      .format("Executing adb (Android Debug Bridge) to install %s on Android (Virtual) Device.",
      apkToInstall.fileName)

    String[] stdStreams
    try
    {
      stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand, "-s", deviceSerialNumber, "install -r",
        apkToInstall.toAbsolutePath().toString())

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb install' failed. Oh my.", e)
    }

    if (stdStreams[0].contains("[INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES]"))
      throw new AdbWrapperException("Execution of 'adb -s $deviceSerialNumber install -r ${apkToInstall.toAbsolutePath().toString()}' " +
        "resulted in [INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES] being output to stdout. Thus, no app was actually " +
        "installed. Likely reason for the problem: you are trying to install a built in Google app that cannot be uninstalled" +
        "or reinstalled. DroidMate doesn't support such apps.")

    if (stdStreams[0].contains("Failure"))
      throw new AdbWrapperException("Execution of 'adb -s $deviceSerialNumber install -r ${apkToInstall.toAbsolutePath().toString()}' " +
        "resulted in stdout containing 'Failure'. The full stdout:\n$stdStreams[0]")
  }

  @Override
  public void installApk(String deviceSerialNumber, IApk apkToInstall)
    throws AdbWrapperException
  {
    Path apkFile = Paths.get(apkToInstall.absolutePath)
    this.installApk(deviceSerialNumber, apkFile)
  }

  @Override
  public void uninstallApk(String deviceSerialNumber, String apkPackageName, boolean ignoreFailure)
    throws AdbWrapperException
  {
    assert deviceSerialNumber != null
    assert apkPackageName != null

    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to uninstall %s from Android Device with s/n %s.",
        apkPackageName, deviceSerialNumber)

      String[] stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand, "-s",
        deviceSerialNumber, "uninstall", apkPackageName)
      removeAdbStartedMsgIfPresent(stdStreams)

      String stdout = stdStreams[0]

      // "Failure" is what the adb's "uninstall" command outputs when it fails.
      if (!ignoreFailure && stdout.contains("Failure"))
        throw new AdbWrapperException("Failed to uninstall the apk package $apkPackageName.")
      
      if (ignoreFailure && stdout.contains("Failure"))
        log.trace("Ignored failure of uninstalling of $apkPackageName.")

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Calling 'adb uninstall' failed. Oh my.", e)
    }
  }

  @Override
  public void forwardPort(String deviceSerialNumber, int port) throws AdbWrapperException
  {
    log.trace("forwardPort($deviceSerialNumber, $port)")
    assert deviceSerialNumber != null

    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to forward port %s to android device with s/n %s.", port,
        deviceSerialNumber)

      sysCmdExecutor.execute(commandDescription, cfg.adbCommand, "-s", deviceSerialNumber, "forward",
        "tcp:" + String.valueOf(port),
        "tcp:" + String.valueOf(port))

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb forward' failed. Oh my.", e)
    }

  }

  /**
   * <p>
   * {@code adb reverse} works only for Android 5.0+. See:<br/>
   * - http://stackoverflow.com/questions/31525431/getting-error-closed-twice-on-adb-reverse/31526946#31526946<br/>
   * - sacha comment to johnny's answer in:<br/>
   * http://stackoverflow.com/questions/3415797/adb-forward-remote-port-to-local-machine<br/>
   * - http://www.codeka.com.au/blog/2014/11/connecting-from-your-android-device-to-your-host-computer-via-adb<br/>
   * - https://android.googlesource.com/platform/system/core/+/252586941934d23073a8d167ec240b221062505f<br/>
   *
   * @param deviceSerialNumber
   * @param port
   * @throws AdbWrapperException
   */
  @Override
  void reverseForwardPort(String deviceSerialNumber, int port) throws AdbWrapperException
  {
    log.debug("reverseForwardPort($deviceSerialNumber, $port)")
    assert deviceSerialNumber != null

    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to reverse-forward port %s to android device with s/n %s.", port,
        deviceSerialNumber)

      sysCmdExecutor.execute(commandDescription, cfg.adbCommand, "-s", deviceSerialNumber, "reverse",
        "tcp:" + String.valueOf(port),
        "tcp:" + String.valueOf(port))

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb forward' failed. Oh my.", e)
    }

  }

  @Override
  public void reboot(String deviceSerialNumber) throws AdbWrapperException
  {
    assert deviceSerialNumber != null

    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to reboot android device with s/n %s.", deviceSerialNumber)

      sysCmdExecutor.execute(commandDescription, cfg.adbCommand, "-s", deviceSerialNumber, "reboot")

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb reboot' failed. Oh my.", e)
    }
  }


  @Override
  public List<String> readMessagesFromLogcat(String deviceSerialNumber, String messageTag) throws AdbWrapperException
  {
    try
    {
      String commandDescription = "Executing adb (Android Debug Bridge) to read from logcat messages tagged: $messageTag"

      String[] stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        /*
         Command line explanation:
        -d      : Dumps the log to the screen and exits.
        -b main : Loads the "main" buffer.
        -v time : Sets the message output format to time (see [2]).
        *:s     : Suppresses all messages, besides the ones having messageTag.
                  Detailed explanation of the "*:s* filter:
                  * : all messages // except messageTag, overridden by next param, "messageTag"
                  S : SILENT: suppress all messages

        Logcat reference:
          [1] http://developer.android.com/tools/help/logcat.html
          [2] http://developer.android.com/tools/debugging/debugging-log.html#outputFormat

        */
        "logcat -d -b main -v time *:s", messageTag)

      List<String> messages = stdStreams[0].tokenize("\n")*.trim()

      return messages

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException(e)
    }
  }

  @Override
  String listPackages(String deviceSerialNumber) throws AdbWrapperException
  {
    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to list packages.")

      String[] stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell pm list packages")

      String stdout = stdStreams[0]
      return stdout

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException(e)
    }
  }

  @Override
  String listPackage(String deviceSerialNumber, String packageName) throws AdbWrapperException
  {
    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to list package $packageName.")

      String[] stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell pm list packages $packageName")

      String stdout = stdStreams[0]
      return stdout

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException(e)
    }
  }

  @Override
  String ps(String deviceSerialNumber) throws AdbWrapperException
  {
    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to list processes (ps).")

      String[] stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell ps")

      String stdout = stdStreams[0]
      return stdout

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException(e)
    }
  }

  @Override
  public void clearLogcat(String deviceSerialNumber) throws AdbWrapperException
  {
    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to clear logcat output.")

      sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "logcat -c")

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException(e)
    }

  }

  @Override
  public List<String> waitForMessagesOnLogcat(
    String deviceSerialNumber, String messageTag, int minMessagesCount, int waitTimeout, int queryDelay)
    throws AdbWrapperException
  {

    List<String> readMessages = new ArrayList<>()

    try
    {
      int timeLeftToQuery = waitTimeout
      while (timeLeftToQuery >= 0 && readMessages.size() < minMessagesCount)
      {
//        log.verbose("waitForMessagesOnLogcat.sleep(queryDelay=$queryDelay)")
        Thread.sleep(queryDelay)
        timeLeftToQuery -= queryDelay
//        log.verbose("waitForMessagesOnLogcat.readMessagesFromLogcat(messageTag=$messageTag) " +
//          "timeLeftToQuery=$timeLeftToQuery readMessages.size()=${readMessages.size()} minMessagesCount=$minMessagesCount")
        readMessages = this.readMessagesFromLogcat(deviceSerialNumber, messageTag)
      }
    } catch (InterruptedException e)
    {
      throw new AdbWrapperException(e)
    }
//    log.verbose("waitForMessagesOnLogcat loop finished. readMessages.size()=${readMessages.size()}")

    if (readMessages.size() < minMessagesCount)
    {
      throw new AdbWrapperException("Failed waiting for at least $minMessagesCount messages on logcat. " +
        "actual messages count before timeout: ${readMessages.size()},  " +
        "s/n: $deviceSerialNumber, " +
        "messageTag: $messageTag, " +
        "minMessageCount: $minMessagesCount, " +
        "waitTimeout: $waitTimeout, " +
        "queryDelay: $queryDelay")

    }

    assert readMessages.size() >= minMessagesCount
    return readMessages
  }


  @Override
  public void killAdbServer() throws AdbWrapperException
  {
    try
    {
      String commandDescription = String
        .format(
        "Executing adb (Android Debug Bridge) to kill adb server.")

      sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "kill-server")

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb kill-server' failed. Oh my.", e)
    }
  }

  @Override
  public void startAdbServer() throws AdbWrapperException
  {
    Process p
    try
    {
      /* Calling ProcessBuilder() instead of SysCmdExecutor.execute() as it behaves in strange ways, namely:
       - if the server doesn't need to be started, it returns 0
       - if the server needs to be started and timeout is set to 1000ms, it throws exception caused by exit code -1
       - if the server needs to be started and timeout is set to 5000, it hangs, so it seems the timeout has no effect.

       My question on Stack Overflow with some discussion:
       http://stackoverflow.com/questions/17282081/adb-start-server-java-gradle-and-apache-commons-exec-how-to-make
       -it-right/

       Other references:
       http://stackoverflow.com/questions/931536/how-do-i-launch-a-completely-independent-process-from-a-java-program
       http://www.javaworld.com/jw-12-2000/jw-1229-traps.html?page=1
      */

      // .inheritIO() causes the command to write out to stdout if it indeed had to start the server.
      p = new ProcessBuilder(Utils.quoteIfIsPathToExecutable(cfg.adbCommand),
        "start-server").inheritIO().start()

      p.waitFor()

    } catch (IOException e)
    {
      throw new AdbWrapperException("Starting adb server failed, oh my!", e)
    } catch (InterruptedException e)
    {
      throw new AdbWrapperException("Interrupted starting adb server. Oh my!", e)
    }
  }


  @Override
  public void pushJar(String deviceSerialNumber, Path jarFile, String targetFileName = null) throws AdbWrapperException
  {
    assert cfg.adbCommand != null
    assert deviceSerialNumber != null
    // A new path must be created, otherwise this will result in a ProviderMismatchException
    // More information here: http://stackoverflow.com/questions/22611919/why-do-i-get-providermismatchexception-when-i-try-to-relativize-a-path-agains
    assert jarFile != null
    Path path = Paths.get(jarFile.toUri())
    assert Files.exists(path) && !Files.isDirectory(path)

    String commandDescription = String
      .format(
      "Executing adb to push %s on Android Device with s/n %s.",
      jarFile.fileName.toString(), deviceSerialNumber)

    try
    {
      // Executed command based on step 4 from:
      // http://developer.android.com/tools/testing/testing_ui.html#builddeploy
      sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "push", jarFile.toAbsolutePath().toString(), BuildConstants.AVD_dir_for_temp_files + (targetFileName ?: "") )

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb push ...' failed. Oh my.", e)
    }
  }

  @Override
  public void removeJar(String deviceSerialNumber, Path jarFile) throws AdbWrapperException
  {
    assert cfg.adbCommand != null
    assert deviceSerialNumber != null

    String commandDescription = String
      .format(
      "Executing adb to remove %s from Android Device with s/n %s.",
      jarFile.fileName.toString(), deviceSerialNumber)

    try
    {
      // Executed command based on:
      // http://forum.xda-developers.com/showthread.php?t=517874
      //
      // Hint: to list files to manually check if the file was deleted, use: adb shell ls
      sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell", "rm", BuildConstants.AVD_dir_for_temp_files + jarFile.fileName.toString())

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb shell rm ...' failed. Oh my.", e)
    }
  }

  @Override
  public void launchMainActivity(String deviceSerialNumber, String launchableActivityComponentName) throws AdbWrapperException
  {
    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to start main activity on the Android Device.")

      // Reference:
      // http://developer.android.com/tools/help/adb.html#am
      String[] stdStreams = sysCmdExecutor.executeWithTimeout(commandDescription, cfg.launchActivityTimeout, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell am start", // start an activity using Activity Manager (am)
        "-W", // wait for launch to complete
        "-S", // force stop before starting activity
        "-a", "android.intent.action.MAIN", // from package android.content.Intent.ACTION_MAIN
        "-c", "android.intent.category.LAUNCHER", // from package android.content.Intent.CATEGORY_LAUNCHER
        "-n", launchableActivityComponentName)

      String stdout = stdStreams[0]
      String launchMainActivityFailureString = "Error: "

      if (stdout.contains(launchMainActivityFailureString))
      {
        String failureLine = stdout.readLines().find {String line -> line.contains(launchMainActivityFailureString)}

        throw new AdbWrapperException("AdbWrapper.launchMainActivity successfully executed the underlying adb shell command, " +
          "but its stdout contains the failure string of: '$launchMainActivityFailureString'. Full line from the command " +
          "stdout with the failure string:\n" +
          "$failureLine")
      }

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb shell am start <INTENT>' failed. Oh my.", e)
    }
  }

  /**
   * Stops the package process and clears all the data. Source: http://stackoverflow.com/a/3117310/986533
   * @param deviceSerialNumber
   * @param apkPackageName
   */
  @Override
  public boolean clearPackage(String deviceSerialNumber, String apkPackageName) throws AdbWrapperException
  {
    try
    {
      String commandDescription = String
        .format("Executing adb (Android Debug Bridge) to clear package on the Android Device.")

      // WISH what about softer alternative of am force-stop ? See http://stackoverflow.com/questions/3117095/stopping-an-android-app-from-console
      // Reference:
      // http://stackoverflow.com/questions/3117095/stopping-an-android-app-from-console/3117310#3117310
      String[] stdStreams = sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell pm clear", // clear everything associated with a package
        apkPackageName)

      String stdout = stdStreams[0].trim()
      String adbClearPackageFailureStdout = "Failed"
      if (stdout == adbClearPackageFailureStdout)
        throw new AdbWrapperException("adb returned '$adbClearPackageFailureStdout' on stdout when supplied with command 'shell pm clear $apkPackageName'")

      return true

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb shell pm clear <PACKAGE_NAME>' failed. Oh my.", e)
    }
  }

  @Override
  public void startUiautomatorDaemon(String deviceSerialNumber, int port) throws AdbWrapperException
  {
    if (cfg.androidApi == "api19")
      startUiautomatorDaemon_api19(deviceSerialNumber, port)
    else if (cfg.androidApi == "api23")
      startUiautomatorDaemon_api23(deviceSerialNumber, port)
    else throw new UnexpectedIfElseFallthroughError()
  }

  private void startUiautomatorDaemon_api23(String deviceSerialNumber, int port) throws AdbWrapperException
  {
    String commandDescription = String
      .format(
      "Executing adb to start UiAutomatorDaemon service on Android Device with " +
        "s/n %s.",
      deviceSerialNumber)

    String uiaDaemonCmdLine = String.format("-e %s %s -e %s %s -e %s %s",
      UiautomatorDaemonConstants.uiaDaemonParam_waitForGuiToStabilize, cfg.uiautomatorDaemonWaitForGuiToStabilize,
      UiautomatorDaemonConstants.uiaDaemonParam_waitForWindowUpdateTimeout, cfg.uiautomatorDaemonWaitForWindowUpdateTimeout,
      UiautomatorDaemonConstants.uiaDaemonParam_tcpPort, port)

    def testRunner = UiautomatorDaemonConstants.uia2Daemon_testPackageName + "/" + UiautomatorDaemonConstants.uia2Daemon_testRunner
    def failureString = "'adb shell -s $deviceSerialNumber instrument --user 0 $uiaDaemonCmdLine -w $testRunner' failed. Oh my. "
    String[] stdStreams
    try
    {
      stdStreams = this.sysCmdExecutor.executeWithoutTimeout(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell am instrument",
        "--user 0",
        uiaDaemonCmdLine,
        "-w",
        testRunner)

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException(failureString, e)
    }

    validateInstrumentation(stdStreams, failureString)
  }

  private void startUiautomatorDaemon_api19(String deviceSerialNumber, int port) throws AdbWrapperException
  {

    String commandDescription = String
      .format(
      "Executing adb to start UiAutomatorDaemon.init() method on Android Device with " +
        "s/n %s.",
      deviceSerialNumber)

    String uiaDaemonCmdLine = String.format("-c %s -e %s %s -e %s %s -e %s %s",
      UiautomatorDaemonConstants.uiaDaemon_initMethodName,
      UiautomatorDaemonConstants.uiaDaemonParam_waitForGuiToStabilize, cfg.uiautomatorDaemonWaitForGuiToStabilize,
      UiautomatorDaemonConstants.uiaDaemonParam_waitForWindowUpdateTimeout, cfg.uiautomatorDaemonWaitForWindowUpdateTimeout,
      UiautomatorDaemonConstants.uiaDaemonParam_tcpPort, port)

    def failureString = "Executing 'adb -s $deviceSerialNumber shell uiautomator runtest ${cfg.uiautomatorDaemonJar.fileName.toString()} $uiaDaemonCmdLine' failed. Oh my."
    String[] stdStreams
    try
    {
      stdStreams = this.sysCmdExecutor.executeWithoutTimeout(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell uiautomator runtest",
        cfg.uiautomatorDaemonJar.fileName.toString(),
        uiaDaemonCmdLine)

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException(failureString, e)
    }

    validateInstrumentation(stdStreams, failureString)
  }


  private void validateInstrumentation(String[] stdStreams, GString failureString)
  {
    if (stdStreams[0].contains("INSTRUMENTATION_FAILED"))
    {
      throw new AdbWrapperException("Executing " +
        failureString +
        "Reason: stdout contains 'INSTRUMENTATION_FAILED' line. The full stdout:\n${stdStreams[0]}")
    }
  }

  @Override
  void pullFile_api19(String deviceSerialNumber, String pulledFileName, String destinationFilePath) throws AdbWrapperException
  {
    assert deviceSerialNumber != null
    assert pulledFileName?.size() > 0
    assert destinationFilePath?.size() > 0

    assert Files.notExists(Paths.get(destinationFilePath))

    String pulledFilePath = UiautomatorDaemonConstants.deviceLogcatLogDir_api19 + pulledFileName
    String commandDescription = String
      .format(
      "Executing adb to pull file %s from Android Device with s/n %s.",
      pulledFilePath, deviceSerialNumber)

    try
    {
      sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "pull", pulledFilePath, destinationFilePath)

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb pull ...' failed. Oh my.", e)
    }
  }

  @Override
  /**
   * Pull file from the device.
   *
   * Due to Android 6 enhanced security it is necessary to run the software as
   * a specific package to extract a file
   *
   * The command to do that is: adb exec-out run-as <APPLICATION> cat <SOURCE> <DESTINATION>
   * More information:
   *   http://stackoverflow.com/questions/18471780/android-adb-retrieve-database-using-run-as
   */
  void pullFile_api23(String deviceSerialNumber, String pulledFileName, String destinationFilePath, String shellPackageName) throws AdbWrapperException
  {
    assert deviceSerialNumber != null
    assert pulledFileName?.size() > 0
    assert destinationFilePath?.size() > 0
    assert !shellPackageName?.empty

    assert Files.notExists(Paths.get(destinationFilePath))

    String pulledFilePath = UiautomatorDaemonConstants.deviceLogcatLogDir_api23 + pulledFileName
    String commandDescription = String
      .format(
      "Executing adb to pull file %s from Android Device with s/n %s.",
      pulledFilePath, deviceSerialNumber)

    try
    {
      String[] stdStreams = sysCmdExecutor.execute(
        commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "exec-out",
        "run-as", shellPackageName,
        "cat", pulledFilePath)

      FileWriter writer = new FileWriter(destinationFilePath)
      writer.write(stdStreams[0])
      writer.close()

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb pull ...' failed. Oh my.", e)
    }
  }

  void removeFile_api19(String deviceSerialNumber, String fileName) throws AdbWrapperException
  {
    assert deviceSerialNumber != null
    assert fileName != null
    assert fileName.size() > 0

    String filePath = UiautomatorDaemonConstants.deviceLogcatLogDir_api19 + fileName
    String commandDescription = String
      .format(
      "Executing adb to delete file %s from Android Device with s/n %s.",
      filePath, deviceSerialNumber)

    try
    {
      sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell rm", filePath)

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb shell rm ...' failed. Oh my.", e)
    }
  }

  @Override
  /**
   * Remove a file from the device
   *
   * See explanation about the shellPackageName parameter in {@link org.droidmate.android_sdk.AdbWrapper#pullFile_api23}
   */
  void removeFile_api23(String deviceSerialNumber, String fileName, String shellPackageName) throws AdbWrapperException
  {
    assert deviceSerialNumber != null
    assert fileName != null
    assert fileName.size() > 0
    assert !shellPackageName?.empty

    String filePath = UiautomatorDaemonConstants.deviceLogcatLogDir_api23 + fileName
    String commandDescription = String
      .format(
      "Executing adb to delete file %s from Android Device with s/n %s.",
      filePath, deviceSerialNumber)

    try
    {
      sysCmdExecutor.execute(commandDescription, cfg.adbCommand,
        "-s", deviceSerialNumber,
        "shell run-as", shellPackageName,
        "rm", filePath)

    } catch (SysCmdExecutorException e)
    {
      throw new AdbWrapperException("Executing 'adb shell rm ...' failed. Oh my.", e)
    }
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  private debugStdStreams(String[] stdStreams)
  {
    String stdout = stdStreams[0]
    String stderr = stdStreams[1]
    println "=========="
    println "DEBUG STD STREAMS"
    println "===== STD OUT ====="
    println stdout
    println "===== STD ERR ====="
    println stderr
    println "=========="
  }
}
