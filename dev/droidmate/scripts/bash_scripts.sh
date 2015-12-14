#!/bin/bash          

# Warning: on 25 Mar 2014, this script was moved from ".." to ".". Probably needs fixing!

function jars_to_manifest() {

  classpath=$(echo lib/*.jar | { sed 's/ /\n  /g'; })
  echo "Class-Path: $classpath"
}

function core_jar_jars_to_manifest() {
  curr_dir=$(pwd)
  cd "out/artifacts/core_jar"
  echo "$(jars_to_manifest)"
  cd $curr_dir
}

function run {
	gradle :projects:core:run -PjvmArgs="-ea -DLOGS_DIRECTORY=./logs" -Pargs="-command=record -guiExplorationClicks=1 -forceInstrumentation -displayConfig -resourcesDirPath=./src/main/resources -artifactsDirPath=./artifacts"
}

function dump_gui {
  adb shell uiautomator dump # should dump to: /storage/emulated/legacy/window_dump.xml
  adb pull storage/emulated/legacy/window_dump.xml ./window_dump.xml
}


function vis_dump_gui {
  echo "After the GUI starts, please select the device to the left and click on the appropriate button above it to dump the GUI."
  # noob explanation on how to run .bat files from Windows' bash script.
  # http://en.wikipedia.org/wiki/ComSpec
  # http://stackoverflow.com/questions/3395374/run-invoke-windows-batch-script-from-sh-or-bash-script
  $COMSPEC /c monitor.bat
}