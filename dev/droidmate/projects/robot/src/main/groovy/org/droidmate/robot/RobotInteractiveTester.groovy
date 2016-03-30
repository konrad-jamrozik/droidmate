// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot

import com.google.common.base.Splitter
import com.google.common.collect.Lists
import org.droidmate.common.DroidmateException
import org.droidmate.configuration.Configuration

public class RobotInteractiveTester
{

  private static RobotConfiguration robotConfig

  // These values will be set from config.
  private static int speed = 0;
  private static int lastX = 0;

  private static int lastY = 0;

  public static final String msg_setupSuccessful = "Robot controller set up successfully."

  public static final String msg_commandChoice =
    "Please supply a command. Available commands, comma separated: calibrate, demo, raw <command>, " +
      "startCorner, endCorner, backP, backL, x <X>, y <Y>, xy <X> <Y>, speed <SPEED>, down, up, end"

  public static final String msg_terminated = "Robot tester terminated."

  /**
   * @param args
   */
  public static void main(String[] args) throws DroidmateException
  {
    Reader userInputReader = new InputStreamReader(System.in, "UTF-8")
    PrintWriter outputWriter = new PrintWriter(System.out, true)
    Configuration config = Configuration.getDefault()

    robotConfig = new RobotConfiguration();
    if (args.any { it.contains("echoCable")})
      robotConfig.echoCable = true;

    IRobotController robotController = new RobotController(
      config,
      robotConfig,
      userInputReader,
      new SerialDriver(robotConfig),
      new CoordinateMapperNexus10(
        config,
        robotConfig),
      new RobotPathPlotterArc(config, robotConfig)
    )

    speed = robotConfig.robotSpeed;
    lastX = robotConfig.robotMinX;
    lastY = robotConfig.robotMinY;

    boolean connectionStatus = robotController.connect();

    if (!connectionStatus)
    {
      outputWriter.println "Failed to connect to robot, aborting."
      return;
    }

    outputWriter.println msg_setupSuccessful

    String command = "start";
    while (!command.equals("end"))
    {
      outputWriter.println msg_commandChoice
      command = userInputReader.readLine()

      if (command.equals("calibrate"))
      {
        robotController.calibrate();
      } else if (command.equals("demo"))
        runDemo(robotController);
      else if (command.startsWith("raw"))
        runRaw(robotController, command);
      else if (command.startsWith("speed"))
        setSpeed(command);
      else if (command.equals("startCorner"))
        robotController.moveToMinXY(true /* isLandscapeOrientation */);
      else if (command.equals("endCorner"))
        robotController.moveToMaxXY(true /* isLandscapeOrientation */);
      else if (command.equals("backP"))
        robotController.moveToBackButton(false /* isLandscapeOrientation */);
      else if (command.equals("backL"))
        robotController.moveToBackButton(true /* isLandscapeOrientation */);
      else if (command.startsWith("xyp"))
        runXYportrait(robotController, command);
      else if (command.startsWith("xy"))
        runXY(robotController, command);
      else if (command.startsWith("x"))
        runX(robotController, command);
      else if (command.startsWith("y"))
        runY(robotController, command);
      else if (command.equals("down"))
        robotController.moveDown();
      else if (command.equals("up"))
        robotController.moveUp();
      else if (!command.equals("end"))
        outputWriter.println "Unknown command: " + command

    }

    robotController.disconnect();

    outputWriter.println msg_terminated
  }

  private static void setSpeed(String command)
  {
    List<Integer> values = extractValues(command);
    speed = values.get(0);
  }

  private static void runX(IRobotController robotController, String command) throws RobotException
  {
    List<Integer> values = extractValues(command);
    int xCoor = values.get(0);

    robotController.moveToCoordinates(xCoor, lastY, speed, true /* isLandscapeOrientation */);

    lastX = xCoor;
  }

  private static void runY(IRobotController robotController, String command) throws RobotException
  {
    List<Integer> values = extractValues(command);
    int yCoor = values.get(0);

    robotController.moveToCoordinates(lastX, yCoor, speed, true /* isLandscapeOrientation */);

    lastY = yCoor;
  }

  private static void runXYportrait(IRobotController robotController, String command) throws RobotException
  {
    List<Integer> values = extractValues(command);
    int xCoor = values.get(0);
    int yCoor = values.get(1);

    robotController.moveToCoordinates(xCoor, yCoor, speed, false /* isLandscapeOrientation */);

    lastX = xCoor;
    lastY = yCoor;
  }

  private static void runXY(IRobotController robotController, String command) throws RobotException
  {
    List<Integer> values = extractValues(command);
    int xCoor = values.get(0);
    int yCoor = values.get(1);

    robotController.moveToCoordinates(xCoor, yCoor, speed, true /* isLandscapeOrientation */);

    lastX = xCoor;
    lastY = yCoor;
  }

  private static List<Integer> extractValues(String command)
  {
    Iterable<String> split = Splitter.on(' ').split(command);
    ArrayList<String> splitList = Lists.newArrayList(split);
    splitList.remove(0);
    List<Integer> values = new ArrayList<Integer>();
    for (String value : splitList)
    {
      values.add(Integer.valueOf(value));
    }
    return values;
  }

  private static void runRaw(IRobotController robotController, String command) throws RobotException
  {
    robotController.runRaw(command.split(" ").drop(1).join(" "));
  }

  private static void runDemo(IRobotController robotController) throws RobotException
  {
    // nexus 10 dimensions in landscape mode
    int x = 2560;
    int cx = x / 2;
    int y = 1600;
    int cy = y / 2;

    // robotController.moveToCoordinates(x, y, speed, true);
//    robotController.moveToCoordinates(x, 0, speed, true);
//    robotController.moveTocalCoordinates(0, y, speed, true);
//    robotController.moveToCoordinates(0, 0, speed, true);
    robotController.moveToCoordinates(0, 0, robotConfig.robotSpeedSlow, true);
    click(robotController);
    robotController.moveToCoordinates((int) (cx / 2), (int) (cy / 2), speed, true);
    click(robotController);
    robotController.moveToCoordinates(cx, cy, speed, true);
    click(robotController);
    robotController.moveToCoordinates(x, y, speed, true);
    click(robotController);
    robotController.moveToCoordinates(cx, cy, speed, true);
    click(robotController);
    robotController.moveToCoordinates(x, 0, speed, true);
    click(robotController);
    robotController.moveToCoordinates(0, y, speed, true);
    click(robotController);
    robotController.moveToCoordinates((int) x, (int) (2 * y / 3), speed, true);
    click(robotController);
    robotController.moveToCoordinates((int) 0, (int) (1 * y / 3), speed, true);
    click(robotController);
    robotController.moveToCoordinates(x, y, speed, true);
    click(robotController);
    robotController.moveToCoordinates(0, 0, speed, true);
  }

  private static void click(IRobotController robotController) throws RobotException
  {
    robotController.moveDown();
    robotController.moveUp();
  }

}
