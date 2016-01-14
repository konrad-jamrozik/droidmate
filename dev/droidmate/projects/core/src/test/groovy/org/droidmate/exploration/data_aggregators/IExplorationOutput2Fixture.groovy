// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.data_aggregators

interface IExplorationOutput2Fixture
{
  ExplorationOutput2 getFixture()

  List<String> getExpectedChartFileContents()
}