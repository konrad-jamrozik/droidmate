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

package org.droidmate.device.datatypes

import groovy.transform.EqualsAndHashCode
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.logging.LogbackConstants

import java.awt.*
import java.util.List

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.logging.Markers.exceptions

/**
 * <p>
 * <i> --- This doc was last reviewed on 04 Jan 2014.</i>
 * </p><p>
 *
 * Represents the GUI snapshot returned by uiautomator.
 *
 * </p><p>
 *
 * About uiautomator dump:<br/>
 * http://developer.android.com/tools/help/uiautomator/index.html#table1
 *
 * </p><p>
 *
 * Example bash scripts on how to get the dump on Windows:<br/>
 *
 * <pre>
 * function dump_gui {*   adb shell uiautomator dump # should dump to: /storage/emulated/legacy/window_dump.xml
 *   adb pull storage/emulated/legacy/window_dump.xml ./window_dump.xml
 *}*
 * # pre-condition: a device is running and connected through adb.
 * function vis_dump_gui {*   echo "After the GUI starts, please select the device to the left and click on the appropriate
 * button above it"
 *   echo "to dump the GUI."
 *   $COMSPEC /c monitor.bat
 *}</pre>
 * The monitor.bat from examples above should live in path like:<br/>
 * c:\Program Files (x86)\Android\android-sdk\tools\monitor.bat
 */
@TypeChecked
@Slf4j
@EqualsAndHashCode
class UiautomatorWindowDump implements IDeviceGuiSnapshot, Serializable
{

  private static final long serialVersionUID = 1

  public static final String rootXmlNodePrefix = "<hierarchy"

  final         String         windowHierarchyDump
  final         Rectangle      deviceDisplayBounds
  private final WellFormedness wellFormedness

  private final IGuiState        guiState
  final         ValidationResult validationResult
  private final String           androidLauncherPackageName

  /** Id is used only for tests, for:
   * - easy determination by human which widget is which when looking at widget string representation
   * - For asserting actual widgets match expected.
   * */
  String id = null


  UiautomatorWindowDump(String windowHierarchyDump, Dimension displayDimensions, String androidLauncherPackageName, String id = null)
  {
    this.id = id
    this.deviceDisplayBounds = new Rectangle(displayDimensions)
    this.androidLauncherPackageName = androidLauncherPackageName

    def wellFormedness = checkWellFormedness(windowHierarchyDump)
    if (wellFormedness == WellFormedness.OK)
    {
      this.windowHierarchyDump = removeSystemuiNodes(windowHierarchyDump)
      this.wellFormedness = checkWellFormedness(this.windowHierarchyDump)
    }
    else
    {
      this.wellFormedness = wellFormedness
      this.windowHierarchyDump = windowHierarchyDump
    }

    if (this.wellFormedness == WellFormedness.OK)
      this.guiState = computeGuiState(this.windowHierarchyDump)
    else
      this.guiState = null

    this.validationResult = validate()
  }

  @Override
  String getWindowHierarchyDump()
  {
    return windowHierarchyDump
  }

  @Override
  String getPackageName()
  {
    if (this.wellFormedness != WellFormedness.OK)
      return "Package unknown: the snapshot is not well-formed"

    int startIndex = windowHierarchyDump.indexOf("package=\"");
    int endIndex = windowHierarchyDump.indexOf('"', startIndex + "package=\"".length());
    return windowHierarchyDump.substring(startIndex + "package=\"".length(), endIndex);
  }

  //region Getting GUI state

  /**
   * see {@link #computeGuiState()}
   */
  @Override
  IGuiState getGuiState()
  {
    assert guiState != null
    return guiState
  }

  @TypeChecked(SKIP)
  private GuiState computeGuiState(String windowHierarchyDump)
  {
    assert wellFormedness == WellFormedness.OK
    
    GPathResult hierarchy = new XmlSlurper().parseText(windowHierarchyDump)
    assert hierarchy.name() == "hierarchy"

    String topNodePackage = hierarchy.node[0]?.@package?.text()
    // KJA bug assert fail after on fixture droidmate clicked "Crash activity"
    // KJA DEBUG
    if (topNodePackage.empty)
    {
      log.warn("window hierarchy dump: \n"+windowHierarchyDump)
    }
    assert !topNodePackage.empty


    List<Widget> widgets = hierarchy.depthFirst().findAll {it.name() == "node"}.collect {

      try
      {
        /*
          Example "it": <node index="0" text="LOG IN" resource-id="com.snapchat.android:id/landing_page_login_button" class="android.widget.Button" package="com.snapchat.android" content-desc="" checkable="false" checked="false" clickable="true" enabled="true" focusable="true" focused="false" scrollable="false" long-clickable="false" password="false" selected="false" bounds="[0,949][800,1077]"/>
         */
        Widget w = new Widget(

          // @formatter:off
          id                  : it.@id.text() != "" ? it.@id.text() : null, // Appears only in test code simulating the device, never on actual devices or their emulators.

          index               : it.@index             .text() as int,
          text                : it.@text              .text(),
          resourceId          : it.@'resource-id'     .text(),
          className           : it.@class             .text(),
          packageName         : it.@package           .text(),
          contentDesc         : it.@'content-desc'    .text(),
          checkable           : it.@checkable         .text() == "true",
          checked             : it.@checked           .text() == "true",
          clickable           : it.@clickable         .text() == "true",
          enabled             : it.@enabled           .text() == "true",
          focusable           : it.@focusable         .text() == "true",
          focused             : it.@focused           .text() == "true",
          scrollable          : it.@scrollable        .text() == "true",
          longClickable       : it.@'long-clickable'  .text() == "true",
          password            : it.@password          .text() == "true",
          selected            : it.@selected          .text() == "true",

          bounds              : Widget.parseBounds(it.@bounds.text()),
          deviceDisplayBounds : deviceDisplayBounds,
          // @formatter:on
        )
        return w;
      }
      catch (InvalidWidgetBoundsException e)
      {
        log.error("Catching exception: parsing widget bounds failed. $LogbackConstants.err_log_msg\n" +
          "Continuing execution, skipping the widget with invalid bounds.")
        log.error(exceptions, "parsing widget bounds failed with exception:\n", e);
        return null
      }
    }.findAll {it != null}

    def gs = new GuiState(topNodePackage, id, widgets, this.androidLauncherPackageName)
    if (gs.isRequestRuntimePermissionDialogBox())
      return new RuntimePermissionDialogBoxGuiState(topNodePackage, widgets, this.androidLauncherPackageName)
    else if (gs.isAppHasStoppedDialogBox())
      return new AppHasStoppedDialogBoxGuiState(topNodePackage, widgets, this.androidLauncherPackageName)
    else
      return gs
  }

  //endregion Getting GUI state

  //region Validation

  private ValidationResult validate()
  {
    if (wellFormedness == WellFormedness.OK)
    {
      def gs = this.guiState
      assert gs != null

      if (gs instanceof AppHasStoppedDialogBoxGuiState)
      {
        if (gs.OKWidget.enabled)
          return ValidationResult.app_has_stopped_dialog_box_with_OK_button_enabled
        else
          return ValidationResult.app_has_stopped_dialog_box_with_OK_button_disabled

      } else if (gs instanceof RuntimePermissionDialogBoxGuiState)
      {
        if (gs.allowWidget.enabled)
          return ValidationResult.request_runtime_permission_dialog_box_with_Allow_button_enabled
        else
          return ValidationResult.request_runtime_permission_dialog_box_with_Allow_button_disabled

      } else
      {
        return ValidationResult.OK
      }
    } else
      return wellFormedness.toValidationResult()
  }


  private WellFormedness checkWellFormedness(String windowHierarchyDump)
  {
    if (windowHierarchyDump == null)
      return WellFormedness.is_null
    else if (windowHierarchyDump.length() == 0 || isEmptyStub(windowHierarchyDump))
      return WellFormedness.is_empty
    else if (!windowHierarchyDump.contains(rootXmlNodePrefix))
      return WellFormedness.missing_root_xml_node_prefix
    else
      return WellFormedness.OK
  }

  /**
   * This covers a case when the dump looks as follows:
   * 
   * <?xml version="1.0" encoding="UTF-8"?><hierarchy rotation="0">
   *
   *
   * </hierarchy>
   */
  private boolean isEmptyStub(String windowHierarchyDump)
  {
    return windowHierarchyDump.count("<") <= 3 && windowHierarchyDump.count("\n") <= 5
  }

  private enum WellFormedness {

    OK,
    is_null,
    is_empty,
    missing_root_xml_node_prefix

    ValidationResult toValidationResult()
    {
      switch (this)
      {
        case OK:
          assert false: "Called .toValidatonResult() on 'OK' well-formedness status. This is forbidden, as 'OK' well-formedness is not enough by itself to determine validation result."
          break
        case is_null:
          return ValidationResult.is_null
          break
        case is_empty:
          return ValidationResult.is_empty
          break
        case missing_root_xml_node_prefix:
          return ValidationResult.missing_root_xml_node_prefix
          break
        default:
          throw new UnexpectedIfElseFallthroughError("Unhandled $this")
      }

      assert false: "This statement should be unreachable code!"
      return null // To make compiler happy
    }
  }
  //endregion


  @Override
  public String toString()
  {
    String cls = UiautomatorWindowDump.simpleName
    if (this.wellFormedness != WellFormedness.OK)
      return "$cls{!not well-formed!: $windowHierarchyDump}"

    if (this.guiState.isHomeScreen())
      return "$cls{home screen}"

    if (this.guiState.isRequestRuntimePermissionDialogBox())
      return "$cls{\"Runtime permission\" dialog box. Allow widget enabled: ${(this.guiState as RuntimePermissionDialogBoxGuiState).allowWidget.enabled}}"

    if (this.guiState.isAppHasStoppedDialogBox())
      return "$cls{\"App has stopped\" dialog box. OK widget enabled: ${(this.guiState as AppHasStoppedDialogBoxGuiState).OKWidget.enabled}}"

    if (this.guiState.isCompleteActionUsingDialogBox())
      return "$cls{\"Complete action using\" dialog box.}"

    if (this.guiState.isSelectAHomeAppDialogBox())
      return "$cls{\"Select a home app\" dialog box.}"

    String returnString = "$cls{${packageName}. Widgets# ${this.guiState.widgets.size()}}"
    
    // Uncomment when necessary for debugging.
//    List<Widget> widgets = this.guiState.widgets
//    final int displayedWidgetsLimit = 50
//    returnString += widgets.take(displayedWidgetsLimit).collect {it.toShortString() }.join("\n") + "\n"
//    if (widgets.size() > displayedWidgetsLimit)
//      returnString += "...\n...skipped displaying remaining ${widgets.size()-displayedWidgetsLimit} widgets...\n"
//    returnString += "----- end of widgets ----\n"

    return returnString
  }

  private String removeSystemuiNodes(String windowHierarchyDump)
  {
    return UiautomatorWindowDump_functionsKt.removeSystemuiNodes(windowHierarchyDump)
}
}

