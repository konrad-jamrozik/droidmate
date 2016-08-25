// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

package org.droidmate.android_sdk

import groovy.transform.Memoized
import groovy.util.logging.Slf4j
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.LaunchableActivityNameProblemException
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.misc.DroidmateException
import org.droidmate.misc.ISysCmdExecutor
import org.droidmate.misc.SysCmdExecutorException

import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Matcher

import static org.droidmate.android_sdk.Utils.getAndValidateFirstMatch

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
      if (cfg.androidApi == Configuration.api19)
        aaptCommand = cfg.aaptCommandApi19
      else if (cfg.androidApi == Configuration.api23)
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
