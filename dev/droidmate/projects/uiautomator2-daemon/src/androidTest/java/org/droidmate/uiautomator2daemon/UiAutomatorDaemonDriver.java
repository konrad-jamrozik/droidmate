// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.uiautomator2daemon;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.*;
import android.util.Log;
import org.apache.commons.io.FileUtils;
import org.droidmate.uiautomator_daemon.DeviceCommand;
import org.droidmate.uiautomator_daemon.DeviceResponse;
import org.droidmate.uiautomator_daemon.UiAutomatorDaemonException;
import org.droidmate.uiautomator_daemon.UiautomatorWindowHierarchyDumpDeviceResponse;
import org.droidmate.uiautomator_daemon.guimodel.GuiAction;

import java.io.File;
import java.io.IOException;

import static org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.*;

// KJA2 (refactoring) dry up common code with uiad-1. Put the shared code in uiautomator-daemon-lib
class UiAutomatorDaemonDriver implements IUiAutomatorDaemonDriver
{
  private final UiDevice device;

  /**
   * Decides if {@link #UiAutomatorDaemonDriver} should wait for the window to go to idle state after each click.
   */
  private final boolean waitForGuiToStabilize;
  private final int     waitForWindowUpdateTimeout;
  private final Context context;

  UiAutomatorDaemonDriver(boolean waitForGuiToStabilize, int waitForWindowUpdateTimeout)
  {
    // The instrumentation required to run uiautomator2-daemon is
    // provided by the command: adb shell instrument <PACKAGE>/<RUNNER>
    Instrumentation instr = InstrumentationRegistry.getInstrumentation();
    if (instr == null) throw new AssertionError();

    this.context = InstrumentationRegistry.getTargetContext();
    if (context == null) throw new AssertionError();
    
    this.device = UiDevice.getInstance(instr);
    if (device == null) throw new AssertionError();
    
    this.waitForGuiToStabilize = waitForGuiToStabilize;
    this.waitForWindowUpdateTimeout = waitForWindowUpdateTimeout;
  }


  @Override
  public DeviceResponse executeCommand(DeviceCommand deviceCommand) throws UiAutomatorDaemonException
  {
    Log.v(uiaDaemon_logcatTag, "Executing device command: " + deviceCommand.command);

    if (deviceCommand.command.equals(DEVICE_COMMAND_STOP_UIADAEMON))
    {
      // The server will be closed after this response is sent, because the given deviceCommand.command will be interpreted
      // in the caller, i.e. SerializableTcpServerBase.
      return new DeviceResponse();
    }

    if (deviceCommand.command.equals(DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP))
      return getWindowHierarchyDump();

    if (deviceCommand.command.equals(DEVICE_COMMAND_GET_IS_ORIENTATION_LANDSCAPE))
      return getIsNaturalOrientation();


    if (deviceCommand.command.equals(DEVICE_COMMAND_PERFORM_ACTION))
      return performAction(deviceCommand);

    if (deviceCommand.command.equals(DEVICE_COMMAND_GET_DEVICE_MODEL))
      return getDeviceModel();

    throw new UiAutomatorDaemonException(String.format("The command %s is not implemented yet!", deviceCommand.command));
  }

  private DeviceResponse getDeviceModel()
  {
    Log.d(uiaDaemon_logcatTag, "getDeviceModel()");
    String model = Build.MODEL;
    String manufacturer = Build.MANUFACTURER;
    DeviceResponse deviceResponse = new DeviceResponse();
    deviceResponse.model = manufacturer + "-" + model;
    Log.d(uiaDaemon_logcatTag, "Device model: "+deviceResponse.model);
    return deviceResponse;
  }

  private String getWifiSwitchWidgetName()
  {
    String deviceModel = this.getDeviceModel().model;

    String switchWidgetName;
    if (deviceModel.equals(DEVICE_SAMSUNG_GALAXY_S3_GT_I9300))
      switchWidgetName = "android:id/switchWidget";
    else if (deviceModel.equals(DEVICE_GOOGLE_NEXUS_7) || deviceModel.equals(DEVICE_GOOGLE_NEXUS_5X))
      switchWidgetName = "com.android.settings:id/switch_widget";
    else
      switchWidgetName = "com.android.settings:id/switchWidget";

    return switchWidgetName;
  }

  private DeviceResponse getIsNaturalOrientation()
  {
    Log.d(uiaDaemon_logcatTag, "Getting 'isNaturalOrientation'");
    this.device.waitForIdle();
    DeviceResponse deviceResponse = new DeviceResponse();
    deviceResponse.isNaturalOrientation = device.isNaturalOrientation();
    return deviceResponse;
  }


  @TargetApi(Build.VERSION_CODES.FROYO)
  private DeviceResponse performAction(DeviceCommand deviceCommand) throws UiAutomatorDaemonException
  {
    Log.v(uiaDaemon_logcatTag, "Performing GUI action");

    GuiAction action = deviceCommand.guiAction;

    if (action.guiActionCommand != null)
    {

      // Explanation for turning off the 'IfCanBeSwitch' inspection:
      // the ant script used for building this source uses Java 1.5 in which switch over strings is not supported.
      //noinspection IfCanBeSwitch
      if (action.guiActionCommand.equals(guiActionCommand_pressBack))
      {
        Log.d(uiaDaemon_logcatTag, "Pressing 'back' button.");
        this.device.pressBack();
        waitForGuiToStabilize();
      } else if (action.guiActionCommand.equals(guiActionCommand_pressHome))
      {
        Log.d(uiaDaemon_logcatTag, "Pressing 'home' button.");
        this.device.pressHome();
        waitForGuiToStabilize();
      } else if (action.guiActionCommand.equals(guiActionCommand_turnWifiOn))
      {
        turnWifiOn();
      } else if (action.guiActionCommand.equals(guiActionCommand_launchApp))
      {
        launchApp(action.resourceId);

      } else
      {
        throw new UiAutomatorDaemonException(String.format("Unrecognized GUI action command: %s",
          action.guiActionCommand));
      }

    } else if (deviceCommand.guiAction.resourceId != null)
    {
      Log.d(uiaDaemon_logcatTag, String.format("Setting text of widget with resource ID %s to %s.", deviceCommand.guiAction.resourceId, deviceCommand.guiAction.textToEnter));
      try
      {
        boolean enterResult = this.device.findObject(
          new UiSelector().resourceId(deviceCommand.guiAction.resourceId)
        ).setText(deviceCommand.guiAction.textToEnter);

        if (enterResult)
          waitForGuiToStabilize();

        if (!enterResult)
          Log.w(uiaDaemon_logcatTag, String.format(
            "Failed to enter text in widget with resource id: %s", deviceCommand.guiAction.resourceId));

      } catch (UiObjectNotFoundException e)
      {
        throw new AssertionError("Assertion error:  UIObject not found. ResourceId: " + deviceCommand.guiAction.resourceId);
      }
    } else
    {
      int clickXCoor = deviceCommand.guiAction.clickXCoor;
      int clickYCoor = deviceCommand.guiAction.clickYCoor;

      Log.d(uiaDaemon_logcatTag, String.format("Clicking on (x,y) coordinates of (%d,%d)", clickXCoor, clickYCoor));

      if (clickXCoor < 0) throw new AssertionError("assert clickXCoor >= 0");
      if (clickYCoor < 0) throw new AssertionError("assert clickYCoor >= 0");

      if (clickXCoor > this.device.getDisplayWidth())
        throw new AssertionError("assert clickXCoor <= device.getDisplayWidth()");
      if (clickYCoor > this.device.getDisplayHeight())
        throw new AssertionError("assert clickXCoor <= device.getDisplayHeight()");

      // WISH return clickResult in deviceResponse, so we can try to click again on 'app has stopped' and other dialog boxes. Right now there is just last chance attempt in org.droidmate.exploration.VerifiableDeviceActionsExecutor.executeAndVerify()
      boolean clickResult;
      clickResult = click(deviceCommand, clickXCoor, clickYCoor);
      if (!clickResult)
      {
        Log.d(uiaDaemon_logcatTag, (String.format("The operation device.click(%d, %d) failed (the 'click' method returned 'false'). Retrying after 2 seconds.", clickXCoor, clickYCoor)));

        try
        {
          Thread.sleep(2000);
        } catch (InterruptedException e)
        {
          Log.w(uiaDaemon_logcatTag, "InterruptedException while sleeping before repeating a click.");
        }

        clickResult = click(deviceCommand, clickXCoor, clickYCoor);

        // WISH what does it actually mean that click failed?
        if (!clickResult)
        {
          Log.w(uiaDaemon_logcatTag, (String.format("The operation ui.getUiDevice().click(%d, %d) failed for the second time. Giving up.", clickXCoor, clickYCoor)));
        }
        else
          Log.d(uiaDaemon_logcatTag, "The click retry attempt succeeded.");
      }
    }

    DeviceResponse deviceResponse = new DeviceResponse();
    deviceResponse.isNaturalOrientation = this.device.isNaturalOrientation();

    return deviceResponse;
  }


  /**
   * Based on: http://stackoverflow.com/a/12420590/986533
   */
  private void turnWifiOn()
  {
    Log.d(uiaDaemon_logcatTag, "Ensuring WiFi is turned on.");
    WifiManager wfm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    
    boolean wifiEnabled = wfm.setWifiEnabled(true);

    if (!wifiEnabled)
      Log.w(uiaDaemon_logcatTag, "Failed to ensure WiFi is enabled!");
  }

  private boolean click(DeviceCommand deviceCommand, int clickXCoor, int clickYCoor)
  {
    boolean clickResult;
    if (deviceCommand.guiAction.longClick)
      clickResult = this.device.swipe(clickXCoor, clickYCoor, clickXCoor, clickYCoor, 100); // 100 ~ 2s. Empirical evaluation.
    else
      clickResult = this.device.click(clickXCoor, clickYCoor);

    if (clickResult)
      waitForGuiToStabilize();

    return clickResult;
  }

  // WISH maybe waitForIdle can be set by http://developer.android.com/tools/help/uiautomator/Configurator.html#setWaitForIdleTimeout%28long%29

  /**
   * <p>
   * Waits until GUI gets into a state in which it is reasonable to expect it won't change, so DroidMate's exploration can
   * proceed, by analyzing the GUI etc.
   *
   * </p><p>
   * The method first waits for idle [wfi], then repeatedly both waits for window update [wfwu] and waits for idle, until the
   * window update times out and waits for idle returns immediately.
   *
   * </p><p>
   * The first call to 'wait for idle' is made to catch any ongoing GUI changes. A call to 'wait for window update' is made
   * to wait for the GUI to react to any click [clck] that was potentially made just before this method was called.
   * The next call to 'wait for idle' is made to wait for the GUI to receive any pending ongoing events coming after
   * the window update. The process is then looped starting from 'wait for window update' to double-ensure the method didn't
   * considered the GUI stable while there were some events incoming.
   *
   * <br/>
   * -----<br/>
   * </p><p>
   * 'Wait for idle' will return as soon as at least 500 ms have passed since the GUI received last accessibility event,
   * because 'wait for idle' [wfi] will call [wfi2] which will call [wfi3] with 500ms [qti].
   *
   * </p><p>
   * If the GUI was already idle at the call to 'wait for idle', it will return immediately or after 1 or 2 ms, which is caused
   * by clock imprecision.
   *
   * <br/>
   * -----<br/>
   * </p>
   * [wfi]: {@link android.support.test.uiautomator.UiDevice#waitForIdle()}<br/>
   * [wfi2]: {@code android.support.test.uiautomator.UiAutomatorBridge#waitForIdle(long)}<br/>
   * [wfi3]: {@link android.app.UiAutomation#waitForIdle(long, long)}<br/>
   * [qti]: {@code android.support.test.uiautomator.UiAutomatorBridge#QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE}<br/>
   * [wfwu]: {@link android.support.test.uiautomator.UiDevice#waitForWindowUpdate(String, long)}<br/>
   * [wue]: {@link android.view.accessibility.AccessibilityEvent#TYPE_WINDOW_CONTENT_CHANGED}<br/>
   * [clck]: {@link android.support.test.uiautomator.UiDevice#click(int, int)}<br/>
   */
  private void waitForGuiToStabilize()
  {


    if (waitForGuiToStabilize)
    {
      Log.v(uiaDaemon_logcatTag, "Waiting for GUI to stabilize.");

      /* If we would like to extends wait for idle time to more than 500 ms, here are possible ways to do it:

         - http://developer.android.com/tools/help/uiautomator/Configurator.html

         - Use android.app.UiAutomation.waitForIdle but getting instance of UiAutomation requires instrumenting
         the app under exploration:
         https://developer.android.com/about/versions/android-4.3.html#Testing

         Use reflection to get com.android.uiautomator.core.UiDevice#getAutomatorBridge and call the internal waitForIdle.
         - http://stackoverflow.com/questions/880365/any-way-to-invoke-a-private-method

       */

      long initialWaitForIdleStartTime = System.currentTimeMillis();
      this.device.waitForIdle();
      long initialWaitForIdleWaitTime = System.currentTimeMillis() - initialWaitForIdleStartTime;
      Log.v(uiaDaemon_logcatTag, "waitForGuiToStabilize: initial waitForIdle took " + initialWaitForIdleWaitTime + "ms");

      boolean wfwuReachedTimeout;
      boolean wfiReturnedImmediately;
      int iteration = 0;
      int maxIterations = 10;
      do
      {
        iteration++;
        wfwuReachedTimeout = waitForWindowUpdate(iteration);
        wfiReturnedImmediately = waitForIdle(iteration);
      } while
        (!guiStabilized(wfwuReachedTimeout, wfiReturnedImmediately)
        && !guiStabilizationAttemptsExhausted(iteration, maxIterations));

      if (guiStabilizationAttemptsExhausted(iteration, maxIterations))
        Log.w(uiaDaemon_logcatTag, "GUI failed to stabilize. Continuing nonetheless.");
      else
        Log.d(uiaDaemon_logcatTag, "GUI stabilized after " + iteration + " iterations / " + (System.currentTimeMillis() - initialWaitForIdleStartTime) + "ms");

    } else
    {
      Log.v(uiaDaemon_logcatTag, "Skipped waiting for GUI to stabilize.");
    }

  }

  private boolean guiStabilizationAttemptsExhausted(int waitForStabilizationLoopIteration, int maxWaitForStabilizationLoopIterations)
  {
    return waitForStabilizationLoopIteration >= maxWaitForStabilizationLoopIterations;
  }

  private boolean guiStabilized(boolean waitForWindowUpdateReachedTimeout, boolean waitForIdleReturnedImmediately)
  {
    return waitForWindowUpdateReachedTimeout && waitForIdleReturnedImmediately;
  }

  private boolean waitForWindowUpdate(int i)
  {
    boolean waitForWindowUpdateReachedTimeout;
    long waitForWindowUpdateStartTime = System.currentTimeMillis();
    this.device.waitForWindowUpdate(null, waitForWindowUpdateTimeout);
    long waitForWindowUpdateWaitTime = System.currentTimeMillis() - waitForWindowUpdateStartTime;
    Log.v(uiaDaemon_logcatTag, "waitForGuiToStabilize: iteration " + i + " waitForWindowUpdate took " + waitForWindowUpdateWaitTime + "ms");

    waitForWindowUpdateReachedTimeout = waitForWindowUpdateWaitTime >= waitForWindowUpdateTimeout;
    return waitForWindowUpdateReachedTimeout;
  }

  private boolean waitForIdle(int i)
  {
    boolean waitForIdleReturnedImmediately;
    long waitForIdleStartTime = System.currentTimeMillis();
    this.device.waitForIdle();
    long waitForIdleWaitTime = System.currentTimeMillis() - waitForIdleStartTime;
    Log.v(uiaDaemon_logcatTag, "waitForGuiToStabilize: iteration " + i + " waitForIdle took " + waitForIdleWaitTime + "ms");

    waitForIdleReturnedImmediately = waitForIdleWaitTime <= 2;
    return waitForIdleReturnedImmediately;
  }

  /*
    Possible programmatic alternatives to getting GUI dump from XML:

    - inherit from http://developer.android.com/reference/android/accessibilityservice/AccessibilityService.html
    - modify appguard loader to insert our custom code to the apk under exploration and obtain the window hierarchy from
    Window Manager Service (or something like that, legacy aut-addon instrumentation buried in the repo has p.o.co code for that)
   */
  private UiautomatorWindowHierarchyDumpDeviceResponse getWindowHierarchyDump() throws UiAutomatorDaemonException
  {
    Log.d(uiaDaemon_logcatTag, "Getting window hierarchy dump");

    String windowDumpFileName = "window_hierarchy_dump.xml";
    File windowDumpFile = prepareWindowDumpFile(windowDumpFileName);

    dumpWindowHierarchyProtectingAgainstException(windowDumpFile);

    String windowHierarchyDump;
    try
    {
      windowHierarchyDump = FileUtils.readFileToString(windowDumpFile);
    } catch (IOException e)
    {
      throw new UiAutomatorDaemonException(e);
    }

    int width = this.device.getDisplayWidth();
    int height = this.device.getDisplayHeight();
    /* We don't make calls to:
     ui.getUiDevice().getCurrentActivityName();
     ui.getUiDevice().getCurrentPackageName();
     due to the performance reasons.
     Instead, package name is taken from the window hierarchy XML dump and activity is ignored as of now. Later on
     the activity will be taken programmatically from AutAddon.
     See: http://stackoverflow.com/questions/3873659/android-how-can-i-get-the-current-foreground-activity-from-a-service
     */

    return new UiautomatorWindowHierarchyDumpDeviceResponse(windowHierarchyDump, width, height);
  }

  /**
   * <p>
   * There is a bug in com.android.uiautomator.core.UiDevice#dumpWindowHierarchy(java.lang.String)
   * that sometimes manifest itself with an Exception. This method  protects against it, making a couple of
   * attempts at getting the dump and if all of them fail, throwing an
   * {@link UiAutomatorDaemonException}.
   *
   * </p><p>
   * Example stack trace of possible NPE:<br/>
   * <code>
   * java.lang.NullPointerException: null<br/>
   * at com.android.uiautomator.core.AccessibilityNodeInfoDumper.childNafCheck(AccessibilityNodeInfoDumper.java:200)<br/>
   * at com.android.uiautomator.core.AccessibilityNodeInfoDumper.nafCheck(AccessibilityNodeInfoDumper.java:180)<br/>
   * at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:104)<br/>
   * at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:129)<br/>
   * (...)<br/>
   * at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpWindowToFile(AccessibilityNodeInfoDumper.java:89)<br/>
   * at com.android.uiautomator.core.UiDevice.dumpWindowHierarchy(UiDevice.java:<obsolete line number here>)
   * </code>
   * </p><p>
   * Example stack trace of possible IllegalArgumentException: of an exception that occurred on Snapchat 5.0.27.3 (July 3, 2014):
   *
   * </p><p>
   *
   * <code>
   * java.lang.IllegalArgumentException: Illegal character (d83d)<br/>
   * at org.kxml2.io.KXmlSerializer.reportInvalidCharacter(KXmlSerializer.java:144) ~[na:na]<br/>
   * at org.kxml2.io.KXmlSerializer.writeEscaped(KXmlSerializer.java:130) ~[na:na]<br/>
   * at org.kxml2.io.KXmlSerializer.attribute(KXmlSerializer.java:465) ~[na:na]<br/>
   * at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpNodeRec(AccessibilityNodeInfoDumper.java:111) ~[na:na]<br/>
   * (...)<br/>
   * at com.android.uiautomator.core.AccessibilityNodeInfoDumper.dumpWindowToFile(AccessibilityNodeInfoDumper.java:89) ~[na:na]<br/>
   * at com.android.uiautomator.core.UiDevice.dumpWindowHierarchy(UiDevice.java:768) ~[na:na]<br/>
   * at org.droidmate.uiautomatordaemon.UiAutomatorDaemonDriver.tryDumpWindowHierarchy(UiAutomatorDaemonDriver.java:420) ~[na:na]<br/>
   * (...)<br/>
   * </code>
   * </p><p>
   *
   * Exploration log snippet of the IllegalArgumentException:
   *
   * </p><p>
   * <code>
   * 2015-02-20 19:44:21.113 DEBUG o.d.e.VerifiableDeviceActionsExecutor    - Performing verifiable device action: <click on LC? 0 Wdgt:View/""/""/[570,233], no expectations><br/>
   * 2015-02-20 19:44:23.958 DEBUG o.d.exploration.ApiLogcatLogsReader      - Current API logs read count: 0<br/>
   * 2015-02-20 19:44:24.040 ERROR o.d.e.ExplorationOutputCollector         - Abrupt exploration end. Caught exception thrown during exploration of com.snapchat.android. Exception message: Device returned DeviceResponse with non-null throwable, indicating something exploded on the A(V)D. The exception is given as a cause of this one. If it doesn't have enough information, try inspecting the logcat output of the A(V)D.
   * </code>
   * </p><p>
   * Discussion: https://groups.google.com/forum/#!topic/appium-discuss/pkDcLx0LyWQ
   *
   * </p><p>
   * Issue tracker: https://code.google.com/p/android/issues/detail?id=68419
   *
   * </p>
   */
  private void dumpWindowHierarchyProtectingAgainstException(File windowDumpFile) throws UiAutomatorDaemonException
  {
    int dumpAttempts = 5;
    int dumpAttemptsLeft = dumpAttempts;
    boolean dumpSucceeded;
    do
    {
      dumpSucceeded = tryDumpWindowHierarchy(windowDumpFile);
      dumpAttemptsLeft--;

      if (!dumpSucceeded)
      {
        if (dumpAttemptsLeft == 1)
        {
          Log.w(uiaDaemon_logcatTag, "UiDevice.dumpWindowHierarchy() failed. Attempts left: 1. Pressing home screen button.");
          // Countermeasure for "Illegal character (d83d)". See the doc of this method and
          // https://hg.st.cs.uni-saarland.de/issues/981
          this.device.pressHome();
        } else
        {
          Log.w(uiaDaemon_logcatTag, "UiDevice.dumpWindowHierarchy() failed. Attempts left: " + dumpAttemptsLeft);
        }
        try
        {
          Thread.sleep(2000);
        } catch (InterruptedException e)
        {
          Log.e(uiaDaemon_logcatTag, "Sleeping between tryDumpWindowHierarchy attempts was interrupted!");
        }
      }

    } while (!dumpSucceeded && dumpAttemptsLeft > 0);

    if (dumpAttemptsLeft <= 0)
    {
      Log.w(uiaDaemon_logcatTag, "UiDevice.dumpWindowHierarchy() failed. No attempts left. Throwing UiAutomatorDaemonException.");
      throw new UiAutomatorDaemonException(String.format("All %d tryDumpWindowHierarchy(%s) attempts exhausted.", dumpAttempts, windowDumpFile));
    }
  }

  /**
   * @see #dumpWindowHierarchyProtectingAgainstException
   */
  // Note on read/write permissions:
  //   http://stackoverflow.com/questions/23527767/open-failed-eacces-permission-denied
  private boolean tryDumpWindowHierarchy(File windowDumpFile)
  {
    try
    {
      Log.d(uiaDaemon_logcatTag, String.format("Trying to create dump file '%s'", windowDumpFile.toString()));
      Log.d(uiaDaemon_logcatTag, "Executing dump");
      this.device.dumpWindowHierarchy(windowDumpFile);
      Log.d(uiaDaemon_logcatTag, "Dump executed");

      if (windowDumpFile.exists())
      {
        return true;
      } else
      {
        Log.w(uiaDaemon_logcatTag, ".dumpWindowHierarchy returned, but the dumped file doesn't exist!");
        return false;
      }

    } catch (NullPointerException e)
    {
      Log.w(uiaDaemon_logcatTag, "Caught NPE while dumping window hierarchy. Msg: " + e.getMessage());
      return false;
    } catch (IllegalArgumentException e)
    {
      Log.w(uiaDaemon_logcatTag, "Caught IllegalArgumentException while dumping window hierarchy. Msg: " + e.getMessage());
      return false;
    } catch (IOException e)
    {
      Log.w(uiaDaemon_logcatTag, "Caught IOException while dumping window hierarchy. Msg: " + e.getMessage());
      return false;
    }
  }

  private File prepareWindowDumpFile(String fileName) throws UiAutomatorDaemonException
  {
    // Replaced original location for application directory due to the following access denied exception:
    //    Caught IOException while dumping window hierarchy.
    //       Msg: /data/local/tmp/window_hierarchy_dump.xml: open failed: EACCES (Permission denied)
    // More information in: http://stackoverflow.com/questions/23424602/android-permission-denied-for-data-local-tmp

    final File dir = context.getFilesDir();
    File file = new File(dir, fileName);

    Log.d(uiaDaemon_logcatTag, String.format("Dump data directory: %s", dir.toString()));
    Log.d(uiaDaemon_logcatTag, String.format("Dump data file: %s", file.toString()));

    // Here we ensure the directory of the target file exists.
    if (!dir.isDirectory())
      if (!dir.mkdirs())
        throw new UiAutomatorDaemonException("!windowDumpDir.isDirectory() && !windowDumpDir.mkdirs()");

    // Here we ensure the target file doesn't exist.
    if (file.isDirectory())
      throw new UiAutomatorDaemonException("windowDumpFile.isDirectory()");
    if (file.exists())
      if (!file.delete())
        throw new UiAutomatorDaemonException("windowDump.exists() && !windowDump.delete()");

    // Here we check if we ensured things correctly.
    if (file.exists())
    {
      throw new AssertionError("Following assertion failed: !windowDump.exists()");
    }
    if (!(file.getParentFile().isDirectory()))
    {
      throw new AssertionError("Following assertion failed: windowDump.getParentFile().isDirectory()");
    }

    return file;
  }

  //region Launching app
  private void launchApp(String appLaunchIconText) throws UiAutomatorDaemonException
  {
    Log.d(uiaDaemon_logcatTag, "Launching app by navigating to and clicking icon with text "+appLaunchIconText);

    boolean clickResult;
    try
    {
      UiObject app = navigateToAppLaunchIcon(appLaunchIconText);
      Log.v(uiaDaemon_logcatTag, "Pressing the " + appLaunchIconText + " app icon to launch it.");
      clickResult = app.clickAndWaitForNewWindow();

    } catch (UiObjectNotFoundException e)
    {
      Log.w(uiaDaemon_logcatTag,
        String.format("Attempt to navigate to and click on the icon labeled '%s' to launch the app threw an exception: %s: %s",
          appLaunchIconText, e.getClass().getSimpleName(), e.getLocalizedMessage()));
      Log.d(uiaDaemon_logcatTag, "Pressing 'home' button after failed app launch.");
      this.device.pressHome();
      waitForGuiToStabilize();
      return;
    }

    if (clickResult)
        waitForGuiToStabilize();
    else
      Log.w(uiaDaemon_logcatTag, (String.format("A click on the icon labeled '%s' to launch the app returned false", appLaunchIconText)));
  }


  private UiObject navigateToAppLaunchIcon(String appLaunchIconName) throws UiObjectNotFoundException
  {
    // Simulate a short press on the HOME button.
    this.device.pressHome();

    // We’re now in the home screen. Next, we want to simulate
    // a user bringing up the All Apps screen.
    // If you use the uiautomatorviewer tool to capture a snapshot
    // of the Home screen, notice that the All Apps button’s
    // content-description property has the value "Apps".  We can
    // use this property to create a UiSelector to find the button.
    UiObject allAppsButton = this.device.findObject(new UiSelector().description("Apps"));

    // Simulate a click to bring up the All Apps screen.
    allAppsButton.clickAndWaitForNewWindow();


    // In the All Apps screen, the app launch icon is located in
    // the Apps tab. To simulate the user bringing up the Apps tab,
    // we create a UiSelector to find a tab with the text
    // label "Apps".
    UiObject appsTab = this.device.findObject(new UiSelector().text("Apps"));

    // Simulate a click to enter the Apps tab.
    appsTab.click();

    // Next, in the apps tabs, we can simulate a user swiping until
    // they come to the app launch icon. Since the container view
    // is scrollable, we can use a UiScrollable object.
    UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));

    // Set the swiping mode to horizontal (the default is vertical)
    appViews.setAsHorizontalList();

    // Create a UiSelector to find the app launch icon and simulate
    // a user click to launch the app.
    return appViews.getChildByText(
      new UiSelector().className(android.widget.TextView.class.getName()),
      appLaunchIconName);
  }

  //endregion
}
