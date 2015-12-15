// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.deprecated_still_used

import groovy.util.logging.Slf4j
import org.droidmate.configuration.Configuration

@Deprecated
@Slf4j
class ExplorationOutputAnalysisPersister implements IExplorationOutputAnalysisPersister
{

  Configuration                   config
  IExplorationOutputDataExtractor dataExtractor
  IStorage                        storage

  ExplorationOutputAnalysisPersister(Configuration config, IExplorationOutputDataExtractor dataExtractor, IStorage storage)
  {
    this.config = config
    this.dataExtractor = dataExtractor
    this.storage = storage
  }

  @Deprecated
  @Override
  void persist(ExplorationOutput explorationOutput)
  {
    String packageName = getExplorationOutputPackageNameWithDash(explorationOutput)
    if (packageName == "")
      log.info("Persisting data from exploration output.")
    else
      log.info("Persisting data from exploration output for ${packageName[1..-1]}.")

    if (explorationOutput.size() == 0)
    {
      log.warn("Exploration output is empty! Aborting data extraction.")
      return
    }

    if (config.deployRawApks)
    {
      log.warn("DroidMate was run in 'raw apks' mode, thus no data for extraction will be available. Aborting data extraction.")
      return
    }

    if (config.extractSummaries)
      persistSummary(explorationOutput)

    if (config.extractSaturationCharts)
      persistSaturationCharts(explorationOutput)

    if (config.extractAdditionalData)
      persistAdditionalData(explorationOutput)

    storage.deleteEmpty()
  }


  private void persistSummary(ExplorationOutput explorationOutput)
  {
    String packageName = getExplorationOutputPackageNameWithDash(explorationOutput)


    if (packageName == "")
      log.info("Persisting summary.")
    else
      log.info("Persisting summary for ${packageName[1..-1]}.")

    dataExtractor.summary(explorationOutput, storage.getWriter("summary${packageName}.txt"))

  }

  private void persistSaturationCharts(ExplorationOutput explorationOutput)
  {
    String packageName = getExplorationOutputPackageNameWithDash(explorationOutput)
    if (packageName == "")
      log.info("Persisting saturation charts.")
    else
      log.info("Persisting saturation charts for ${packageName[1..-1]}.")

    int timeTicks = (int) (config.saturationChartsHours * 360)

    if (config.splitCharts)
    {
      explorationOutput.each {
        dataExtractor.pgfplotsChartInputData(
          [it] as ExplorationOutput, storage.getWriter("saturation_chart-${config.saturationChartsHours}h-${it.appPackageName}.txt"),
          timeTickSize: 10000, timeTicks: timeTicks)

        dataExtractor.pgfplotsChartInputData(
          [it] as ExplorationOutput, storage.getWriter("saturation_chart-${config.saturationChartsHours}h-perEvent-${it.appPackageName}.txt"),
          timeTickSize: 10000, timeTicks: timeTicks, perEvent: true)
      }
    } else
    {
      dataExtractor.pgfplotsChartInputData(
        explorationOutput, storage.getWriter("saturation_chart-${config.saturationChartsHours}h${packageName}.txt"),
        timeTickSize: 10000, timeTicks: timeTicks)

      dataExtractor.pgfplotsChartInputData(
        explorationOutput, storage.getWriter("saturation_chart-${config.saturationChartsHours}h-perEvent${packageName}.txt"),
        timeTickSize: 10000, timeTicks: timeTicks, perEvent: true)
    }
  }

  private void persistAdditionalData(ExplorationOutput explorationOutput)
  {
    String packageName = getExplorationOutputPackageNameWithDash(explorationOutput)

    log.info("Extracting actions, stack traces, api manifests, possibly redundant API calls.")

    dataExtractor.actions(explorationOutput, storage.getWriter("actions${packageName}.txt"))

    dataExtractor.stackTraces(explorationOutput, storage.getWriter("stack_traces${packageName}.txt"))

    dataExtractor.apiManifest(explorationOutput, storage.getWriter("api_manifest${packageName}.txt"))

    dataExtractor.possiblyRedundantApiCalls(explorationOutput, storage.getWriter("possibly_redundant_api_calls${packageName}.txt"));
  }

  private static String getExplorationOutputPackageNameWithDash(ExplorationOutput explorationOutput)
  {
    String packageName = ""
    // If the package name of all the apks in the exploration output is the same,
    // include the package name in the summary file name.
    if (explorationOutput*.appPackageName.unique().size() == 1)
      packageName = "-" + explorationOutput[0].appPackageName
    return packageName
  }
}
