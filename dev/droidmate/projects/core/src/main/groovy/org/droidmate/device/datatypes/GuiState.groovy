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

import groovy.transform.Canonical
import org.droidmate.misc.TextUtilsCategory

@Canonical(excludes = "id")
class GuiState implements Serializable, IGuiState
{
  private static final long serialVersionUID = 1

  final static String androidPackageName            = "android"
  final static String resId_runtimePermissionDialog = "com.android.packageinstaller:id/dialog_container"

  final String       topNodePackageName

  /** Id is used only for tests, for easy determination by human which instance is which when looking at widget string
   * representation. */
  final List<Widget> widgets

  final String id

  final String androidLauncherPackageName

  GuiState(String topNodePackageName, String id, List<Widget> widgets, String androidLauncherPackageName)
  {
    this.topNodePackageName = topNodePackageName
    this.widgets = widgets
    this.id = id
    this.androidLauncherPackageName = androidLauncherPackageName

    assert !this.topNodePackageName?.empty
    assert !this.androidLauncherPackageName?.empty
    assert widgets != null
  }

  @Override
   List<Widget> getActionableWidgets()
  {
    widgets.findAll {it.canBeActedUpon()}
  }

  @Override
   String toString()
  {
    use(TextUtilsCategory) {
      if (this.isHomeScreen())
        return "GUI state: home screen".wrapWith("<>")

      if (this instanceof AppHasStoppedDialogBoxGuiState)
        return "GUI state of \"App has stopped\" dialog box. OK widget enabled: ${(this as AppHasStoppedDialogBoxGuiState).OKWidget.enabled}".wrapWith("<>")

      if (this instanceof RuntimePermissionDialogBoxGuiState)
        return "GUI state of \"Runtime permission\" dialog box. Allow widget enabled: ${(this as RuntimePermissionDialogBoxGuiState).allowWidget.enabled}".wrapWith("<>")

      return "GuiState " + (id != null ? "id=$id " : "") + "pkg=$topNodePackageName Widgets count = ${widgets.size()}".wrapWith("<>")
    }
  }

  @Override
  boolean isHomeScreen()
  {
    return topNodePackageName == androidLauncherPackageName && !widgets.any {it.text == "Widgets"}
  }

  @Override
  boolean isAppHasStoppedDialogBox()
  {
    return topNodePackageName == androidPackageName &&
      widgets.any {it.text == "OK"} &&
      !widgets.any {it.text == "Just once"}
  }

  @Override
  boolean isCompleteActionUsingDialogBox()
  {
    return !isSelectAHomeAppDialogBox() && !isUseLauncherAsHomeDialogBox() &&
      topNodePackageName == androidPackageName &&
      widgets.any {it.text == "Just once"}
  }

  @Override
  boolean isSelectAHomeAppDialogBox()
  {
    return topNodePackageName == androidPackageName &&
      widgets.any {it.text == "Just once"} &&
      widgets.any {it.text == "Select a Home app"}
  }

  @Override
  boolean isUseLauncherAsHomeDialogBox()
  {
    return topNodePackageName == androidPackageName &&
      widgets.any {it.text == "Use Launcher as Home" } &&
      widgets.any {it.text == "Just once"} &&
      widgets.any {it.text == "Always"}
  }


  @Override
  boolean isRequestRuntimePermissionDialogBox()
  {
    boolean isRuntimeDialog = widgets.any {it.resourceId == resId_runtimePermissionDialog}

    return isRuntimeDialog
  }

  @Override
  boolean belongsToApp(String appPackageName)
  {
    return this.topNodePackageName == appPackageName
  }

  @Override
  String debugWidgets()
  {
    StringWriter sw = new StringWriter()
    sw.withWriter {wr ->
      
      wr.println("widgets (${widgets.size()}):")
      widgets.each {wr.println(it.toString())}

      wr.println("actionable widgets (${actionableWidgets.size()}):")
      actionableWidgets.each {wr.println(it.toString())}
    }
    return sw.toString()
  }
}
