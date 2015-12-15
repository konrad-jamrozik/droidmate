// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.command.exploration

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.IApk
import org.droidmate.configuration.Configuration
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.actions.IExplorationActionRunResult
import org.droidmate.exploration.actions.IRunnableExplorationAction
import org.droidmate.exploration.actions.RunnableExplorationAction
import org.droidmate.exploration.actions.RunnableTerminateExplorationAction
import org.droidmate.exploration.data_aggregators.ApkExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.IDeviceWithReadableLogs
import org.droidmate.exploration.strategy.ExplorationStrategy
import org.droidmate.exploration.strategy.IExplorationStrategy
import org.droidmate.misc.ITimeProvider
import org.droidmate.misc.TimeProvider

import static org.droidmate.exploration.actions.ExplorationAction.newResetAppExplorationAction

@TypeChecked
@Slf4j
class Exploration implements IExploration
{

  private final Configuration cfg
  private final ITimeProvider timeProvider

  Exploration(Configuration cfg, ITimeProvider timeProvider)
  {
    this.timeProvider = timeProvider
    this.cfg = cfg
  }

  public static Exploration build(Configuration cfg, ITimeProvider timeProvider = new TimeProvider())
  {
    return new Exploration(cfg, timeProvider)
  }

  @Override
  IApkExplorationOutput2 tryRun(IApk app, IDeviceWithReadableLogs device) throws DeviceException
  {
    log.info("run(${app?.packageName}, device)")

    assert app != null
    assert device != null

    tryAssertDeviceHasPackageInstalled(device, app.packageName)
    tryWarnDeviceDisplaysHomeScreen(device, app.fileName)

    IApkExplorationOutput2 output = explorationLoop(app, device)

    output.verify()

    if (!output.noException)
      log.warn("! Encountered ${output.exception.class.simpleName} during the exploration of ${app.packageName} " +
        "after already obtaining some exploration output.")

    return output
  }

  public IApkExplorationOutput2 explorationLoop(IApk app, IDeviceWithReadableLogs device)
  {
    // Construct initial action and run it on the device to obtain initial result.
    IRunnableExplorationAction action = RunnableExplorationAction.from(
      newResetAppExplorationAction(/* isFirst */ true), timeProvider.now)

    log.debug("explorationLoop(app=${app?.fileName}, device)")
    log.info("Initial action: ${action.base}")

    assert app != null
    assert device != null

    IExplorationActionRunResult result = action.run(app, device)

    // Construct the output holder
    IApkExplorationOutput2 output = new ApkExplorationOutput2(app)
    // Write the initial action and its execution result to the output holder.
    output.add(action, result)

    // Construct the strategy making decisions in the exploration loop.
    IExplorationStrategy strategy = ExplorationStrategy.build(app.packageName, cfg)

    // Execute the exploration loop proper, starting with the values of initial reset action and its result.
    while (result.successful && !(action instanceof RunnableTerminateExplorationAction))
    {
      action = RunnableExplorationAction.from(strategy.decide(result), timeProvider.now)
      result = action.run(app, device)
      output.add(action, result)
    }
    assert !result.successful || action instanceof RunnableTerminateExplorationAction

    output.explorationEndTime = timeProvider.now

    assert output != null
    return output
  }

  private void tryAssertDeviceHasPackageInstalled(IExplorableAndroidDevice device, String packageName) throws DeviceException
  {
    log.trace("tryAssertDeviceHasPackageInstalled(device, $packageName)")

    // KJA bug #994
    assert device.hasPackageInstalled(packageName)
  }

  private void tryWarnDeviceDisplaysHomeScreen(IExplorableAndroidDevice device, String fileName) throws DeviceException
  {
    log.trace("tryWarnDeviceDisplaysHomeScreen(device, $fileName)")

    IDeviceGuiSnapshot initialGuiSnapshot = device.guiSnapshot

    if (!initialGuiSnapshot.guiState.isHomeScreen())
      log.warn("An exploration process for $fileName is about to start but the device doesn't display home screen. " +
        "Instead, its GUI state is: $initialGuiSnapshot.guiState. " +
        "Continuing the exploration nevertheless, hoping that the first \"reset app\" " +
        "exploration action will force the device into the home screen.")
  }
}
