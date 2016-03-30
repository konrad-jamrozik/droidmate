// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

import com.beust.jcommander.Parameter;
import org.droidmate.configuration.IConfiguration;

public class RobotConfiguration implements IConfiguration
{
  private static final String PARAM_NAME_ROBOT_RESPONSE_CONFIRMATION = "-robotResponseConfirmation";

  @Parameter(names = {"-echoCable"}, description =
    "Denotes if the 'echo' cable is connected instead of the robot. It is used for debugging purposes. " +
    "The cable echoes back the command sent to it instead of sending the robot response " +
    "(see "+PARAM_NAME_ROBOT_RESPONSE_CONFIRMATION+" for the default robot response).")
  public boolean echoCable = false;

  @Parameter(names = {"-robotArcSegmentCount"}, description =
    "The number of segments from which the arcs followed by the robot are composed of.")
  public int robotArcSegmentCount = 30;

  @Parameter(names = {"-robotBackButtonLandscapeX"}, description =
    "The X coordinate value of the back button in landscape mode required by the robot.")
  public int robotBackButtonLandscapeX = 109;

  @Parameter(names = {"-robotBackButtonLandscapeY"}, description =
    "The Y coordinate value of the back button in landscape mode required by the robot.")
  public int robotBackButtonLandscapeY = 140;

  @Parameter(names = {"-robotBackButtonPortraitX"}, description =
    "The X coordinate value of the back button in portrait mode required by the robot.")
  public int robotBackButtonPortraitX = 238;

  @Parameter(names = {"-robotBackButtonPortraitY"}, description =
    "The Y coordinate value of the back button in portrait mode required by the robot.")
  public int robotBackButtonPortraitY = 100;

  @Parameter(names = {"-robotLiftedZ"}, description =
    "The Z coordinate value for the robot in the lifted state.")
  public int robotLiftedZ = 0;

  @Parameter(names = {"-robotLoweredZ"}, description =
    "The Z coordinate value for the robot in which it is lowered to touch the tablet.")
  public int robotLoweredZ = 8;

  @Parameter(names = {"-robotMaxX"}, description =
    "The X coordinate of robot that would point on the farthest corner of the tablet.")
  public int robotMaxX = 240;

  @Parameter(names = {"-robotMaxY"}, description =
    "The Y coordinate of robot that would point on the farthest corner of the tablet.")
  public int robotMaxY = 145;

  @Parameter(names = {"-robotMinX"}, description =
    "The X coordinate of robot that would point on the closest corner of the tablet.")
  public int robotMinX = 24;

  @Parameter(names = {"-robotMinY"}, description =
    "The Y coordinate of robot that would point on the closest corner of the tablet.")
  public int robotMinY = 8;

  @Parameter(names = {PARAM_NAME_ROBOT_RESPONSE_CONFIRMATION}, description =
    "The value to be expected from robot when the command completed successfully.")
  public String robotResponseConfirmation = "ok";

  @Parameter(names = {"-robotResponseTimeout"}, description =
    "How long DroidMate should await, in milliseconds, for robot's response on serial port after issuing a command to" +
      " it.")

  public int robotResponseTimeout = 30 * 1000;

  @Parameter(names = {"-robotSpeed"}, description =
    "The speed at which robot moves by default.")

  public int robotSpeed = 12000;

  @Parameter(names = {"-robotSpeedMax"}, description =
    "The maximum speed at which robot can move.")

  public int robotSpeedMax = 35000;

  @Parameter(names = {"-robotSpeedSlow"}, description =
    "The slow speed at which robot moves when executing some commands like \"move to start corner\"")
  public int robotSpeedSlow = 6000;

}