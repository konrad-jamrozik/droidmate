// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

File inputUrls = new File("app_urls.txt")
File pullTargetDir = new File("C:/my/local/repos/chair/droidmate/dev/droidmate/projects/core/artifacts/apks_2015_Feb_12")

assert inputUrls.file

List<String> urls = inputUrls.readLines()

List<String> apkPaths = urls.collect { String url ->
  def (_, packageName) = url.split("=")
  String apkPath = getApkPath(packageName)
  return apkPath
}.findAll { it.length() > 0}

apkPaths.each { String apkPath ->

  File targetApk = getTargetApk(apkPath, pullTargetDir)

  if (targetApk.file)
    println "Apk ${targetApk.name} already exists. Not pulling."
  else
    pullApk(apkPath, targetApk)
}

private String getApkPath(packageName)
{
  def adbShellPmPathCmd = "adb shell pm path $packageName"
  println "Executing sys cmd: $adbShellPmPathCmd"

  Process adbShellPmPath = adbShellPmPathCmd.execute()
  adbShellPmPath.waitFor()

  String apkPath = adbShellPmPath.in.text

  if (!(apkPath?.length() > 0))
  {
    println "Didn't found path to package $packageName. Skipping the apk."
    return ""
  }

  assert apkPath?.startsWith("package:")

  apkPath = apkPath.substring("package:".length()).trim()

  // without the dot, "Git Bash" on Windows behaves incorrectly: it attaches the Bash executable absolute path.
  apkPath = ".$apkPath"

  return apkPath
}

private File getTargetApk(String apkPath, File pullTargetDir)
{
  String apkName = apkPath.split("/").last()
  String normalizedApkName = apkName.replaceAll(/-\d/, "")
  File targetApk = new File(pullTargetDir, normalizedApkName)
  return targetApk
}

private void pullApk(String apkPath, File targetApk)
{
  assert !targetApk.file
  String adbPullCmd = /adb pull "$apkPath" ${targetApk.path}/
  println "Executing sys cmd: $adbPullCmd"

  Process adbPull = adbPullCmd.execute()
  adbPull.waitFor()

  println adbPull.in.text
  println adbPull.err.text
  assert adbPull.exitValue() == 0
  assert targetApk.file

}