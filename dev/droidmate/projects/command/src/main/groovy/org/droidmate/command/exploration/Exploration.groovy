// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

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
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.exploration.strategy.ExplorationStrategy
import org.droidmate.exploration.strategy.IExplorationStrategy
import org.droidmate.exploration.strategy.IExplorationStrategyProvider
import org.droidmate.misc.Failable
import org.droidmate.misc.ITimeProvider
import org.droidmate.misc.TimeProvider

import static org.droidmate.exploration.actions.ExplorationAction.newResetAppExplorationAction

@TypeChecked
@Slf4j
class Exploration implements IExploration
{

  private final Configuration                cfg
  private final ITimeProvider                timeProvider
  private final IExplorationStrategyProvider strategyProvider


  Exploration(Configuration cfg, ITimeProvider timeProvider, IExplorationStrategyProvider strategyProvider)
  {
    this.timeProvider = timeProvider
    this.cfg = cfg
    this.strategyProvider = strategyProvider
  }

  public static Exploration build(Configuration cfg,
                                  ITimeProvider timeProvider = new TimeProvider(),
                                  IExplorationStrategyProvider strategyProvider = {ExplorationStrategy.build(cfg)})
  {
    return new Exploration(cfg, timeProvider, strategyProvider)
  }

  @Override
  Failable<IApkExplorationOutput2, DeviceException> run(IApk app, IRobustDevice device)
  {
    log.info("run(${app?.packageName}, device)")

    assert app != null
    assert device != null
    device.resetTimeSync()

    try
    {
      tryDeviceHasPackageInstalled(device, app.packageName)
      tryWarnDeviceDisplaysHomeScreen(device, app.fileName)
    } catch (DeviceException e)
    {
      return new Failable<IApkExplorationOutput2, DeviceException>(null, e)
    }

    IApkExplorationOutput2 output = explorationLoop(app, device)

    output.verify()

    if (output.exceptionIsPresent)
      log.warn("! Encountered ${output.exception.class.simpleName} during the exploration of ${app.packageName} " +
        "after already obtaining some exploration output.")

    return new Failable<IApkExplorationOutput2, DeviceException>(output, output.exceptionIsPresent ? output.exception : null)
  }

  public IApkExplorationOutput2 explorationLoop(IApk app, IRobustDevice device)
  {
    // Construct initial action and run it on the device to obtain initial result.
    IRunnableExplorationAction action = RunnableExplorationAction.from(
      newResetAppExplorationAction(/* isFirst */ true), timeProvider.now)

    log.debug("explorationLoop(app=${app?.fileName}, device)")
    log.info("Initial action: ${action.base}")

    assert app != null
    assert device != null

    // Construct the object that will hold the exploration output and that will be returned from this method.
    IApkExplorationOutput2 output = new ApkExplorationOutput2(app)

    output.explorationStartTime = timeProvider.now
    log.debug("Exploration start time: " + output.explorationStartTime)

    IExplorationActionRunResult result = action.run(app, device)

    // Write the initial action and its execution result to the output holder.
    output.add(action, result)

    IExplorationStrategy strategy = strategyProvider.provideNewInstance()

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

  private void tryDeviceHasPackageInstalled(IExplorableAndroidDevice device, String packageName) throws DeviceException
  {
    log.trace("tryDeviceHasPackageInstalled(device, $packageName)")

    if (!device.hasPackageInstalled(packageName))
      throw new DeviceException()
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
