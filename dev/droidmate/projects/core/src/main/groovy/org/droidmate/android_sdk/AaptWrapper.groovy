// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.android_sdk

import groovy.transform.Memoized
import groovy.util.logging.Slf4j
import org.droidmate.common.ISysCmdExecutor
import org.droidmate.common.SysCmdExecutorException
import org.droidmate.configuration.Configuration
import org.droidmate.common.DroidmateException
import org.droidmate.exceptions.NoLaunchableActivityNameException

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


  private static String tryGetLaunchableActivityNameFromBadgingDump(String aaptBadgingDump) throws DroidmateException
  {
    assert aaptBadgingDump?.length() > 0

    Matcher matcher = aaptBadgingDump =~ /(?:.*)launchable-activity: name='(\S*)'.*/

    if (matcher.size() == 0)
      throw new NoLaunchableActivityNameException()
    else if (matcher.size() > 1)
      throw new DroidmateException("More than one launchable activity found! While some apks have more than one launchable activities, DroidMate doesn't know how to handle such situations.")
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

    String aaptBadgingDump = aaptDumpBadging(apk)
    String launchableActivityName
    try
    {
      launchableActivityName = tryGetLaunchableActivityNameFromBadgingDump(aaptBadgingDump)
    } catch (DroidmateException e)
    {
      log.debug("! Caught ${e.class.simpleName} while trying to obtain launchable activity name. Returning null instead. The exception: $e")
      return null
    }

    assert launchableActivityName?.length() > 0;
    return launchableActivityName
  }

  @Override
  String getLaunchableActivityComponentName(Path apk) throws DroidmateException
  {
    assert Files.isRegularFile(apk)

    String aaptBadgingDump = aaptDumpBadging(apk)
    String launchableActivityComponentName
    try
    {
      launchableActivityComponentName = tryGetLaunchableActivityComponentNameFromBadgingDump(aaptBadgingDump)
    } catch (DroidmateException e)
    {
      log.debug("! Caught ${e.class.simpleName} while trying to obtain launchable activity component name. Returning null instead. The exception: $e")
      return null
    }

    assert launchableActivityComponentName?.length() > 0
    return launchableActivityComponentName
  }

  @Override
  List<String> getMetadata(Path apk)
  {
    [getPackageName(apk),
     getLaunchableActivityName(apk),
     getLaunchableActivityComponentName(apk)]
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
      outputStreams = sysCmdExecutor.execute(
        commandDescription, cfg.aaptCommand, "dump badging", instrumentedApk.toAbsolutePath().toString())

    } catch (SysCmdExecutorException e)
    {
      throw new DroidmateException(e)
    }

    String aaptBadgingDump = outputStreams[0]

    assert aaptBadgingDump?.length() > 0
    return aaptBadgingDump
  }
}
