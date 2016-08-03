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

import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.device.model.DeviceModel

import java.awt.*
import java.util.List

class WidgetTestHelper
{

  public static Widget newGenWidget(Map args, int widgetGenIndex)
  {
    assert widgetGenIndex >= 1
    Map genArgs = args

    // @formatter:off
    genArgs.id        = args.id        ?: getIdsList(widgetGenIndex).last()
    genArgs.text      = args.text      ?: getTextsList(widgetGenIndex, ([genArgs.id] * widgetGenIndex) as List<String>).last()
    genArgs.bounds    = args.bounds    ?: getBoundsList(widgetGenIndex).last()
    genArgs.className = args.className ?: getClassesList(widgetGenIndex).last()
    genArgs.enabled   = args.enabled   ?: true
    // @formatter:on

    return newWidget(genArgs)
  }


  public static List<Widget> newWidgets(int widgetCount, String packageName, Map props, String widgetIdPrefix = null)
  {
    assert widgetCount >= 1

    // @formatter:off
    List<String>        idsList     = props.idsList     ?: getIdsList(widgetCount, widgetIdPrefix)
    List<String>        textsList   = props.textsList   ?: getTextsList(widgetCount, idsList)
    List<List<Integer>> boundsList  = props.boundsList  ?: getBoundsList(widgetCount)
    List<String>        classesList = props.classList   ?: getClassesList(widgetCount)
    List<Boolean>       enabledList = props.enabledList ?: [true] * widgetCount
    // @formatter:on

    assert [idsList, textsList, boundsList, classesList, enabledList].every {it.size() == widgetCount}

    List<Widget> widgets = []
    widgetCount.times {int i ->
      widgets << newWidget(
        // @formatter:off
        id          : idsList[i],
        text        : textsList[i],
        bounds      : boundsList[i],
        className   : classesList[i],
        packageName : packageName,
        clickable   : true,
        checkable   : false,
        enabled     : enabledList[i]
        // @formatter:on
      )
    }
    assert widgets?.size() == widgetCount
    return widgets
  }

  private static List<String> getIdsList(int idsCount, String widgetIdPrefix = null)
  {
    List<String> idsList = []
    idsCount.times {int i -> idsList << getNextWidgetId(i, widgetIdPrefix)}
    return idsList
  }

  private static List<String> getTextsList(int textsCount, List<String> widgetIds)
  {
    assert widgetIds.size() == textsCount
    List<String> textsList = []
    textsCount.times {int i -> textsList << "txt:id/" + widgetIds[i]}
    return textsList
  }

  private static List<String> getClassesList(int classesCount)
  {
    List<String> classesList = androidWidgetClassesForTesting as List
    List<String> classes = []
    classesCount.times {int index ->
      int classNameIndex = index % classesList.size()
      classes << classesList[classNameIndex]
    }

    return classes
  }

  private static List<List<Integer>> getBoundsList(int boundsCount)
  {
    int lowX = 5 + getBoundsListCallGen
    int lowY = 6 + getBoundsListCallGen
    getBoundsListCallGen++
    int highX = lowX + 20
    int highY = lowY + 30

    List<List<Integer>> bounds = []
    boundsCount.times {
      bounds << [lowX, lowY, highX, highY]
      lowX += 25
      lowY += 35
      highX = lowX + 20
      highY = lowY + 30
    }
    return bounds
  }

  static long dummyNameGen         = 0
  static long getBoundsListCallGen = 0

  public static GString getNextWidgetId(int index, String widgetIdPrefix = null)
  {
    if (widgetIdPrefix == null)
      return "${index}_uniq${dummyNameGen++}"
    else
      return "${widgetIdPrefix}_W${index}"
  }

  public static final List<String> androidWidgetClassesForTesting = [
    "android.view.View",
    "android.widget.Button",
    "android.widget.CheckBox",
    "android.widget.CheckedTextView",
    "android.widget.CompoundButton",
    "android.widget.EditText",
    "android.widget.GridView",
    "android.widget.ImageButton",
    "android.widget.ImageView",
    "android.widget.LinearLayout",
    "android.widget.ListView",
    "android.widget.RadioButton",
    "android.widget.RadioGroup",
    "android.widget.Spinner",
    "android.widget.Switch",
    "android.widget.TableLayout",
    "android.widget.TextView",
    "android.widget.ToggleButton"
  ]


  public static Widget newClickableButton(Map args = [:])
  {
    newButton(args + [clickable: true, checkable: false, enabled: true])
  }

  public static Widget newButton(Map args)
  {
    return newWidget(args + [className: "android.widget.Button"])
  }


  public static newTopLevelWidget(String packageName)
  {
    newWidget([id: "topLevelFrameLayout", packageName: packageName, class: "android.widget.FrameLayout", bounds: [0, 0, 800, 1205]])
  }

  public static Widget newClickableWidget(Map args = [:], Integer widgetGenIndex = null)
  {
    if (widgetGenIndex == null)
      newWidget(args + [clickable: true, enabled: true])
    else
    {
      assert widgetGenIndex >= 0
      newGenWidget(args + [clickable: true, enabled: true], widgetGenIndex + 1)
    }

  }

  public static Widget newWidget(Map args)
  {
    List<Integer> bounds = (args.bounds ?: [10, 20, 101, 202]) as List<Integer>

    assert bounds?.size() == 4
    assert bounds[0] < bounds[2]
    assert bounds[1] < bounds[3]

    int lowX = bounds[0]
    int lowY = bounds[1]
    int highX = bounds[2]
    int highY = bounds[3]


    return new Widget(
      // @formatter:off
      id            : args.id            ?: null,
      index         : args.index         ?: 0,
      text          : args.text          ?: "fix_text",
      resourceId    : args.resourceId    ?: "fix_resId", // other example value: dummy.package.ExampleApp:id/button_someName
      className     : args.className     ?: "fix_cls",
      packageName   : args.packageName   ?: "fix_pkg", // note that: apkFixture_simple_packageName == "org.droidmate.fixtures.apks.simple"
      contentDesc   : args.contentDesc   ?: "fix_contDesc",
      checkable     : args.checkable     ?: false,
      checked       : args.checked       ?: false,
      clickable     : args.clickable     ?: false,
      enabled       : args.enabled       ?: false,
      focusable     : args.focusable     ?: false,
      focused       : args.focused       ?: false,
      scrollable    : args.scrollable    ?: false,
      longClickable : args.longClickable ?: false,
      password      : args.password      ?: false,
      selected      : args.selected      ?: false,
      bounds        : new Rectangle(lowX, lowY, highX - lowX, highY - lowY),
      deviceDisplayBounds: new Rectangle(DeviceModel.buildDefault().getDeviceDisplayDimensionsForTesting())
      // @formatter:on
    )
  }


}
