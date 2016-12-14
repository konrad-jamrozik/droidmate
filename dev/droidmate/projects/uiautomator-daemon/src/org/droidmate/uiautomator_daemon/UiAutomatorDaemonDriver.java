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

package org.droidmate.uiautomator_daemon;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import org.apache.commons.io.FileUtils;
import org.droidmate.uiautomator_daemon.guimodel.GuiAction;

import java.io.File;
import java.io.IOException;

import static org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants.*;

public class UiAutomatorDaemonDriver implements IUiAutomatorDaemonDriver
{
  private final UiAutomatorTestCase ui;

  /**
   * Decides if {@link #UiAutomatorDaemonDriver} should wait for the window to go to idle state after each click.
   */
  private final boolean waitForGuiToStabilize;
  private final int     waitForWindowUpdateTimeout;

  public UiAutomatorDaemonDriver(UiAutomatorTestCase uiAutomatorTestCase, boolean waitForGuiToStabilize, int waitForWindowUpdateTimeout)
  {
    this.ui = uiAutomatorTestCase;
    this.waitForGuiToStabilize = waitForGuiToStabilize;
    this.waitForWindowUpdateTimeout = waitForWindowUpdateTimeout;
  }

  // WISH DRY-up duplicates
  @SuppressWarnings("Duplicates")
  @Override
  public DeviceResponse executeCommand(DeviceCommand deviceCommand) throws UiAutomatorDaemonException
  {
    Log.v(uiaDaemon_logcatTag, "Executing device command: " + deviceCommand.command);

    if (deviceCommand.command.equals(DEVICE_COMMAND_STOP_UIADAEMON))
    {
      // The server will be closed after this response is sent, because the given deviceCommand.command will be interpreted
      // in the caller, i.e. UiautomatorDaemonTcpServerBase.
      return new DeviceResponse();
    }

    if (deviceCommand.command.equals(DEVICE_COMMAND_GET_UIAUTOMATOR_WINDOW_HIERARCHY_DUMP))
      return getWindowHierarchyDump();

    if (deviceCommand.command.equals(DEVICE_COMMAND_GET_IS_ORIENTATION_LANDSCAPE))
      return getIsNaturalOrientation();


    if (deviceCommand.command.equals(DEVICE_COMMAND_PERFORM_ACTION))
      return performAction(deviceCommand);

    if (deviceCommand.command.equals(DEVICE_COMMAND_GET_DEVICE_MODEL))
    {
      DeviceResponse deviceResponse = new DeviceResponse();
      deviceResponse.model = getDeviceModel();
      return deviceResponse;
    }

    throw new UiAutomatorDaemonException(String.format("The command %s is not implemented yet!", deviceCommand.command));
  }

  private String _deviceModel = null; 
  private String getDeviceModel()
  {
    if (_deviceModel == null)
    {
      String model = Build.MODEL;
      String manufacturer = Build.MANUFACTURER;
      _deviceModel = manufacturer + "-" + model;
      Log.d(uiaDaemon_logcatTag, "Device model: " + _deviceModel);
    }
    return _deviceModel;
  }
  
  private boolean deviceIsEmulator()
  {
    return getDeviceModel().contains("unknown");
  }

  private String getsWifiSwitchWidgetName()
  {
    String switchWidgetName;
    if (getDeviceModel().equals(DEVICE_SAMSUNG_GALAXY_S3_GT_I9300))
      switchWidgetName = "android:id/switchWidget";
    else
      switchWidgetName = "com.android.settings:id/switchWidget";

    return switchWidgetName;
  }

  private DeviceResponse getIsNaturalOrientation()
  {
    Log.d(uiaDaemon_logcatTag, "Getting 'isNaturalOrientation'");
    ui.getUiDevice().waitForIdle();
    DeviceResponse deviceResponse = new DeviceResponse();
    deviceResponse.isNaturalOrientation = ui.getUiDevice().isNaturalOrientation();
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
        ui.getUiDevice().pressBack();
        waitForGuiToStabilize();
      } else if (action.guiActionCommand.equals(guiActionCommand_pressHome))
      {
        Log.d(uiaDaemon_logcatTag, "Pressing 'home' button.");
        ui.getUiDevice().pressHome();
        waitForGuiToStabilize();
      } else if (action.guiActionCommand.equals(guiActionCommand_turnWifiOn))
      {
        turnWifiOnAndGoHome();

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
        boolean enterResult = new UiObject(
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

      if (clickXCoor > ui.getUiDevice().getDisplayWidth())
        throw new AssertionError("assert clickXCoor <= ui.getUiDevice().getDisplayWidth()");
      if (clickYCoor > ui.getUiDevice().getDisplayHeight())
        throw new AssertionError("assert clickXCoor <= ui.getUiDevice().getDisplayHeight()");

      // WISH return clickResult in deviceResponse, so we can try to click again on 'app has stopped' and other dialog boxes. Right now there is just last chance attempt in org.droidmate.exploration.VerifiableDeviceActionsExecutor.executeAndVerify()
      boolean clickResult;
      clickResult = click(deviceCommand, clickXCoor, clickYCoor);
      if (!clickResult)
      {
        Log.d(uiaDaemon_logcatTag, (String.format("The operation ui.getUiDevice().click(%d, %d) failed (the 'click' method returned 'false'). Retrying after 2 seconds.", clickXCoor, clickYCoor)));

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
    deviceResponse.isNaturalOrientation = ui.getUiDevice().isNaturalOrientation();

    return deviceResponse;
  }

  private void turnWifiOnAndGoHome()
  {
    if (deviceIsEmulator())
    {
      Log.d(uiaDaemon_logcatTag, "Checking wifi state: skipped, because running on emulator.");
      return;
    }
    
    Log.d(uiaDaemon_logcatTag, "Checking wifi state.");
    try
    {
      new UiObject(new UiSelector().textContains("Settings")).click();
      waitForGuiToStabilize();

    } catch (UiObjectNotFoundException e)
    {
      Log.w(uiaDaemon_logcatTag, "No 'settings' to click found, while in the process of ensuring that wifi is on! " +
        "Please ensure the 'settings' app icon is visible (drag-n-drop it to desktop from the list of apps).");
    }

    try
    {
      String switchWidgetName = this.getsWifiSwitchWidgetName();
      UiObject wifiSwitch = new UiObject(new UiSelector().resourceId(switchWidgetName));
      if (wifiSwitch.getText().equals("OFF"))
      {
        Log.i(uiaDaemon_logcatTag, "Turning wifi on.");
        wifiSwitch.click();
        waitForGuiToStabilize();

        // WISH toremove if it ultimately proves to be unnecessary.
//            try
//            {
//              Thread.sleep(0);
//            } catch (InterruptedException e)
//            {
//              Log.wtf(uiaDaemon_logcatTag, "Thread interrupted while sleeping after turning wifi on!");
//            }

        if (wifiSwitch.getText().equals("ON"))
          Log.i(uiaDaemon_logcatTag, "Wifi turned on successfully.");
        else
          Log.w(uiaDaemon_logcatTag, "Clicked to make wifi on, but it is not ON!");
      }

    } catch (UiObjectNotFoundException e)
    {
      // WISH this might happen if some app requested settings -> factory reset. Then on clicking "settings" another subscreen of it is displayed. Proposed solution: swipe down the upper right drop-down menu instead.
      // WISH look like this can also happen on Nexus 7 2012 android v4.4.2 emulator. Only Bluetooth available.
      Log.w(uiaDaemon_logcatTag, "No wifi switch found while in the process of ensuring that wifi is on!");
    }

    ui.getUiDevice().pressHome();
    waitForGuiToStabilize();
  }

  private boolean click(DeviceCommand deviceCommand, int clickXCoor, int clickYCoor)
  {
    boolean clickResult;
    if (deviceCommand.guiAction.longClick)
      clickResult = ui.getUiDevice().swipe(clickXCoor, clickYCoor, clickXCoor, clickYCoor, 100); // 100 ~ 2s. Empirical evaluation.
    else
      clickResult = ui.getUiDevice().click(clickXCoor, clickYCoor);

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
   * [wfi]: {@link com.android.uiautomator.core.UiDevice#waitForIdle()}<br/>
   * [wfi2]: {@code com.android.uiautomator.core.UiAutomatorBridge#waitForIdle(long)}<br/>
   * [wfi3]: {@link android.app.UiAutomation#waitForIdle(long, long)}<br/>
   * [qti]: {@code com.android.uiautomator.core.UiAutomatorBridge#QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE}<br/>
   * [wfwu]: {@link com.android.uiautomator.core.UiDevice#waitForWindowUpdate(String, long)}<br/>
   * [wue]: {@link android.view.accessibility.AccessibilityEvent#TYPE_WINDOW_CONTENT_CHANGED}<br/>
   * [clck]: {@link com.android.uiautomator.core.UiDevice#click(int, int)}<br/>
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
      ui.getUiDevice().waitForIdle();
      long initialWaitForIdleWaitTime = System.currentTimeMillis() - initialWaitForIdleStartTime;
      Log.v(uiaDaemon_logcatTag, "waitForGuiToStabilize: initial waitForIdle took " + initialWaitForIdleWaitTime + "ms");

      boolean wfwuReachedTimeout;
      boolean wfiReturnedImmediately;
      int iteration = 0;
      int maxIterations = 5;
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
    ui.getUiDevice().waitForWindowUpdate(null, waitForWindowUpdateTimeout);
    long waitForWindowUpdateWaitTime = System.currentTimeMillis() - waitForWindowUpdateStartTime;
    Log.v(uiaDaemon_logcatTag, "waitForGuiToStabilize: iteration " + i + " waitForWindowUpdate took " + waitForWindowUpdateWaitTime + "ms");

    waitForWindowUpdateReachedTimeout = waitForWindowUpdateWaitTime >= waitForWindowUpdateTimeout;
    return waitForWindowUpdateReachedTimeout;
  }

  private boolean waitForIdle(int i)
  {
    boolean waitForIdleReturnedImmediately;
    long waitForIdleStartTime = System.currentTimeMillis();
    ui.getUiDevice().waitForIdle();
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

    int width = ui.getUiDevice().getDisplayWidth();
    int height = ui.getUiDevice().getDisplayHeight();
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
   * {@link org.droidmate.uiautomator_daemon.UiAutomatorDaemonException}.
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
   * at org.droidmate.uiautomator_daemon.UiAutomatorDaemonDriver.tryDumpWindowHierarchy(UiAutomatorDaemonDriver.java:420) ~[na:na]<br/>
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
          ui.getUiDevice().pressHome();
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
  private boolean tryDumpWindowHierarchy(File windowDumpFile)
  {
    try
    {
      ui.getUiDevice().dumpWindowHierarchy(windowDumpFile.getName());

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
    }
  }

  private File prepareWindowDumpFile(String fileName) throws UiAutomatorDaemonException
  {
    // Copied from com.android.uiautomator.core.UiDevice.dumpWindowHierarchy()
    final File dir = new File(Environment.getDataDirectory(), "local/tmp");
    File file = new File(dir, fileName);

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
      ui.getUiDevice().pressHome();
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
    ui.getUiDevice().pressHome();

    // We’re now in the home screen. Next, we want to simulate
    // a user bringing up the All Apps screen.
    // If you use the uiautomatorviewer tool to capture a snapshot
    // of the Home screen, notice that the All Apps button’s
    // content-description property has the value "Apps".  We can
    // use this property to create a UiSelector to find the button.
    UiObject allAppsButton = new UiObject(new UiSelector().description("Apps"));

    // Simulate a click to bring up the All Apps screen.
    allAppsButton.clickAndWaitForNewWindow();


    // In the All Apps screen, the app launch icon is located in
    // the Apps tab. To simulate the user bringing up the Apps tab,
    // we create a UiSelector to find a tab with the text
    // label "Apps".
    UiObject appsTab = new UiObject(new UiSelector().text("Apps"));

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
