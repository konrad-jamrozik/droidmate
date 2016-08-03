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
