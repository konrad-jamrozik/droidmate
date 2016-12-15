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
package org.droidmate.test_tools.device_simulation

import com.google.common.base.MoreObjects
import org.droidmate.apis.ITimeFormattedLogcatMessage
import org.droidmate.apis.TimeFormattedLogcatMessage
import org.droidmate.device.datatypes.*
import org.droidmate.device.model.DeviceModel
import org.droidmate.errors.UnexpectedIfElseFallthroughError
import org.droidmate.errors.UnsupportedMultimethodDispatch
import org.droidmate.misc.MonitorConstants
import org.droidmate.test_tools.device.datatypes.GuiStateTestHelper
import org.droidmate.test_tools.device.datatypes.UiautomatorWindowDumpTestHelper
import org.droidmate.test_tools.device.datatypes.WidgetTestHelper
import org.droidmate.uiautomator_daemon.UiautomatorDaemonConstants
import org.droidmate.uiautomator_daemon.guimodel.GuiAction

class GuiScreen implements IGuiScreen
{
  //private static final String packageAndroidLauncher = new DeviceConfigurationFactory(UiautomatorDaemonConstants.DEVICE_DEFAULT).getConfiguration().getPackageAndroidLauncher()

  public static final String              idHome                  = "home"
  public static final String              idChrome                = "chrome"
  public static final Set<String>         reservedIds             = [idHome, idChrome]
  public static final Map<String, String> reservedIdsPackageNames = [
    (idHome)  : DeviceModel.buildDefault().androidLauncherPackageName,
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
    internalPerform(action)
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
        case UiautomatorDaemonConstants.guiActionCommand_pressHome:
          out = new ScreenTransitionResult(home, [])
          break
        case UiautomatorDaemonConstants.guiActionCommand_turnWifiOn:
          assert this.is(home)
          out = new ScreenTransitionResult(this, [])
          break
        case UiautomatorDaemonConstants.guiActionCommand_pressBack:
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
  void buildInternals()
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

  GuiState buildEmptyInternals()
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
        MonitorConstants.loglevel.toUpperCase(),
        MonitorConstants.tag_mjt,
        "4224", // arbitrary process ID
        MonitorConstants.msg_ctor_success),
      TimeFormattedLogcatMessage.from(
        this.timeGenerator.shiftAndGet(milliseconds: 1810), // Milliseconds amount based on empirical evidence.
        MonitorConstants.loglevel.toUpperCase(),
        MonitorConstants.tag_mjt,
        "4224", // arbitrary process ID
        MonitorConstants.msgPrefix_init_success + this.packageName)
    ]
  }


  @Override
   String toString()
  {
    return MoreObjects.toStringHelper(this)
      .add("id", id)
      .toString()
  }
}
