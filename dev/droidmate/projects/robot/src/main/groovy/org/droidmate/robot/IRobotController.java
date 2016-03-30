// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

public interface IRobotController
{
  boolean connect() throws RobotException;

  void moveToMinXY(boolean isLandscapeOrientation) throws RobotException;

  void moveToCoordinates(int x, int y, int speed, boolean isLandscapeOrientation) throws RobotException;

  void moveToMappedCoordinates(float mappedX, float mappedY, int speed, boolean isLandscapeOrientation)
  throws RobotException;

  void moveDown() throws RobotException;

  void moveUp() throws RobotException;

  void disconnect();

  void moveToMaxXY(boolean isLandscapeOrientation) throws RobotException;

  void moveToBackButton(boolean isLandscapeOrientation) throws RobotException;

  void calibrate() throws RobotException;

  void runRaw(String command) throws RobotException;



}
