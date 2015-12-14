// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

import org.droidmate.common.LabeledEdge
import org.droidmate.misc.ITimeGenerator

import java.util.regex.Matcher

class GuiScreensBuilderFromSpec implements IGuiScreensBuilder
{

  private final ITimeGenerator timeGenerator
  private final String         spec
  private final String         packageName

  GuiScreensBuilderFromSpec(ITimeGenerator timeGenerator, String spec, String packageName)
  {
    this.timeGenerator = timeGenerator
    this.spec = spec
    this.packageName = packageName
  }

  @Override
  public List<IGuiScreen> build()
  {
    return buildGuiScreens(spec, packageName)
  }


  private List<IGuiScreen> buildGuiScreens(String spec, String packageName)
  {
    ArrayList<LabeledEdge<String, String, String>> edges = parseSpecEdges(spec)

    List<IGuiScreen> guiScreens = buildAppGuiScreens(edges, packageName)
    addSpecialGuiScreens(guiScreens)
    buildWidgetTransitions(edges, guiScreens)

    guiScreens.each {
      it.buildInternals()
      it.verify()
    }

    return guiScreens
  }

  private static ArrayList<LabeledEdge<String, String, String>> parseSpecEdges(String spec)
  {
    List<LabeledEdge<String, String, String>> specEdges = []

    Matcher m = spec =~ /(\w+)-(\w+)->(\w+) ?/

    while (m.find())
    {
      def edge = new LabeledEdge<String, String, String>(m.group(1), m.group(2), m.group(3))
      assert (!(edge.source in GuiScreen.reservedIds))
      specEdges << edge
    }

    assert specEdges.size() > 0: "Expected to have at least one spec edge defined."
    return specEdges
  }

  private List<IGuiScreen> buildAppGuiScreens(ArrayList<LabeledEdge<String, String, String>> edges, String pkgName)
  {
    List<IGuiScreen> guiScreens = edges.collect {def edge ->
      [edge.source, edge.target].collect {def id ->
        if (id in GuiScreen.reservedIds)
          return null // Here we return null as Gui Screens having reserved ids do not belong to the app and so will be built by a different method.
        else
          return new GuiScreen(id, pkgName, this.timeGenerator)
      }
    }.flatten().findAll {it != null} as List<IGuiScreen>

    assert guiScreens*.id.every {!(it in GuiScreen.reservedIds)}

    // Remove duplicate representations of the GuiScreens.
    guiScreens.unique(true) {it.id}

    return guiScreens
  }

  private void addSpecialGuiScreens(List<IGuiScreen> guiScreens)
  {
    // The first GuiScreen is denoted as the one representing main activity, to be launched on app start.
    IGuiScreen main = guiScreens[0]
    IGuiScreen home = new GuiScreen(GuiScreen.idHome, /* packageName */ null, this.timeGenerator)
    IGuiScreen chrome = new GuiScreen(GuiScreen.idChrome, /* packageName */ null, this.timeGenerator)

    guiScreens.addAll([home, chrome])

    guiScreens.each {
      it.addHomeScreenReference(home)
      it.addMainScreenReference(main)
    }
  }

  private static List<LabeledEdge<String, String, String>> buildWidgetTransitions(
    ArrayList<LabeledEdge<String, String, String>> edges, guiScreens)
  {
    return edges.each {def edge ->
      IGuiScreen sourceScreen = guiScreens.findSingle {edge.source == it.id}
      IGuiScreen targetScreen = guiScreens.findSingle {edge.target == it.id}
      sourceScreen.addWidgetTransition(edge.label, targetScreen)
    }
  }

}
