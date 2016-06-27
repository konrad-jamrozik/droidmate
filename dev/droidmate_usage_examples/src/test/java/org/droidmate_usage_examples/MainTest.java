// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate_usage_examples;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import org.droidmate.android_sdk.IApk;
import org.droidmate.command.ExploreCommand;
import org.droidmate.common.exploration.datatypes.Widget;
import org.droidmate.configuration.Configuration;
import org.droidmate.device.datatypes.IDeviceGuiSnapshot;
import org.droidmate.exceptions.DeviceException;
import org.droidmate.exploration.actions.ExplorationAction;
import org.droidmate.exploration.actions.IExplorationActionRunResult;
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult;
import org.droidmate.exploration.actions.WidgetExplorationAction;
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2;
import org.droidmate.exploration.strategy.IExplorationStrategyProvider;
import org.droidmate.frontend.DroidmateFrontend;
import org.droidmate.frontend.ICommandProvider;
import org.droidmate.logcat.IApiLogcatMessage;
import org.droidmate.report.OutputDir;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains tests showing example use cases of DroidMate API. To understand better how to work with DroidMate API, 
 * please explore the source code of the DroidMate classes called by the examples here. For where to find the sources and how to 
 * navigate them, please read <pre>https://github.com/konrad-jamrozik/droidmate/blob/master/README.md</pre>.
 */
public class MainTest
{
  /**
   * <p>
   * This test shows how to access DroidMate API with default settings. If you run it right off the bat, DroidMate will inform 
   * you into which dir to put apks. If you put apks there, DroidMate will inform you why and how you should inline them.
   *
   * </p><p>
   * DroidMate will also tell you where to look for its run output. Both the .txt files and serialized results. To see how
   * to access serialized results, see {@link #deserialize_and_work_with_exploration_result()}
   *
   * </p><p>
   * In any case, please read the README.md mentioned in {@link MainTest}.
   *
   * </p>
   */
  @Test
  public void explore_with_default_settings()
  {
    callMainThenAssertExitStatusIs0(new String[]{});
  }

  /**
   * <p>
   * This test shows how to access various part of the data structure serialized by DroidMate to file system, containing all the
   * results from the exploration. Note that the methods used are not exhaustive. Explore the sources
   * of the used types to find out more.
   * 
   * </p><p>
   * For details of the run used to obtain the fixture for this test, 
   * please see {@code dev/droidmate_usage_examples/src/test/resources}. 
   * The apk used to obtain the fixture is located in {@code dev/droidmate_usage_examples/apks/inlined}.`  
   *   
   * </p>
   */
  @Test
  public void deserialize_fixture_and_work_with_exploration_result() throws IOException, URISyntaxException
  {
    workWithDroidmateOutput(copyDroidmateOutputFixtureToDir("mock_droidmate_output_dir").getParent());
  }
  
  /**
   * <p>
   * This test is like {@link #deserialize_fixture_and_work_with_exploration_result}, but it doesn't work on a fixture, instead
   * it works on default DroidMate output dir.
   *
   * </p><p>
   * To get any meaningful output to stdout from this test, first run DroidMate on an inlined apk. To understand how to obtain
   * an inlined apk, please read the doc mentioned in {@link MainTest}.
   *
   * </p>
   */
  @Test
  public void deserialize_and_work_with_exploration_result() throws IOException, URISyntaxException
  {
    workWithDroidmateOutput(Configuration.defaultDroidmateOutputDir);
  }
  
  /**
   * This test will make DroidMate inline all the apks present in the default input directory.
   */
  @Test
  public void inline_apks()
  {
    callMainThenAssertExitStatusIs0(new String[]{Configuration.pn_inline});
  }

  /**
   * <p>
   * This test shows some common settings you would wish to override when running DroidMate. In any case, you can always consult
   * source of Configuration class for more settings.
   * 
   * </p><p>
   * This test has been used to obtain fixture for {@link #deserialize_fixture_and_work_with_exploration_result()}. 
   * 
   * </p>
   */
  @Test
  public void explore_with_common_settings_changed()
  {
    List<String> args = new ArrayList<>();
    
    // Notation explanation: "pn" means "parameter name"
    
    Collections.addAll(args, Configuration.pn_apksDir, Configuration.defaultApksDir);
    Collections.addAll(args, Configuration.pn_timeLimit, "10");
    Collections.addAll(args, Configuration.pn_resetEveryNthExplorationForward, String.valueOf(Configuration.defaultResetEveryNthExplorationForward));
    Collections.addAll(args, Configuration.pn_randomSeed, "43");
    Collections.addAll(args, Configuration.pn_androidApi, Configuration.api19);
    
    callMainThenAssertExitStatusIs0(args.toArray(new String[args.size()]));
  }

  /**
   * This test shows how to make DroidMate run with your custom exploration strategy and termination criterion. Right now there
   * is no base ExplorationStrategy from which you can inherit and the ITerminationCriterion interface is a bit rough. To help
   * yourself, see how the actual DroidMate exploration strategy is implemented an its components 
   * <a href="https://github.com/konrad-jamrozik/droidmate/blob/ffd6da96e16978418d34b7f186699423d548e1f3/dev/droidmate/projects/core/src/main/groovy/org/droidmate/exploration/strategy/ExplorationStrategy.groovy#L90">on GitHub</a>
   */
  @Test
  public void explore_with_custom_exploration_strategy_and_termination_criterion()
  {
    final IExplorationStrategyProvider strategyProvider = () -> new ExampleExplorationStrategy(new ExampleTerminationCriterion());
    final ICommandProvider commandProvider = cfg -> ExploreCommand.build(cfg, strategyProvider);
    callMainThenAssertExitStatusIs0(new String[]{}, commandProvider);
  }
  
  private void callMainThenAssertExitStatusIs0(String[] args)
  {
    // null commandProvider means "do not override DroidMate command (and thus: any components) with custom implementation" 
    final ICommandProvider commandProvider = null;
    callMainThenAssertExitStatusIs0(args, commandProvider);
  }

  private void callMainThenAssertExitStatusIs0(String[] args, ICommandProvider commandProvider)
  {
    int exitStatus = DroidmateFrontend.main(args, commandProvider);
    Assert.assertEquals(0, exitStatus);
  }

  private File copyDroidmateOutputFixtureToDir(String targetDirPath) throws IOException, URISyntaxException
  {
    File targetDir = new File(targetDirPath);
    //noinspection ResultOfMethodCallIgnored
    targetDir.mkdir();
    if (!targetDir.exists())
      throw new IllegalStateException();

    final URL fixtureURL = Iterables.getOnlyElement(
      Collections.list(
        // "2016 May 13 1003 com.ht.manga.gpanda.ser2"
        // 2016 May 13 1205 com.adobe.reader.ser2
        ClassLoader.getSystemResources("fixture_output_device1/2016 May 13 1240 ru.tubin.bp.ser2")));
    final File fixtureFile = new File(fixtureURL.toURI());


    final File targetFile = new File(targetDirPath, fixtureFile.getName());
    Files.copy(fixtureFile, targetFile);

    if (!targetFile.exists())
      throw new IllegalStateException();

    return targetFile;
  }

  
  private void workWithDroidmateOutput(String outputDirPath)
  {
    final List<IApkExplorationOutput2> output = new OutputDir(Paths.get(outputDirPath)).getExplorationOutput2();
    output.forEach(this::workWithSingleApkExplorationOutput);
  }

  /**
   * Please see the comment on {@link #deserialize_and_work_with_exploration_result}.
   */
  private void workWithSingleApkExplorationOutput(IApkExplorationOutput2 apkOut)
  {
    final IApk apk = apkOut.getApk();
    if (!apkOut.getExceptionIsPresent())
    {

      int actionCounter = 0;
      for (RunnableExplorationActionWithResult actionWithResult : apkOut.getActRess())
      {
        actionCounter++;

        final ExplorationAction action = actionWithResult.getAction().getBase();
        System.out.println("Action " + actionCounter + " is of type " + action.getClass().getSimpleName());

        if (action instanceof WidgetExplorationAction)
        {
          WidgetExplorationAction widgetAction = (WidgetExplorationAction) action;
          Widget w = widgetAction.getWidget();
          System.out.println("Text of acted-upon widget of given action: " + w.getText());
        }

        final IExplorationActionRunResult result = actionWithResult.getResult();
        final IDeviceGuiSnapshot guiSnapshot = result.getGuiSnapshot();

        System.out.println("Action " + actionCounter + " resulted in a screen containing following actionable widgets: ");
        for (Widget widget : guiSnapshot.getGuiState().getActionableWidgets())
          System.out.println("Widget of class " + widget.getClassName() + " with bounds: " + widget.getBoundsString());

        final List<IApiLogcatMessage> apiLogs = result.getDeviceLogs().getApiLogsOrEmpty();
        System.out.println("Action " + actionCounter + " resulted in following calls to monitored Android framework's APIs being made:");
        for (IApiLogcatMessage apiLog : apiLogs)
          System.out.println(apiLog.getObjectClass() + "." + apiLog.getMethodName());
      }

      // Convenience method for accessing GUI snapshots resulting from all actions.
      @SuppressWarnings("unused")
      final List<IDeviceGuiSnapshot> guiSnapshots = apkOut.getGuiSnapshots();

      // Convenience method for accessing API logs resulting from all actions.
      @SuppressWarnings("unused")
      final List<List<IApiLogcatMessage>> apiLogs = apkOut.getApiLogs();
    } else
    {
      @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
      final DeviceException exception = apkOut.getException();
      System.out.println("Exploration of " + apk.getFileName() + " resulted in exception: " + exception.toString());
    }
  }
}