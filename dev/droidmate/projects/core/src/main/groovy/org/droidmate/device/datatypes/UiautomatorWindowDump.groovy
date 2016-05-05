// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import groovy.transform.EqualsAndHashCode
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import org.droidmate.common.exceptions.InvalidWidgetBoundsException
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.device.model.IDeviceModel
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError

import java.awt.*
import java.util.List

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.common.logging.Markers.exceptions

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
  private final IDeviceModel     deviceModel

  /** Id is used only for tests, for:
   * - easy determination by human which widget is which when looking at widget string representation
   * - For asserting actual widgets match expected.
   * */
  String id = null


  UiautomatorWindowDump(String windowHierarchyDump, Dimension displayDimensions, IDeviceModel deviceModel, String id = null)
  {
    this.id = id
    this.windowHierarchyDump = windowHierarchyDump
    this.deviceDisplayBounds = new Rectangle(displayDimensions)

    this.wellFormedness = this.checkWellFormedness()

    this.deviceModel = deviceModel

    if (this.wellFormedness == WellFormedness.OK)
      this.guiState = computeGuiState()
    else
      this.guiState = null

    this.validationResult = this.validate()
  }

  @Override
  String getWindowHierarchyDump()
  {
    return windowHierarchyDump
  }

  @Override
  String getPackageName()
  {
    if (this.checkWellFormedness() != WellFormedness.OK)
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
  private GuiState computeGuiState()
  {
    assert wellFormedness == WellFormedness.OK

    GPathResult hierarchy = new XmlSlurper().parseText(windowHierarchyDump)
    assert hierarchy.name() == "hierarchy"

    String topNodePackage = hierarchy.node[0]?.@package?.text()
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

    def gs = new GuiState(topNodePackage, id, widgets, this.deviceModel)
    if (gs.isAppHasStoppedDialogBox())
      return new AppHasStoppedDialogBoxGuiState(topNodePackage, widgets, this.deviceModel)
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

      } else
      {
        return ValidationResult.OK
      }
    } else
      return wellFormedness.toValidationResult()
  }


  WellFormedness checkWellFormedness()
  {
    if (windowHierarchyDump == null)
      return WellFormedness.is_null
    else if (windowHierarchyDump.length() == 0)
      return WellFormedness.is_empty
    else if (!windowHierarchyDump.contains(rootXmlNodePrefix))
      return WellFormedness.missing_root_xml_node_prefix
    else
      return WellFormedness.OK
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
    if (this.checkWellFormedness() != WellFormedness.OK)
      return "$cls{!not well-formed!: $windowHierarchyDump}"

    if (this.guiState.isHomeScreen())
      return "$cls{home screen}"

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
}

