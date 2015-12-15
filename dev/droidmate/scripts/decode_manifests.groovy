// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

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

