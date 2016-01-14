// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import com.google.common.base.MoreObjects
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common.logcat.TimeFormattedLogcatMessage
import org.droidmate.common_android.Constants
import org.droidmate.common_android.guimodel.GuiAction
import org.droidmate.device.datatypes.*
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exceptions.UnsupportedMultimethodDispatch
import org.droidmate.lib_android.MonitorJavaTemplate
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.droidmate.misc.ITimeGenerator

class GuiScreen implements IGuiScreen
{

  public static final String              idHome                  = "home"
  public static final String              idChrome                = "chrome"
  public static final Set<String>         reservedIds             = [idHome, idChrome]
  public static final Map<String, String> reservedIdsPackageNames = [
    (idHome)  : GuiState.package_android_launcher,
    (idChrome): "com.android.chrome"
  ]

  IDeviceGuiSnapshot guiSnapshot = null

  final         String id
  private final String packageName

  private IGuiScreen home = null
  private IGuiScreen main = null

  /**
   * <p>
   * The time generator provides successive timestamps to the logs returned by the simulated device from a call to
   * {@link #perform(org.droidmate.device.datatypes.IAndroidDeviceAction)}.
   *
   * </p><p>
   * If this object s a part of simulation obtained from exploration output the time generator is null, as no time needs to be
   * generated. Instead, all time is obtained from the exploration output timestamps.
   *
   * </p>
   */
  private final ITimeGenerator timeGenerator

  private Map<Widget, IGuiScreen> widgetTransitions = new LinkedHashMap<>()
  private boolean                 finishedBuilding  = false

  GuiScreen(IDeviceGuiSnapshot snapshot)
  {
    this(snapshot.id, snapshot.packageName)
    this.guiSnapshot = snapshot
  }

  GuiScreen(String id, String packageName = null, ITimeGenerator timeGenerator = null)
  {
    this.timeGenerator = timeGenerator

    this.id = id
    this.packageName = packageName ?: reservedIdsPackageNames[id]

    assert this.id != null
    assert this.packageName != null
    assert (this.id in reservedIds).implies(this.packageName == reservedIdsPackageNames[id])
    assert (!(this.id in reservedIds)).implies(!(this.packageName in reservedIdsPackageNames.values()))
  }

  @Override
  IScreenTransitionResult perform(IAndroidDeviceAction action)
  {
    assert finishedBuilding
    return internalPerform(action)
  }

  //region internalPerform multimethod

  private IScreenTransitionResult internalPerform(action)
  {
    throw new UnsupportedMultimethodDispatch(action)
  }

  // This method is used: it is a multimethod.
  @SuppressWarnings("GroovyUnusedDeclaration")
  private IScreenTransitionResult internalPerform(AdbClearPackageAction clearPackage)
  {
    if (this.guiSnapshot.packageName == clearPackage.packageName)
      return new ScreenTransitionResult(home, [])
    else
      return new ScreenTransitionResult(this, [])
  }

  // This method is used: it is a multimethod.
  @SuppressWarnings("GroovyUnusedDeclaration")
  private IScreenTransitionResult internalPerform(LaunchMainActivityDeviceAction launch)
  {
    return new ScreenTransitionResult(main, this.buildMonitorMessages())
  }

  // This method is used: it is a multimethod.
  @SuppressWarnings("GroovyUnusedDeclaration")
  private IScreenTransitionResult internalPerform(ClickGuiAction click)
  {
    GuiAction guiAction = click.guiAction

    IScreenTransitionResult out = null
    if (guiAction.guiActionCommand != null)
    {
      switch (guiAction.guiActionCommand)
      {
        case Constants.guiActionCommand_pressHome:
          out = new ScreenTransitionResult(home, [])
          break
        case Constants.guiActionCommand_turnWifiOn:
          assert this.is(home)
          out = new ScreenTransitionResult(this, [])
          break
        case Constants.guiActionCommand_pressBack:
          assert false: "Not yet implemented!"
          break
        default:
          throw new UnexpectedIfElseFallthroughError()
      }
    } else
    {

      Widget widget = click.getSingleMatchingWidget(this.widgetTransitions.keySet())
      out = new ScreenTransitionResult(widgetTransitions[widget], [])

    }
    assert out != null
    return out
  }

  //endregion internalPerform multimethod


  @Override
  void addWidgetTransition(String widgetId, IGuiScreen targetScreen, boolean ignoreDuplicates = false)
  {
    assert !finishedBuilding
    assert !(this.id in reservedIds)
    assert (!ignoreDuplicates).implies(!(widgetTransitions.keySet()*.id.contains(widgetId)))

    if (!(ignoreDuplicates && widgetTransitions.keySet()*.id.contains(widgetId)))
    {
      Widget widget
      if (this.guiSnapshot != null)
        widget = this.guiSnapshot.guiState.widgets.findSingle {it.id == widgetId}
      else
        widget = WidgetTestHelper.newClickableWidget([id: widgetId], /* widgetGenIndex */ widgetTransitions.keySet().size())

      widgetTransitions[widget] = targetScreen
    }

    assert widgetTransitions.keySet()*.id.contains(widgetId)
    assert widgetTransitions.values().every {it != null}
  }

  @Override
  void addHomeScreenReference(IGuiScreen home)
  {
    assert !finishedBuilding
    assert home.id == idHome
    this.home = home
  }

  @Override
  void addMainScreenReference(IGuiScreen main)
  {
    assert !finishedBuilding
    assert !(main.id in reservedIds)
    this.main = main
  }

  @Override
  public void buildInternals()
  {
    assert !this.finishedBuilding
    assert this.guiSnapshot == null

    Set<Widget> widgets = widgetTransitions.keySet()
    if (!(id in reservedIds))
    {
      IGuiState guiState
      if (widgets.empty)
      {
        guiState = buildEmptyInternals()
      } else
        guiState = GuiStateTestHelper.newGuiStateWithWidgets(
          widgets.size(), packageName, /* enabled */ true, id, widgets*.id as List<String>)

      this.guiSnapshot = UiautomatorWindowDumpTestHelper.fromGuiState(guiState)

    } else if (id == idHome)
    {
      this.guiSnapshot = UiautomatorWindowDumpTestHelper.newHomeScreenWindowDump(this.id)

    } else if (id == idChrome)
    {
      this.guiSnapshot = UiautomatorWindowDumpTestHelper.newAppOutOfScopeWindowDump(this.id)

    } else
      throw new UnexpectedIfElseFallthroughError("Unsupported reserved id: $id")

    assert this.guiSnapshot.id != null

  }

  public GuiState buildEmptyInternals()
  {
    def guiState = GuiStateTestHelper.newGuiStateWithTopLevelNodeOnly(packageName, id)
    // This one widget is necessary, as it is the only xml element from which packageName can be obtained. Without it, following
    // method would fail: org.droidmate.device.datatypes.UiautomatorWindowDump.getPackageName when called on
    // org.droidmate.exploration.device.simulation.GuiScreen.guiSnapshot.
    assert guiState.widgets.size() == 1
    return guiState
  }

  @Override
  void verify()
  {
    assert !finishedBuilding
    this.finishedBuilding = true

    assert this?.home?.id == idHome
    assert !(this?.main?.id in reservedIds)
    assert this.guiSnapshot.id != null
    assert this.guiSnapshot.guiState.id != null
    assert (!(this.id in reservedIds)).implies(this.widgetTransitions.keySet()*.id.sort() == this.guiSnapshot.guiState.actionableWidgets*.id.sort())
    assert widgetTransitions.values().every {it != null}
    assert this.finishedBuilding
  }

  ArrayList<ITimeFormattedLogcatMessage> buildMonitorMessages()
  {
    return [
      TimeFormattedLogcatMessage.from(
        this.timeGenerator.shiftAndGet(milliseconds: 1500), // Milliseconds amount based on empirical evidence.
        MonitorJavaTemplate.loglevel.toUpperCase(),
        MonitorJavaTemplate.tag_init,
        "4224", // arbitrary process ID
        MonitorJavaTemplate.msg_ctor_success),
      TimeFormattedLogcatMessage.from(
        this.timeGenerator.shiftAndGet(milliseconds: 1810), // Milliseconds amount based on empirical evidence.
        MonitorJavaTemplate.loglevel.toUpperCase(),
        MonitorJavaTemplate.tag_init,
        "4224", // arbitrary process ID
        MonitorJavaTemplate.msgPrefix_init_success + this.packageName)
    ]
  }


  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
      .add("id", id)
      .toString();
  }
}
