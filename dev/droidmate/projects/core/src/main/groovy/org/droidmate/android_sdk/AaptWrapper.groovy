// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.android_sdk

import groovy.transform.Memoized
import groovy.util.logging.Slf4j
import org.droidmate.common.DroidmateException
import org.droidmate.common.ISysCmdExecutor
import org.droidmate.common.SysCmdExecutorException
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.LaunchableActivityNameProblemException
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError

import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Matcher

import static Utils.getAndValidateFirstMatch

/**
 * Wrapper for the {@code aapt} tool from Android SDK.
 */
@Slf4j
public class AaptWrapper implements IAaptWrapper
{

  private final Configuration   cfg
  private       ISysCmdExecutor sysCmdExecutor


  AaptWrapper(Configuration cfg, ISysCmdExecutor sysCmdExecutor)
  {
    this.cfg = cfg
    this.sysCmdExecutor = sysCmdExecutor
  }

  static String tryGetLaunchableActivityComponentNameFromBadgingDump(String aaptBadgingDump) throws DroidmateException
  {
    return tryGetPackageNameFromBadgingDump(aaptBadgingDump) + "/" + tryGetLaunchableActivityNameFromBadgingDump(aaptBadgingDump)
  }

  private static String tryGetPackageNameFromBadgingDump(String aaptBadgingDump) throws DroidmateException
  {
    assert aaptBadgingDump?.length() > 0

    Matcher matcher = aaptBadgingDump =~ /(?:.*)package: name='(\S*)'.*/

    if (matcher.size() == 0)
      throw new DroidmateException("No package name found in 'aapt dump badging'")
    else if (matcher.size() > 1)
      throw new DroidmateException("More than one package name found in 'aapt dump badging'")
    else
    {
      String packageName = getAndValidateFirstMatch(matcher)
      return packageName
    }
  }


  private static String tryGetLaunchableActivityNameFromBadgingDump(String aaptBadgingDump) throws LaunchableActivityNameProblemException
  {
    assert aaptBadgingDump?.length() > 0

    Matcher matcher = aaptBadgingDump =~ /(?:.*)launchable-activity: name='(\S*)'.*/

    if (matcher.size() == 0)
      throw new LaunchableActivityNameProblemException("No launchable activity found.")
    else if (matcher.size() > 1)
      throw new LaunchableActivityNameProblemException("More than one launchable activity found.", /* isFatal */ true)
    else
    {
      String launchableActivityName = getAndValidateFirstMatch(matcher)
      return launchableActivityName
    }
  }

  @Override
  String getPackageName(Path apk) throws DroidmateException
  {
    assert Files.isRegularFile(apk)

    String aaptBadgingDump = aaptDumpBadging(apk)
    String packageName = tryGetPackageNameFromBadgingDump(aaptBadgingDump)

    assert packageName?.length() > 0;
    return packageName;
  }

  @Override
  String getLaunchableActivityName(Path apk) throws DroidmateException
  {
    assert Files.isRegularFile(apk)

    String launchableActivityName = tryGetLaunchableActivityNameFromBadgingDump(aaptDumpBadging(apk))

    assert launchableActivityName?.length() > 0
    return launchableActivityName
  }

  @Override
  String getLaunchableActivityComponentName(Path apk) throws DroidmateException
  {
    assert Files.isRegularFile(apk)

    String launchableActivityComponentName = tryGetLaunchableActivityComponentNameFromBadgingDump(aaptDumpBadging(apk))

    assert launchableActivityComponentName?.length() > 0
    return launchableActivityComponentName
  }

  @Override
  String getApplicationLabel(Path apk) throws DroidmateException
  {
    assert Files.isRegularFile(apk)

    String aaptBadgingDump = aaptDumpBadging(apk)
    return tryGetApplicationLabelFromBadgingDump(aaptBadgingDump)
  }

  private static String tryGetApplicationLabelFromBadgingDump(String aaptBadgingDump) throws DroidmateException
  {
    assert aaptBadgingDump?.length() > 0

    Matcher matcher = aaptBadgingDump =~ /(?:.*)application-label:'(.*)'.*/

    if (matcher.size() == 0)
      throw new DroidmateException("No application label found in 'aapt dump badging'")
    else if (matcher.size() > 1)
      throw new DroidmateException("More than one application label found in 'aapt dump badging'")
    else
    {
      String applicationLabel = getAndValidateFirstMatch(matcher)
      return applicationLabel
    }
  }

  @Override
  List<String> getMetadata(Path apk)
  {
    List<String> activity
    try
    {
      activity = [getLaunchableActivityName(apk), getLaunchableActivityComponentName(apk)]
    } catch (LaunchableActivityNameProblemException e)
    {
      if (e.isFatal)
      {
        throw e
      } else
      {
        log.trace("While getting metadata for ${apk.toString()}, got an: $e Substituting null for the launchable activity (component) name.")
        activity = [null, null]
      }


    }

    return [getPackageName(apk)] + activity + getApplicationLabel(apk)
  }

  @Memoized
  String aaptDumpBadging(Path instrumentedApk)
  {

    String commandDescription = String.format(
      "Executing aapt (Android Asset Packaging Tool) to extract package name of apk %s.",
      instrumentedApk.toAbsolutePath().toString())

    String[] outputStreams

    try
    {
      String aaptCommand
      if (cfg.androidApi == "api19")
        aaptCommand = cfg.aaptCommandApi19
      else if (cfg.androidApi == "api23")
        aaptCommand = cfg.aaptCommandApi23
      else 
        throw new UnexpectedIfElseFallthroughError()
        
      outputStreams = sysCmdExecutor.execute(
        commandDescription, aaptCommand, "dump badging", instrumentedApk.toAbsolutePath().toString())

    } catch (SysCmdExecutorException e)
    {
      throw new DroidmateException(e)
    }

    String aaptBadgingDump = outputStreams[0]

    assert aaptBadgingDump?.length() > 0
    return aaptBadgingDump
  }
}
