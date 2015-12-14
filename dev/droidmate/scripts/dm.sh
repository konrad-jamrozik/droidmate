#!/bin/bash
/dev/null << About

Author: Konrad Jamrozik

This file contains a set of DroidMate bash scripts for conducting various small tasks.

About

pull_target_dir="c:/my/local/repos/chair/droidmate/dev/droidmate/projects/core/artifacts/apks_ccs2014"
# Parameter $1: the path to the file to be pulled.
#
# How to obtain the path to the file to be pulled?
#
#   a) Use ./mass_pull_apks.groovy
#
#   b) Using IntelliJ: open "Android", then in the "Devices" select an app (it has to be started on the device),
#   select the "System information" icon on the left and select "Package information". Look for "resource path". Copy-paste.
#
#   c) Just with adb:
#     1) adb shell pm path <package name>
#        Reference: http://stackoverflow.com/a/18003462/986533
#     2) Copy-paste the path from the obtained string.
#
# Reference: http://stackoverflow.com/questions/4032960/how-do-i-get-an-apk-file-from-an-android-device
#
function pull_apk() {
${core_test_cmd}ut the dot before $1, "Git Bash" on Windows behaves incorrectly: it attaches the Bash executable absolute path.
  adb pull ".$1" ${pull_target_dir}
}

# Assumptions:
# - adb is recognizable as a global command pointing to android debug bridge from Android SDK
#
# Function args:
# $1: path to the .apk file
function install_apk() {
  adb install -r "$1"
}

tsa_filepath="C:/my/local/repos/chair/droidmate/dev/droidmate/projects/core/artifacts/apks_tsa/TestSubjectApp.apk"
# Used to install test subject apk on the plugged Android Device
function install_tsa()
{
  install_apk $tsa_filepath
}

# Assumptions:
# - aapt is recognizable as a global command pointing to aapt from Android SDK
# - adb is recognizable as a global command pointing to android debug bridge from Android SDK
#
# Function args:
# $1: path to the apk file
function uninstall_apk() {
  package_name=$(aapt dump badging $1 | grep "package:" | cut -d ' ' -f2 | cut -d "'" -f2)
  adb uninstall "$package_name"
}


# This function assumes that:
# - jarsigner is in your PATH
#   For help, see http://stackoverflow.com/questions/12135699/where-is-jarsigner
#
# - keystore_path points to your debug.keystore
#   For hint where it is on your machine, see:
#   http://developer.android.com/tools/publishing/app-signing.html#debugexpiry
#
keystore_path="C:/my/local/repos/chair/droidmate/dev/droidmate/projects/core/src/main/resources/debug.keystore"
function sign_apk() {
  jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore "$keystore_path" -storepass android -keypass android "$1" androiddebugkey
}


# This function is used for checking if the functions in this script can be called directly from cmd line.
# Example call: ./dm.sh echo_f foo
function f_echo() {
  echo "Called echo function! Param 1: $1"
}

apktool_path="C:/my/local/repos/chair/droidmate/dev/droidmate/projects/core/src/main/resources/apktool.jar"
function unpack_apk()
{
  apk_path=$1

  apk_path_noext=${apk_path%.*} # Parameter substitution
  apk_absolute_path=$(pwd)"/"${apk_path}
  cmd="java -jar "${apktool_path}" decode --no-src --force "${apk_absolute_path}" --output apktool_out_${apk_path_noext}"
  eval ${cmd}
}

# Enables calling bash functions from command line.
# Example usage: <this script name> echo_f()
# Reference: http://stackoverflow.com/a/16159057/986533
$@