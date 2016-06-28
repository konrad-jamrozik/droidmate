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
import org.droidmate.apis.ApiMethodSignature

import static java.nio.file.Files.readAllLines

@Slf4j
public class MonitorGeneratorFrontend
{

  public static void main(String[] args)
  {
    try
    {
      MonitorGeneratorResources res = new MonitorGeneratorResources(args)

      generateMonitorSrc(res)

    } catch (Exception e)
    {
      handleException(e)
    }
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

  static handleException = {Exception e ->
    log.error("Exception was thrown and propagated to the frontend.", e)
    System.exit(1)
  }
}