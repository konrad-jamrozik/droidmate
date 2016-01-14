// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

import java.util.regex.Matcher

File inputApkDir = new File("c:/my/SEChair/droidmate/repos/droidmate/dev/droidmate/projects/core/artifacts/apks_ccs2014")
assert inputApkDir.directory

List<File> apks = inputApkDir.listFiles({File dir, String name -> name.endsWith(".apk")} as FilenameFilter)
assert apks.size() > 0

int apksCount = apks.size()
int launchableActivitiesFoundCount = 0

apks.each {File apk ->
  String badgingDump = executeAaptDumpBadging(apk)
  Matcher matcher = badgingDump =~ /(?:.*)launchable-activity: name='(\S*)'.*/

  if (matcher.size() == 0)
    println "${apk.name}: No launchable-activity found"
  else
  {
    launchableActivitiesFoundCount++
    matcher.each {def match -> println "${apk.name}: ${match[1]}" }
  }
}

println "Out of $apksCount, $launchableActivitiesFoundCount have " +
  "launchable-activity and ${apksCount-launchableActivitiesFoundCount} do not."

private String executeAaptDumpBadging(File apk)
{
  assert apk.file
  String aaptDumpBadgingCmd = /aapt dump badging "${apk.path}"/

  StringWriter stdout = new StringWriter()
  StringWriter stderr = new StringWriter()
  Process aaptDumpBadging = aaptDumpBadgingCmd.execute()
  aaptDumpBadging.waitForProcessOutput(stdout, stderr)
  assert aaptDumpBadging.exitValue() == 0
  return stdout

}
