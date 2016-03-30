// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

import java.util.ArrayList;
import java.util.List;

public class RobotPathPlotterStraightLine implements IRobotPathPlotter
{

  @Override
  public List<Pair<Float, Float>> plot(float mappedStartX, float mappedStartY, float mappedEndX, float mappedEndY)
  {
    ArrayList<Pair<Float, Float>> points = new ArrayList<Pair<Float, Float>>();
    points.add(new Pair<Float, Float>(mappedEndX, mappedEndY));
    return points;
  }

}
