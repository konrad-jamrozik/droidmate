#!/bin/bash
/dev/null << About

Author: Konrad Jamrozik
Last revised on: August 14, 2013

Set of functions to make Soot work with .apks

To sootify (instrument) an apk, sign it with debug key and zipalign it in one go, run instrument_jar with path to apk given as a parameter. First however, please make sure all the assumptions given in fuctions comments are fulfilled.

About

# Warning: on 25 Mar 2014, this script was moved from "../" to ".". Probably needs fixing!

# This function assumes that:
# - in current dir is a sootclasses-trunk.jar file build with "ant clean fulljar" from soot's repo
#   OR you can just use soot.jar nightly build from:
#   http://vandyk.st.informatik.tu-darmstadt.de/abc/
#   Use then:
#   java -jar soot.jar -Xmx1g (...)
#
# - [if building soot yourself] there is libs/ dir copied from soot's repo
#   AND having google guava .jar, e.g. guava-14.0.1.jar.
#
# - android jars have been checked out to current dir from https://github.com/Sable/android-platforms
#
# - the apk under instrumentation is expected to be run on A(V)D with Android API 17
#
sootcmd="java -cp \"sootclasses-trunk.jar;libs/*\" -Xmx1g soot.Main -w -allow-phantom-refs -force-android-jar "android-platforms/android-17/android-17-api.jar" -src-prec apk -output-format dex"
function sootify_jar() {
  eval "$sootcmd" -process-dir "$1"
}

# This function assumes that:
# - jarsigner is in your PATH
#   For help, see http://stackoverflow.com/questions/12135699/where-is-jarsigner
#
# - keystore_path points to your debug.keystore
#   For hint where it is on your machine, see:
#   http://developer.android.com/tools/publishing/app-signing.html#debugexpiry
#
keystore_path="C:/my/SEChair/droidmate/repos/droidmate/dev/droidmate/projects/core/./src/main/resources/debug.keystore"
function sign_jar() {
  jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore "$keystore_path" -storepass android -keypass android "$1" androiddebugkey
}

# This function assumes that:
# - zipalign is in your PATH
#   Zipalign can be found in Android SDK/tools
#   More on what it does:
#   http://developer.android.com/tools/help/zipalign.html
#
function zipalign_jar() {

  # Reference: http://stackoverflow.com/a/965072/986533
  filename=$(basename "$1")
  extension="${filename##*.}"
  filename="${filename%.*}"

  zipalign -v -f 4 "$1" "$filename-aligned.$extension"
}

function instrument_jar() {
  sootify_jar "$1"
  sign_jar "sootOutput/$1"
  zipalign_jar "sootOutput/$1"
}
