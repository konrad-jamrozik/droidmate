// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

public interface ICoordinateMapper
{
  float mapToX(int x, int y, boolean isLandscapeOrientation);

  float mapToY(int x, int y, boolean isLandscapeOrientation);


}
