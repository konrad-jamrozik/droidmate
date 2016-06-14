// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command

import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.ApkExplorationException
import org.droidmate.android_sdk.ExplorationException
import org.droidmate.android_sdk.IApk
import org.droidmate.command.exploration.Exploration
import org.droidmate.command.exploration.IExploration
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.*
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.ThrowablesCollection
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.IRobustDevice
import org.droidmate.exploration.strategy.ExplorationStrategy
import org.droidmate.exploration.strategy.IExplorationStrategyProvider
import org.droidmate.misc.Failable
import org.droidmate.misc.ITimeProvider
import org.droidmate.misc.TimeProvider
import org.droidmate.storage.IStorage2
import org.droidmate.storage.Storage2
import org.droidmate.tools.*

import java.nio.file.Files
import java.nio.file.Path

@Slf4j
class ExploreCommand extends DroidmateCommand
{

  private final IApksProvider                       apksProvider
  private final IAndroidDeviceDeployer              deviceDeployer
  private final IApkDeployer                        apkDeployer
  private final IExplorationOutputAnalysisPersister explorationOutputAnalysisPersister
  private final IExploration                        exploration
  private final IStorage2                           storage2


  ExploreCommand(
    IApksProvider apksProvider,
    IAndroidDeviceDeployer deviceDeployer,
    IApkDeployer apkDeployer,
    IExplorationOutputAnalysisPersister explorationOutputAnalysisPersister,
    IExploration exploration,
    IStorage2 storage2)
  {
    this.apksProvider = apksProvider
    this.deviceDeployer = deviceDeployer
    this.apkDeployer = apkDeployer
    this.explorationOutputAnalysisPersister = explorationOutputAnalysisPersister
    this.exploration = exploration
    this.storage2 = storage2
  }

  public static ExploreCommand build(Configuration cfg,
                                     IExplorationStrategyProvider strategyProvider = {ExplorationStrategy.build(cfg)},
                                     ITimeProvider timeProvider = new TimeProvider(),
                                     IDeviceTools deviceTools = new DeviceTools(cfg))
  {
    def storage = new Storage(cfg.droidmateOutputDirPath)
    IApksProvider apksProvider = new ApksProvider(deviceTools.aapt)
    IExplorationOutputDataExtractor extractor = new ExplorationOutputDataExtractor(cfg.compareRuns, cfg)
    IExplorationOutputAnalysisPersister analysisPersister = new ExplorationOutputAnalysisPersister(cfg, extractor, storage)

    def storage2 = new Storage2(cfg.droidmateOutputDirPath)
    IExploration exploration = Exploration.build(cfg, timeProvider, strategyProvider)
    return new ExploreCommand(apksProvider, deviceTools.deviceDeployer, deviceTools.apkDeployer, analysisPersister, exploration, storage2)
  }

  @Override
  void execute(Configuration cfg) throws ThrowablesCollection
  {
    cleanOutputDir(cfg.droidmateOutputDirPath)

    List<Apk> apks = this.apksProvider.getApks(cfg.apksDirPath, cfg.apksLimit, cfg.apksNames)
    if (!validateApks(apks, cfg.runOnNotInlined)) return

    List<ExplorationException> explorationExceptions = execute(cfg, apks)
    if (!explorationExceptions.empty)
      throw new ThrowablesCollection(explorationExceptions)
  }

  private boolean validateApks(List<Apk> apks, boolean runOnNotInlined)
  {
    if (apks.size() == 0)
    {
      log.warn("No input apks found. Terminating.")
      return false
    }
    if (apks.any {!it.inlined})
    {
      if (runOnNotInlined)
      {
        log.info("Not inlined input apks have been detected, but DroidMate was instructed to run anyway. Continuing with execution.")
      } else
      {
        log.warn("At least one input apk is not inlined. DroidMate will not be able to monitor any calls to Android SDK methods done by such apps.")
        log.warn("If you want to inline apks, run DroidMate with $Configuration.pn_inline")
        log.warn("If you want to run DroidMate on non-inlined apks, run it with $Configuration.pn_runOnNotInlined")
        log.warn("DroidMate will now abort.")
        return false
      }
    }
    return true
  }

  private void cleanOutputDir(Path path)
  {
    if (!Files.isDirectory(path))
      return

    path.eachFile(FileType.FILES) {Path p ->
      Files.delete(p)
    }

    path.eachFile {Path p -> assert Files.isDirectory(p)}
  }

  public List<ExplorationException> execute(Configuration cfg, List<Apk> apks)
  {
    ExplorationOutput2 out = new ExplorationOutput2()

    List<ExplorationException> explorationExceptions = []
    try
    {
      explorationExceptions += deployExploreSerialize(cfg.deviceIndex, apks, out)
    }
    catch (Throwable deployExploreSerializeThrowable)
    {
      log.error("!!! Caught ${deployExploreSerializeThrowable.class.simpleName} " +
        "in execute(configuration, apks)->deployExploreSerialize(${cfg.deviceIndex}, apks, out). " +
        "This means ${ExplorationException.simpleName}s have been lost, if any! " +
        "Skipping summary output analysis persisting. " +
        "Rethrowing.")
      throw deployExploreSerializeThrowable
    }

    try
    {
      def deprecatedOut = new ExplorationOutput()
      deprecatedOut.addAll(out.collect {ApkExplorationOutput.from(it)})
      // KJA2 (reporting) implement this in ReportCommand
      this.explorationOutputAnalysisPersister.persist(deprecatedOut)
    } catch (Throwable persistingThrowable)
    {
      explorationExceptions << new ExplorationException(persistingThrowable)
    }

    return explorationExceptions
  }

  private List<ExplorationException> deployExploreSerialize(int deviceIndex, List<Apk> apks, ExplorationOutput2 out)
  {
    this.deviceDeployer.withSetupDevice(deviceIndex) {IRobustDevice device ->

      List<ApkExplorationException> allApksExplorationExceptions = []

      boolean encounteredApkExplorationsStoppingException = false

      apks.eachWithIndex {Apk apk, int i ->

        if (!encounteredApkExplorationsStoppingException)
        {
          log.info("Processing ${i + 1} out of ${apks.size()} apks: ${apk.fileName}")

          allApksExplorationExceptions +=
            this.apkDeployer.withDeployedApk(device, apk) {IApk deployedApk ->
              tryExploreOnDeviceAndSerialize(deployedApk, device, out)
            }

          if (allApksExplorationExceptions.any {it.shouldStopFurtherApkExplorations()})
          {
            log.warn("Encountered an exception that stops further apk explorations. Skipping exploring the remaining apks.")
            encounteredApkExplorationsStoppingException = true
          }
        }
      }
      return allApksExplorationExceptions
    }
  }

  private void tryExploreOnDeviceAndSerialize(
    IApk deployedApk, IRobustDevice device, ExplorationOutput2 out) throws DeviceException
  {
    Failable<IApkExplorationOutput2, DeviceException> failableApkOut2 = this.exploration.run(deployedApk, device)

    if (failableApkOut2.result != null)
    {
      failableApkOut2.result.serialize(this.storage2)
      out << failableApkOut2.result
    }

    if (failableApkOut2.exception != null)
      throw failableApkOut2.exception
  }

}
