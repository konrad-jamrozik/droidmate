// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

@Deprecated
public interface IExplorationOutputDataExtractor
{

  public void pgfplotsChartInputData(Map cfgMap, ExplorationOutput explorationOutput, Writer writer)

  void stackTraces(ExplorationOutput output, Writer writer)

  void apiManifest(ExplorationOutput output, Writer writer)

  void summary(ExplorationOutput output, Writer writer)

  void actions(ExplorationOutput output, Writer writer)

  void possiblyRedundantApiCalls(ExplorationOutput output, Writer writer)
}
