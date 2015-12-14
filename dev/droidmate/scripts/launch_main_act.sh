#!/bin/bash

# Based on: DroidMate source code (AdbWrapper.launchMainActivtiy()) and Android API docs:
# http://developer.android.com/reference/android/content/Intent.html#ACTION_MAIN
# http://developer.android.com/reference/android/content/Intent.html#CATEGORY_LAUNCHER
# http://developer.android.com/tools/help/adb.html#IntentSpec
adb shell am start -W -S -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n com.google.android.apps.youtube.app.honeycomb.Shell/.HomeActivity

# adb shell am start -W -S -a android.intent.action.MAIN -c android.intent.category.LAUNCHER com.example.android.skeletonapp