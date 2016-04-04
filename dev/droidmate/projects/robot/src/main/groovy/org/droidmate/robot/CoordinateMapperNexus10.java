// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org


package org.droidmate.robot;

/**
 * Maps coordinates for Nexus 10.<br/>
 * <br/>
 * By default, when Nexus 10 lies in landscape mode, its width is x=2560 and height is y=1600.<br/>
 * <br/>
 * Coordinates are like so, when looking on the tablet lying on the robot horizontally:
 *
 * <pre>
 * x0 y0    | x2560 y0
 * ---------+--------------
 * x0 y1600 | x2560 y1600
 * </pre>
 *
 * However, when the Nexus 10 switches to portrait mode, we assume the display is effectively rotated 90 degrees
 * counter-clockwise, so it has following coordinate system:
 *
 * <pre>
 * x1600 y0 | x1600 y2560
 * ---------+--------------
 * x0 y0    | x0    y2560
 * </pre>
 *
 * Thus, in portrait mode, mapped X increases as the Nexus 10's Y increases and mapped Y increases as Nexus 10's X
 * decreases.
 *
 */
public class CoordinateMapperNexus10 implements ICoordinateMapper
{
  private RobotConfiguration robotConfig;

  // http://www.google.com/nexus/10/specs/
  private final float nexus10Width = 2560;
  private final float nexus10Height = 1600;

  CoordinateMapperNexus10(RobotConfiguration robotConfig)
  {
    this.robotConfig = robotConfig;
  }

  @Override
  public float mapToX(int x, int y, boolean isLandscapeOrientation)
  {
    float mappedX;
    if (isLandscapeOrientation)
    {
      float xDeviceRangePercentage = x/nexus10Width;
      mappedX = robotConfig.robotMinX + xDeviceRangePercentage*getRobotXRange();
    }
    else
    {
     float yDeviceRangePercentage = y/nexus10Width;
     mappedX = robotConfig.robotMinX + yDeviceRangePercentage*getRobotXRange();
    }
    return mappedX;
  }

  @Override
  public float mapToY(int x, int y, boolean isLandscapeOrientation)
  {
    float mappedY;
    if (isLandscapeOrientation)
    {
      float yDeviceRangePercentage = y/nexus10Height;
      mappedY = robotConfig.robotMinY + yDeviceRangePercentage*getRobotYRange();
    }
    else
    {
     float xDeviceRangePercentage = x/nexus10Height;
     mappedY = robotConfig.robotMinY + (1-xDeviceRangePercentage)*getRobotYRange();
    }
    return mappedY;
  }

  private float getRobotXRange()
  {
    return robotConfig.robotMaxX - robotConfig.robotMinX;
  }

  private float getRobotYRange()
  {
    return robotConfig.robotMaxY - robotConfig.robotMinY;
  }

}
