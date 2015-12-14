#!/bin/bash          

# Based on: http://stackoverflow.com/questions/13193592/adb-android-getting-the-name-of-the-current-activity
adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'

