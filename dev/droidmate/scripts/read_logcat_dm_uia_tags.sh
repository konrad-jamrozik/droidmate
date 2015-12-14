#!/bin/bash

adb logcat -c
adb logcat -b main -v time *:s UiaTestCase Monitor Instrumentation Monitored_API_method_call

