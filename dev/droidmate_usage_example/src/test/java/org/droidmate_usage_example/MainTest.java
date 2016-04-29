// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate_usage_example;

import org.droidmate.configuration.Configuration;
import org.droidmate.exploration.strategy.IExplorationStrategy;
import org.droidmate.frontend.DroidmateFrontend;
import org.droidmate.frontend.ExceptionHandler;
import org.junit.Test;

import java.nio.file.FileSystems;

/**
 * This class contains tests showing example use cases of DroidMate API. To understand better how to work with DroidMate API, 
 * please explore the source code of the DroidMate classes called by the examples here. For where to find the sources and how to 
 * navigate them, please read the <tt>README.md</tt> located in the repository of:
 * <pre>https://github.com/konrad-jamrozik/droidmate</pre>
 */
public class MainTest
{
  @Test
  public void DefaultRun()
  {
    mainWithArgs(new String[]{});
  }

  @Test
  public void InlineApks()
  {
    mainWithArgs(new String[]{Configuration.pn_inline});
  }

  @Test
  public void CommonSettings()
  {
    // KJA current work
    //final String[] args = new ArgsBuilder().apksDir("apks/inlined")..timeLimitInSeconds(20).resetEvery(5).randomSeed(2).build();
    final String[] args = {};
    mainWithArgs(args);
  }

  @Test
  public void CustomExplorationStrategyAndTerminationCriterion()
  {
    IExplorationStrategy strategy = new ExampleExplorationStrategy();

    // KJA current work
//    DroidmateFrontend.main(new String[]{},  FileSystems.getDefault(), new ExceptionHandler(), strategy);

  }

  private void mainWithArgs(String[] args)
  {
    DroidmateFrontend.main(args, FileSystems.getDefault(), new ExceptionHandler());
  }


}