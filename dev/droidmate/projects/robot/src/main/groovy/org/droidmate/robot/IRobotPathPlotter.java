// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

import java.util.List;

public interface IRobotPathPlotter
{
  List<Pair<Float, Float>> plot(float mappedStartX, float mappedStartY, float mappedEndX, float mappedEndY);
}
