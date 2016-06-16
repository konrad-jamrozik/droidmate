// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor

import com.konradjamrozik.ResourcePath
import org.droidmate.apis.ApiMapping
import org.droidmate.apis.ApiMethodSignature
import org.droidmate.apis.ExcludedApis
import org.droidmate.common.BuildConstants

import java.nio.file.Files
import java.nio.file.Path

class ApiListsStats
{

  private List<ApiMethodSignature> pscoutApis
  private List<ApiMethodSignature> appGuardApis
  private List<ApiMethodSignature> pscoutOnlyApis
  private List<ApiMethodSignature> appGuardOnlyApis
  private List<ApiMethodSignature> appGuardAndPscoutApis

  /**
   * This method is an ugly hack done 1 day before CCS 2015 deadline on 17 May 2015, to get the necessary data out of it.
   */
  ApiListsStats(
    List<String> jellybeanPublishedApiMapping,
    List<String> jellybeanStaticMethods,
    List<String> appguardLegacyApis)

  {
    println "Computing API lists stats."

    ApiMapping pscoutMapping = new ApiMapping(jellybeanPublishedApiMapping, jellybeanStaticMethods, appguardLegacyApis)
    pscoutApis = pscoutMapping.apis.findAll {!(new ExcludedApis().contains(it.methodName))}

    List<String> appGuardApiMappingLines = new ResourcePath(BuildConstants.appguard_apis_txt).path.readLines()
    appGuardApis = ApiMapping.parseAppguardLegacyApis(appGuardApiMappingLines)


    def pscoutApisShortSignatures = pscoutApis.collect {it.shortSignature}
    def appGuardApisShortSignatures = appGuardApis.collect {it.shortSignature}

    pscoutOnlyApis = pscoutApis.findAll {
      !(it.shortSignature in appGuardApisShortSignatures)
    }
    appGuardOnlyApis = appGuardApis.findAll {
      !(it.shortSignature in pscoutApisShortSignatures)
    }
    appGuardAndPscoutApis = appGuardApis.findAll {
      (it.shortSignature in pscoutApisShortSignatures)
    }
  }

  public void print(Path outPath = null)
  {
    if (outPath != null)
    {
      assert outPath.toString().size() >= 3
      assert Files.isWritable(outPath)
    }

    println "Writing out the stats to ${outPath ?: "stdout"}"

    PrintWriter pwr = outPath != null ? outPath.newPrintWriter() : System.out.newPrintWriter()
    pwr.with {
      printApiListsCountsAndDiffs(it, pscoutApis, appGuardApis, pscoutOnlyApis, appGuardOnlyApis, appGuardAndPscoutApis)
      printApiLists(it, pscoutApis, appGuardApis)
      pwr.println("END OF FILE")
    }
    pwr.close()

    println "Done."
  }

  private static void printApiListsCountsAndDiffs(PrintWriter pwr,
                                                  List<ApiMethodSignature> pscoutApis,
                                                  List<ApiMethodSignature> appGuardApis,
                                                  List<ApiMethodSignature> pscoutOnlyApis,
                                                  List<ApiMethodSignature> appGuardOnlyApis,
                                                  List<ApiMethodSignature> appGuardAndPscoutApis)
  {
    pwr.println "PScout APIs count: " + pscoutApis.size()
    pwr.println "AppGuard APIs count: " + appGuardApis.size()
    pwr.println "Count of PScout APIs that are not in AppGuard APIs: " + pscoutOnlyApis.size()
    pwr.println "Count of AppGuard APIs that are not in PScout APIs: " + appGuardOnlyApis.size()
    pwr.println "Count of AppGuard AND PScout APIs: " + appGuardAndPscoutApis.size()
    pwr.println "========================================"
    pwr.println ""
    pwr.println ""
    pwr.println ""
    pwr.println "APIs present only in PScout:"
    pwr.println "----------------------------------------"
    pscoutOnlyApis.each {
      pwr.println it.shortSignature
    }
    pwr.println "========================================"
    pwr.println ""
    pwr.println ""
    pwr.println ""
    pwr.println "APIs present only in AppGuard:"
    pwr.println "----------------------------------------"
    appGuardOnlyApis.each {
      pwr.println it.shortSignature
    }
    pwr.println "========================================"
    pwr.println ""
    pwr.println ""
    pwr.println ""
    pwr.println "APIs present in AppGuard AND PScout:"
    pwr.println "----------------------------------------"
    appGuardAndPscoutApis.each {
      pwr.println it.shortSignature
    }
    pwr.println "========================================"
    pwr.println ""
    pwr.println ""
    pwr.println ""
  }

  private static void printApiLists(PrintWriter pwr, List<ApiMethodSignature> pscoutApis, List<ApiMethodSignature> appGuardApis)
  {
    pwr.println "PScout APIs:"
    pwr.println "----------------------------------------"
    pscoutApis.each {
      pwr.println it.shortSignature
    }
    pwr.println "========================================"
    pwr.println ""
    pwr.println ""
    pwr.println ""
    pwr.println "AppGuard APIs:"
    pwr.println "----------------------------------------"
    appGuardApis.each {
      pwr.println it.shortSignature
    }
    pwr.println "========================================"
    pwr.println ""
    pwr.println ""
    pwr.println ""
  }

}
