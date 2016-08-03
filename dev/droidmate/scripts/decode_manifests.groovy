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

File inputDir = new File("C:/my/local/repos/chair/droidmate/dev/droidmate/projects/core/artifacts/apks")
assert inputDir.directory

File permissions = new File("scripts/permissions.txt")
permissions.delete()

File apktool = new File(inputDir, "apktool.jar")
assert apktool.file

inputDir.listFiles().findAll {it.name.endsWith(".apk")}.each { File apk ->

  File outDir = new File(inputDir, apk.name.replace("-inlined",""))
  println "Decoding ${apk.name}"
  println "java -jar $apktool d $apk --no-src --force --output $outDir"

  Process proc = "java -jar $apktool d $apk --no-src --force --output $outDir".execute()
  proc.waitFor()
  println proc.in.text
  println proc.err.text

  File manifest = new File(outDir, "androidManifest.xml")
  assert manifest.file

  permissions.append( apk.name +"\n" )
  manifest.readLines()
    .findAll { it.contains("uses-permission") }
    .each { String permission -> permissions.append ( permission + "\n" )}
}

