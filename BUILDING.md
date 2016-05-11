  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 10 May 2016

# Building, testing and running DroidMate #

DroidMate is built with [Gradle](https://docs.gradle.org/current/userguide/userguide.html). Each DroidMate commit is built with Travis CI, a continuous integration server. You can use it as a reference to troubleshoot your local build setup. Travis CI configuration file: `repo/.travis.yml`. To view the full, detailed log of the build done on the CI server, click on this label => [![Build Status](https://travis-ci.org/konrad-jamrozik/droidmate.svg?branch=master)](https://travis-ci.org/konrad-jamrozik/droidmate).

## Local build requirements ##

To be able to build DroidMate on your local machine, you will need JDK, Android SDK with Android 4 and 6, Apache Ant and some 
environment variables set to appropriate values.

To configure your local setup, do the following:

* Install Java Development Kit (JDK) 8. Set `JAVA_HOME` environment variable to point to its location.
* Install Android SDK. Set `ANDROID_HOME` environment variable to point to its location.
* Run SDK Manager of Android SDK with admin rights. Select and install the following packages:
  * Tools / Android SDK Build-tools 23.0.3
  * Tools / Android SDK Platform-tools 23.1
  * Tools / Android SDK Platform-tools 19.1
  * Android 6.0 (API 23) / Documentation for Android SDK (optional, but recommended)
  * Android 6.0 (API 23) / SDK Platform
  * Android 6.0 (API 23) / Sources for Android SDK (optional, but recommended)
  * Android 4.4.2 (API 19) / SDK Platform
  * Android 4.4.2 (API 19) / Google APIs Intel x86 Atom System Image (if you want to use emulator)
  * Android 4.4.2 (API 19) / Sources for Android SDK (optional, but recommended)
  * Extras / Android Support Repository
  * Extras / Google Play services
  * Extras / Google USB Driver (if your OS requires it)
  * Extras / Intel x86 Emulator Accelerator (HAXM Installer) (if you want to use emulator on Windows)
* Install Apache Ant (newest version should work) and add its `bin` directory to the `PATH` environment variable.
* Set `GRADLE_USER_HOME` environment variable to a directory in which Gradle will locally cache the dependencies downloaded from maven repository ([Gradle doc about environment variables](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_properties_and_system_properties)). (optional)

## Build steps ##

* Setup the local build requirements as described above. 
* `git clone https://github.com/konrad-jamrozik/droidmate.git repo`
* `cd repo`
* `chmod +x gradlew` (on Unix systems)
* `gradlew build` (on Unix systems always add `./` i.e. in this case run `./gradlew build`)

If the last step finished with `BUILD SUCCESSFUL` you successfully built DroidMate and successfully ran all regression tests that do not require an Android device.

## Testing DroidMate with Android device ##

After your build passes, you should setup an Android device and run tests requiring it.

* Setup an Android device, as described in the [official doc](http://developer.android.com/training/basics/firstapp/running-app.html#RealDevice). To see which Android devices DroidMate supports, consult the device compatibility section below.
* If using a physical device (as opposed to emulator), ensure the "settings" app is on the main home screen on the device. You can drag & drop it from the apps list. If you omit this step, DroidMate will not be able to ensure WiFi is enabled before each app restart during exploration. It will work, but will issue a warning to logcat.
* Run DroidMate tests requiring device as described in the section below.

## Daily building and testing ##

All actions in this section assume you first did `cd repo/dev/droidmate`

To build DroidMate and run all regression tests that do not require a device:  `gradlew build`  

To skip tests: `gradlew build -x test`

To run tests only: `gradlew test`

To do a clean build (a full rebuild): `gradlew clean build`

### Testing against a device ###

To run DroidMate regression tests requiring a device:

1. Ensure `adb devices` shows exactly one Android device is available.
* Ensure the device displays home screen (by just looking at it).
* Run:  
`cd repo/dev/droidmate`  
`gradlew testDevice`

## Deploying to local maven repository ##

`cd repo/dev/droidmate`  
`gradlew build install`

This step is necessary to be able to run DroidMate usage examples tests, as described in `repo/RUNNING.md` 

### Library compatibility ###

DroidMate uses the following versions of tools that are independent from local setup:

| Tech.         | Version |
| ------------- | ------- |
| Groovy        | 2.4.6   |
| Gradle        | 2.12    |
| Kotlin        | 1.0.1-2 |
| Android Plugin for Gradle | 1.5.0 |
