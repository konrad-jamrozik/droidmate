#!/bin/sh

# This command is here for interactive manual debugging convenience.
# To be run from current dir after IntelliJ's "make".
adb push "./out/production/monitor-apk-scaffolding/monitor-apk-scaffolding.apk" data/data/com.worldwritabledir/app_files/monitor.apk
