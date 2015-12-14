// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.IExplorationOutputCollector
import org.droidmate.deprecated_still_used.Storage
import org.droidmate.deprecated_still_used.TimestampedExplorationAction
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.device.datatypes.GuiState
import org.droidmate.exploration.actions.ExplorationAction
import org.droidmate.exploration.actions.TerminateExplorationAction
import org.droidmate.exploration.strategy.IExplorationStrategy
import org.droidmate.misc.ITimeProvider
import org.droidmate.misc.TimeProvider

import static org.droidmate.exploration.actions.ExplorationAction.newResetAppExplorationAction

@TypeChecked
@Slf4j
@Deprecated
class ExplorationExecutor implements IExplorationExecutor
{
  private final IExplorationComponentsFactory componentsFactory
  private final ITimeProvider                 timeProvider
  private final IIntermediateOutputSaver      intermediateOutputSaver

  ExplorationExecutor(
    IExplorationComponentsFactory componentsFactory,
    ITimeProvider timeProvider,
    IIntermediateOutputSaver intermediateOutputSaver)
  {
    this.componentsFactory = componentsFactory
    this.timeProvider = timeProvider
    this.intermediateOutputSaver = intermediateOutputSaver
  }


  @Deprecated
  @Override
  IApkExplorationOutput tryExploreAndSerialize(String apkPackageName, String apkLaunchableActivityComponentName, IExplorableAndroidDevice device)
  {
    log.info("Exploring ${apkPackageName}")

    // doc-assert apk?.file
    // doc-assert apk is installed on the device

    warnIfDeviceDoesNotDisplayHomeScreen(device, apkPackageName)

    IExplorationOutputCollector collector = componentsFactory.createExplorationOutputCollector(apkPackageName)
    IExplorationStrategy strategy = componentsFactory.createStrategy(apkPackageName)

    IApkExplorationOutput explOutput = collector.collect {IApkExplorationOutput explOutput ->

      IDeviceExplorationDriver driver = componentsFactory.createDriver(
        device, apkPackageName, apkLaunchableActivityComponentName, explOutput)


      explorationLoop(explOutput, strategy, driver)
    }
    return explOutput
  }


  static void warnIfDeviceDoesNotDisplayHomeScreen(IExplorableAndroidDevice device, String apkPackageName)
  {
    def guiSnapshot = device?.guiSnapshot

    if (!guiSnapshot.guiState.isHomeScreen())
      log.warn("An exploration process for $apkPackageName is about to start (next instruction: instantiating the data " +
        "collector) but the device doesn't display home screen. Instead, its GUI state is: $guiSnapshot.guiState. " +
        "Continuing the exploration nevertheless, hoping that the first \"reset app\" exploration action will force the device " +
        "into the home screen.")
  }

  private void explorationLoop(IApkExplorationOutput output, IExplorationStrategy strategy, IDeviceExplorationDriver driver)
  {
    ExplorationAction currentExplAct = newResetAppExplorationAction()
    log.info("Initial exploration action: $currentExplAct")

    output.actions << TimestampedExplorationAction.from(currentExplAct, timeProvider.now)
    GuiState guiState = driver.execute(currentExplAct)

    intermediateOutputSaver.init()

    while (!(currentExplAct instanceof TerminateExplorationAction))
    {
      intermediateOutputSaver.save(output)

      currentExplAct = strategy.decide(guiState)
      output.actions << TimestampedExplorationAction.from(currentExplAct, timeProvider.now)
      guiState = driver.execute(currentExplAct)
    }

    output.explorationEndTime = timeProvider.now
    log.info("Ended exploration of " + output.appPackageName)
  }


  public static ExplorationExecutor build(Configuration cfg, Storage storage)
  {
    def timeProvider = new TimeProvider()
    ExplorationComponentsFactory componentsFactory = ExplorationComponentsFactory.build(cfg, timeProvider, storage)
    IIntermediateOutputSaver intermediateOutputSaver = new IntermediateOutputSaver(timeProvider, storage)
    ExplorationExecutor executor = new ExplorationExecutor(componentsFactory, timeProvider, intermediateOutputSaver)
    return executor
  }


}