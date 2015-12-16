// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.deprecated

import com.google.common.base.Ticker
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.*
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.exploration.device.DeviceMessagesReader
import org.droidmate.exploration.device.IDeviceMessagesReader
import org.droidmate.exploration.strategy.*
import org.droidmate.misc.ITimeProvider
import org.droidmate.misc.TimeProvider

@Deprecated
class ExplorationComponentsFactory implements IExplorationComponentsFactory
{

  private Configuration                     cfg
  private IStorage                          storage
  private IForwardExplorationSpecialCases   forwardExplorationSpecialCases
  private IValidDeviceGuiSnapshotProvider   validGuiSnapshotProvider
  private Reader                            userInputReader
  private PrintWriter                       programOutputPrintWriter
  private ExplorationOutputCollectorFactory explorationOutputCollectorFactory

  ExplorationComponentsFactory(
    Configuration cfg,
    ITimeProvider timeProvider,
    IStorage storage,
    IForwardExplorationSpecialCases forwardExplorationSpecialCases,
    IValidDeviceGuiSnapshotProvider validGuiSnapshotProvider,
    Reader userInputReader,
    PrintWriter programOutputPrintWriter)
  {
    this.cfg = cfg
    this.storage = storage
    this.forwardExplorationSpecialCases = forwardExplorationSpecialCases
    this.validGuiSnapshotProvider = validGuiSnapshotProvider
    this.userInputReader = userInputReader
    this.programOutputPrintWriter = programOutputPrintWriter
    this.explorationOutputCollectorFactory = new ExplorationOutputCollectorFactory(timeProvider, storage)
  }

  @Override
  IExplorationStrategy createStrategy(String appPackageName)
  {
    TerminationCriterion criterion = new TerminationCriterion(cfg, cfg.timeLimit, Ticker.systemTicker())
    return new ExplorationStrategy(this.createWidgetStrategy(appPackageName), appPackageName, this.cfg, criterion, this.forwardExplorationSpecialCases)
  }

  @Override
  IWidgetStrategy createWidgetStrategy(String appPackageName)
  {
    return new WidgetStrategy(appPackageName, cfg.randomSeed, cfg.alwaysClickFirstWidget, cfg.widgetIndexes)
  }

  @Override
  IDeviceExplorationDriver createDriver(IExplorableAndroidDevice device,
                                        String appPackageName,
                                        String appLaunchableActivityComponentName,
                                        IApkExplorationOutput explorationOutput)
  {
    return new DeviceExplorationDriver(
      this,
      cfg.deployRawApks,
      device,
      appPackageName,
      appLaunchableActivityComponentName,
      explorationOutput)
  }

  @Override
  IExplorationActionToVerifiableDeviceActionsTranslator createActionsTranslator(
    String appPackageName, String appLaunchableActivityComponentName)
  {
    return new ExplorationActionTranslator(appPackageName, appLaunchableActivityComponentName, cfg.softReset)
  }

  @Override
  IVerifiableDeviceActionsExecutor createVerifiableDeviceActionsExecutor(IExplorableAndroidDevice device, IApkExplorationOutput explorationOutput)
  {
    return new VerifiableDeviceActionsExecutor(validGuiSnapshotProvider, cfg, cfg.logWidgets, device, explorationOutput, userInputReader, programOutputPrintWriter)
  }

  @Override
  IExplorationOutputCollector createExplorationOutputCollector(String appPackageName)
  {
    return explorationOutputCollectorFactory.create(appPackageName)
  }

  @Override
  IDeviceMessagesReader createDeviceMessagesReader(IExplorableAndroidDevice device)
  {
    return new DeviceMessagesReader(device, cfg.monitorServerStartTimeout, cfg.monitorServerStartQueryInterval)
  }

  public static ExplorationComponentsFactory build(Configuration cfg, TimeProvider timeProvider, Storage storage)
  {
    def componentsFactory = new ExplorationComponentsFactory(
      cfg,
      timeProvider,
      storage,
      new ForwardExplorationSpecialCases(),
      new ValidUiautomatorWindowDumpProvider(cfg.getValidGuiSnapshotRetryAttempts, cfg.getValidGuiSnapshotRetryDelay),
      new InputStreamReader(System.in, "UTF-8"),
      new PrintWriter(System.out, true))
    return componentsFactory
  }
}
