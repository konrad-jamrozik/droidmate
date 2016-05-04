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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains tests showing example use cases of DroidMate API. To understand better how to work with DroidMate API, 
 * please explore the source code of the DroidMate classes called by the examples here. For where to find the sources and how to 
 * navigate them, please read the <tt>README.md</tt> located in the repository of:
 * <pre>https://github.com/konrad-jamrozik/droidmate</pre>
 */
public class MainTest
{
  @Test
  public void explore_with_default_settings_then_access_output()
  {
    call_main_then_assert_exit_status_is_0(new String[]{});
  }

  @Test
  public void inline_apks()
  {
    call_main_then_assert_exit_status_is_0(new String[]{Configuration.pn_inline});
  }


  // KJA add tests showing how to access output dir and serialized data, i.e. something like:
  // droidmateFrontend.main()
  // outDir = new OutputDir(droidmateFrontend.defaultOutputDirPath)
  // ExplOut2 output = outDir.getOutput
  // Add test for that in droidmate main, not usage example (as it requires fixtures)
  // For usage example just empty output will suffice (probably should be generated? Or warning + empty data structure returned?) 

  
  @Test
  public void explore_with_common_settings_changed()
  {
    List<String> args = new ArrayList<>();
    
    // Notation explanation: "pn" means "parameter name"
    
    Collections.addAll(args, Configuration.pn_apksDir, "apks/inlined");
    Collections.addAll(args, Configuration.pn_timeLimit, "20");
    Collections.addAll(args, Configuration.pn_resetEveryNthExplorationForward, "5");
    Collections.addAll(args, Configuration.pn_randomSeed, "43");
    
    // Look into Configuration class for more settings.
    
    call_main_then_assert_exit_status_is_0(args.toArray(new String[args.size()]));
  }

  @Test
  public void explore_with_custom_exploration_strategy_and_termination_criterion()
  {
    final IExplorationStrategyProvider strategyProvider = () -> new ExampleExplorationStrategy(new ExampleTerminationCriterion());
    final ICommandProvider commandProvider = cfg -> ExploreCommand.build(cfg, strategyProvider);
    call_main_then_assert_exit_status_is_0(new String[]{}, commandProvider);
  }


  private void call_main_then_assert_exit_status_is_0(String[] args)
  {
    call_main_then_assert_exit_status_is_0(args, null);
  }

  private void call_main_then_assert_exit_status_is_0(String[] args, ICommandProvider commandProvider)
  {
    int exitStatus = DroidmateFrontend.main(args, commandProvider);
    Assert.assertEquals(0, exitStatus);
  }


}