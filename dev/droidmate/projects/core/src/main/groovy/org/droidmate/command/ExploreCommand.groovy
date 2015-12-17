// Copyright (c) 2012-2015 Saarland University
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
import org.droidmate.android_sdk.ApkExplorationExceptionsCollection
import org.droidmate.android_sdk.IApk
import org.droidmate.command.exploration.Exploration
import org.droidmate.command.exploration.IExploration
import org.droidmate.common.DroidmateException
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.*
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.ThrowablesCollection
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.device.IDeviceWithReadableLogs
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

  public
  static ExploreCommand build(ITimeProvider timeProvider = new TimeProvider(), Configuration cfg, IDeviceTools deviceTools = new DeviceTools(cfg))
  {
    def storage = new Storage(cfg.droidmateOutputDirPath)
    IApksProvider apksProvider = new ApksProvider(deviceTools.aapt)
    IExplorationOutputDataExtractor extractor = new ExplorationOutputDataExtractor(cfg.compareRuns, cfg)
    IExplorationOutputAnalysisPersister analysisPersister = new ExplorationOutputAnalysisPersister(cfg, extractor, storage)

    def storage2 = new Storage2(cfg.droidmateOutputDirPath)
    IExploration exploration = Exploration.build(cfg, timeProvider)
    return new ExploreCommand(apksProvider, deviceTools.deviceDeployer, deviceTools.apkDeployer, analysisPersister, exploration, storage2)
  }

  @Override
  void execute(Configuration cfg) throws DroidmateException
  {
    cleanOutputDir(cfg.droidmateOutputDirPath)

    List<Apk> apks = apksProvider.getApks(cfg.apksDirPath, cfg.apksLimit, cfg.apksNames)
    if (apks.size() == 0)
    {
      log.warn("No input apks found. Terminating.")
      return
    }

    tryExecute(cfg, apks)
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

  public void tryExecute(Configuration cfg, List<Apk> apks) throws DroidmateException
  {
    ExplorationOutput2 out = new ExplorationOutput2()

    Throwable savedTryThrowable = null
    try
    {
      tryDeployExploreSerialize(cfg.deviceIndex, apks, out)

      def deprecatedOut = new ExplorationOutput()
      deprecatedOut.addAll(out.collect {ApkExplorationOutput.from(it)})
      explorationOutputAnalysisPersister.persist(deprecatedOut)
    } catch (Throwable tryThrowable)
    {
      log.debug("! Caught ${tryThrowable.class.simpleName} in withDeployedApk.computation(apk). Rethrowing.")
      savedTryThrowable = tryThrowable
      throw savedTryThrowable

    } finally
    {
      log.debug("Finalizing: ${ExploreCommand.class.simpleName}.tryExecute finally {}")
      ApkExplorationExceptionsCollection exceptionsCollection = collectApkExplorationExceptionsIfAny(out)
      if (exceptionsCollection != null)
      {
        if (savedTryThrowable != null)
        {
          log.debug("! Collected ${exceptionsCollection.class.simpleName} in collectApkExplorationExceptionsIfAny(out). " +
            "Rethrowing them inside ${ThrowablesCollection.simpleName}, together with a savedTryThrowable.")
          throw new ThrowablesCollection([exceptionsCollection, savedTryThrowable])
        } else
        {
          log.debug("! Collected ${exceptionsCollection.class.simpleName} in collectApkExplorationExceptionsIfAny(out). Throwing.")
          throw exceptionsCollection
        }
      }
      log.debug("Finalizing DONE: ${ExploreCommand.class.simpleName}.tryExecute finally {}")
    }
  }

  ApkExplorationExceptionsCollection collectApkExplorationExceptionsIfAny(ExplorationOutput2 out)
  {
    List<ApkExplorationException> exceptions = []

    out.each {
      if (!(it.noException))
      {
        exceptions.add(new ApkExplorationException(it.apk, it.exception))
      }
    }

    if (!(exceptions.empty))
      return new ApkExplorationExceptionsCollection(exceptions)
    else
      return null

  }

  private void tryDeployExploreSerialize(int deviceIndex, List<Apk> apks, ExplorationOutput2 out) throws DeviceException
  {
    deviceDeployer.withSetupDevice(deviceIndex) {IDeviceWithReadableLogs device ->

      apks.eachWithIndex {Apk apk, int i ->

        log.info("Processing ${i + 1} out of ${apks.size()} apks: ${apk.fileName}")

        apkDeployer.withDeployedApk(device, apk) {IApk deployedApk ->

          tryExploreOnDeviceAndSerialize(deployedApk, device, out)
        }
      }
    }
  }

  private void tryExploreOnDeviceAndSerialize(
    IApk deployedApk, IDeviceWithReadableLogs device, ExplorationOutput2 out) throws DeviceException
  {
    IApkExplorationOutput2 apkOut2 = tryExploreOnDevice(deployedApk, device)
    apkOut2.serialize(storage2)
    out << apkOut2
  }

  private IApkExplorationOutput2 tryExploreOnDevice(
    IApk deployedApk, IDeviceWithReadableLogs device) throws DeviceException
  {
    return exploration.tryRun(deployedApk, device)
  }
}
