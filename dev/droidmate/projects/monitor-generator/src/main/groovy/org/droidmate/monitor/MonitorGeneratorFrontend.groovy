// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor

import groovy.util.logging.Slf4j
import org.droidmate.apis.ApiMapping
import org.droidmate.apis.ApiMethodSignature

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static java.nio.file.Files.readAllLines

@Slf4j
public class MonitorGeneratorFrontend
{

  public static void main(String[] args)
  {
    try
    {
      MonitorGeneratorResources res = new MonitorGeneratorResources(args)

      if (!computeAndPrintApiListsStats(args, res))
        generateMonitorSrc(res)

    } catch (Exception e)
    {
      handleException(e)
    }
  }

  private static boolean computeAndPrintApiListsStats(String[] args, MonitorGeneratorResources res)
  {
    List<String> jellybeanPublishedApiMapping = readAllLines(res.jellybeanPublishedApiMapping)
    List<String> jellybeanStaticMethods = readAllLines(res.jellybeanStaticMethods)
    List<String> appguardLegacyApis = readAllLines(res.appguardLegacyApis)
    def apiListsStatsArg = "apiListsStats"
    if (args.any {it.startsWith(apiListsStatsArg)})
    {
      def stats = new ApiListsStats(jellybeanPublishedApiMapping, jellybeanStaticMethods, appguardLegacyApis)

      String outFilePath = args.find {it.startsWith(apiListsStatsArg + "=")}
      if (outFilePath == null)
        stats.print()
      else
      {
        Path apiListOutFile = Paths.get(outFilePath - (apiListsStatsArg + "="))
        assert Files.isWritable(apiListOutFile)
        stats.print(apiListOutFile)
      }

      return true

    } else
      return false
  }

  private static void generateMonitorSrc(MonitorGeneratorResources res)
  {
    def monitorGenerator = new MonitorGenerator(
      new RedirectionsGenerator(res.androidApi),
      new MonitorSrcTemplate(res.monitorSrcTemplatePath, res.androidApi)
    )


//    List<ApiMethodSignature> signatures = getLegacyMethodSignatures(res)
    List<ApiMethodSignature> signatures = getMethodSignatures(res)

    String monitorSrc = monitorGenerator.generate(signatures)

    new MonitorSrcFile(res.monitorSrcOutPath, monitorSrc)
  }

  public static List<ApiMethodSignature> getMethodSignatures(MonitorGeneratorResources res)
  {
    List<ApiMethodSignature> signatures = readAllLines(res.appguardApis)
      .findAll {
      it.size() > 0 && !(it.startsWith("#")) && !(it.startsWith(" ")) &&
        !((res.androidApi == AndroidAPI.API_19) && (it.startsWith("!API19"))) &&
        !((res.androidApi == AndroidAPI.API_23) && (it.startsWith("!API23")))
    }.collect {
      it.startsWith("!API") ?
        ApiMethodSignature.fromDescriptor(it["!APIXX ".size()..-1]) :
        ApiMethodSignature.fromDescriptor(it)
    }


    return signatures
  }

  @Deprecated
  public static List<ApiMethodSignature> getLegacyMethodSignatures(MonitorGeneratorResources res)
  {
    // Legacy code, left here as reference, in case I will ever have to run DroidMate again with the old APIs. As of 9 Oct 2015
    // the BoxMate ICSE 2015 submission uses the old APIs, with filtering on the host side done by
    // org.droidmate.exploration.output.ExplorationOutputDataExtractor.filterApiLogs(java.util.List<java.util.List<org.droidmate.logcat.IApiLogcatMessage>>, java.lang.String, boolean)
    ApiMapping mapping = new ApiMapping(
      readAllLines(res.jellybeanPublishedApiMapping),
      readAllLines(res.jellybeanStaticMethods),
      readAllLines(res.appguardLegacyApis)
    )
    List<ApiMethodSignature> signatures = mapping.apis
    return signatures
  }

  static handleException = {Exception e ->
    log.error("Exception was thrown and propagated to the frontend.", e)
    System.exit(1)
  }
}