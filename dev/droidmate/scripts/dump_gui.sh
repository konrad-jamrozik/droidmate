#!/bin/bash

adb shell uiautomator dump --verbose data/local/tmp/window_dump.xml
if [ $# -eq 0 ]; then
  dumpFilePath="../gui_dumps/window_dump_$(date +%k%M%S).xml"
  screenshotFilePath="../gui_dumps/screenshot_$(date +%k%M%S).png"
else
  dumpFilePath="../gui_dumps/$1.xml"
  screenshotFilePath="../gui_dumps/$1.png"
fi
adb pull data/local/tmp/window_dump.xml $dumpFilePath
echo "Pulled the GUI dump into $dumpFilePath"

adb shell screencap -p data/local/tmp/screenshot.png
adb pull data/local/tmp/screenshot.png $screenshotFilePath
echo "Pulled the screenshot into $screenshotFilePath"
