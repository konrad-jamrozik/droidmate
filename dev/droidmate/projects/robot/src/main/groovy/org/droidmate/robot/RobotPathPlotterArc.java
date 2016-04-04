// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RobotPathPlotterArc implements IRobotPathPlotter
{

  // 90 degrees
  private static double quarterCircle = Angle.angle(new Coordinate(0, 1));


  public double lastPlottedArcLength;

  private RobotConfiguration robotConfig;

  private static Logger log = LoggerFactory.getLogger(SerialDriver.class.getSimpleName());

  RobotPathPlotterArc(RobotConfiguration robotConfig)
  {
    this.robotConfig = robotConfig;
  }

  @Override
  public List<Pair<Float, Float>> plot(float mappedStartX, float mappedStartY, float mappedEndX, float mappedEndY)
  {
    ArrayList<Pair<Float, Float>> points = new ArrayList<Pair<Float, Float>>();

    LineString arc = computeArc(
        new Coordinate(mappedStartX, mappedStartY),
        new Coordinate(mappedEndX, mappedEndY),
      robotConfig.robotArcSegmentCount);
    log.trace("Arc length: {}", arc.getLength());

    // We iterate from 1, not 0, because we skip the starting point, equivalent to (mappedStartX, mappedStartY).
    for (int i = 1; i < arc.getNumPoints(); i++)
    {
      Point point = arc.getPointN(i);
      points.add(new Pair<Float, Float>((float) point.getX(), (float) point.getY()));
    }

    lastPlottedArcLength = arc.getLength();
    return points;
  }

  private static LineString computeArc(Coordinate startPoint, Coordinate endPoint, int segmentCount)
  {

    double lowX = Math.min(startPoint.x, endPoint.x);
    double highX = Math.max(startPoint.x, endPoint.x);
    double deltaX = highX - lowX;

    double lowY = Math.min(startPoint.y, endPoint.y);
    double highY = Math.max(startPoint.y, endPoint.y);
    double deltaY = highY - lowY;

    GeometricShapeFactory gsf = new GeometricShapeFactory();
    gsf.setNumPoints(1 + segmentCount);
    gsf.setWidth(deltaX * 2);
    gsf.setHeight(deltaY * 2);

    double centreX = 0;
    double centreY = 0;
    double startAng = -1;

    if (startPoint.y == lowY)
    {
      if (endPoint.x == lowX)
      {
        startAng = 0; // 1st quarter
        centreX = lowX;
        centreY = lowY;

      }
      else if (endPoint.x == highX)
      {
        startAng = 3 * quarterCircle; // 4th quarter
        centreX = lowX;
        centreY = highY;

      }
    }
    else if (startPoint.y == highY)
    {
      if (endPoint.x == lowX)
      {
        startAng = quarterCircle; // 2nd quarter
        centreX = highX;
        centreY = lowY;
      }
      else if (endPoint.x == highX)
      {
        startAng = 2 * quarterCircle; // 3rd quarter
        centreX = highX;
        centreY = highY;
      }
    }

    gsf.setCentre(new Coordinate(centreX, centreY));

    LineString arc = gsf.createArc(startAng, quarterCircle);

    return arc;
  }
}
