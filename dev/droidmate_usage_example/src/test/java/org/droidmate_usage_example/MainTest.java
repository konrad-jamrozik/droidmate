// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate_usage_example;

import org.droidmate.command.ExploreCommand;
import org.droidmate.configuration.Configuration;
import org.droidmate.exploration.strategy.IExplorationStrategyProvider;
import org.droidmate.frontend.DroidmateFrontend;
import org.droidmate.frontend.ICommandProvider;
import org.junit.Assert;
import org.junit.Test;

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
    callMain_then_assertExitStatusIs0(new String[]{});
  }

  @Test
  public void InlineApks()
  {
    callMain_then_assertExitStatusIs0(new String[]{Configuration.pn_inline});
  }

  
  @Test
  public void CommonSettings()
  {
    // KJA current work
    //final String[] args = new ArgsBuilder().apksDir("apks/inlined")..timeLimitInSeconds(20).resetEvery(5).randomSeed(2).build();
    final String[] args = {};
    callMain_then_assertExitStatusIs0(args);
  }

  @Test
  public void CustomExplorationStrategyAndTerminationCriterion()
  {
    final IExplorationStrategyProvider strategyProvider = () -> new ExampleExplorationStrategy(new ExampleTerminationCriterion());
    final ICommandProvider commandProvider = cfg -> ExploreCommand.build(cfg, strategyProvider);
    DroidmateFrontend.main(new String[]{}, commandProvider);
  }

  // KJA add tests showing how to access output dir and serialized data, i.e. something like:
  // droidmateFrontend.main()
  // outDir = new OutputDir(droidmateFrontend.defaultOutputDirPath)
  // ExplOut2 output = outDir.getOutput
  // Add test for that in droidmate main, not usage example (as it requires fixtures)
  // For usage example just empty output will suffice (probably should be generated? Or warning + empty data structure returned?)

  private void callMain_then_assertExitStatusIs0(String[] args)
  {
    int exitStatus = DroidmateFrontend.main(args, /* command */ null);
    Assert.assertEquals(0, exitStatus);
  }


}