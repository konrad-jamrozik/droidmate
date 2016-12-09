  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review of this document: 13 Jun 2016

# Building, testing and running DroidMate #

DroidMate is built with [Gradle](https://docs.gradle.org/current/userguide/userguide.html). Each DroidMate commit is built with Travis CI, a continuous integration server. You can use it as a reference to troubleshoot your local build setup. Travis CI configuration file: `repo/.travis.yml`. To view the full, detailed log of the build done on the CI server, click on this label => [![Build Status](https://travis-ci.org/konrad-jamrozik/droidmate.svg?branch=master)](https://travis-ci.org/konrad-jamrozik/droidmate).

## Local build requirements ##

To be able to build DroidMate on your local machine, you will need JDK, Android SDK with Android 4 and 6, Apache Ant, gnuplot 4+ with `pdf` terminal and some environment variables set to appropriate values.

To configure your local setup, do the following:

* Install Java Development Kit (JDK) 8. Set `JAVA_HOME` environment variable to point to its location.
* Install Android SDK. Set `ANDROID_HOME` environment variable to point to its location.
* Run SDK Manager of Android SDK with admin rights. Select and install the following packages:
  * Tools / Android SDK Tools 25.2.3
  * Tools / Android SDK Platform-tools 25.0.1
  * Tools / Android SDK Build-tools 25.0.1
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
* Install gnuplot 4.4.3 or newer, e.g. [from sourceforge](https://sourceforge.net/projects/gnuplot/files/gnuplot). Add `gnuplot/bin` directory to the `PATH` environment variable.
  * On Mac OS X you will have to install gnuplot together with `pdf` terminal. Using homebrew you can do this by doing 
  `brew install gnuplot --with-pdflib-lite`.
* Set `GRADLE_USER_HOME` environment variable to a directory in which Gradle will locally cache the dependencies downloaded from maven repository ([Gradle doc about environment variables](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_properties_and_system_properties)). (optional)

## First build ##

* Setup the local build requirements as described above. 
* `git clone https://github.com/konrad-jamrozik/droidmate.git repo`
* `cd repo/dev/droidmate`
* `chmod +x gradlew` (on Unix systems)
* `gradlew build` (on Unix systems always add `./` i.e. in this case run `./gradlew build`)

If the last step finished with `BUILD SUCCESSFUL` you successfully built DroidMate and successfully ran all regression tests that do not require an Android device.

## Daily building and testing ##

All actions in this section assume you first did `cd repo/dev/droidmate`

To do a full build, i.e. to build DroidMate and run all regression tests that do not require a device :  `gradlew build`  

To skip tests: `gradlew build -x test`

To run tests only: `gradlew test`

To do a clean build (a full rebuild): `gradlew clean build`

## Testing with an Android device

### Setting up the device 

After your build passes, you should setup an Android device and run tests requiring it.

* Setup an Android device, as described in the [official doc](http://developer.android.com/training/basics/firstapp/running-app.html#RealDevice). To see which Android devices DroidMate supports, consult the device compatibility section given in `README.md`.
* If using Android 4.4.4 (API 19) and a physical device (as opposed to emulator), ensure the "settings" app is on the main home screen on the device. You can drag & drop it from the apps list. If you omit this step, DroidMate will not be able to ensure WiFi is enabled before each app restart during exploration. DroidMate will work, but will issue a warning to logcat.
* Configure the device: (on Android 4.4.4 the menus might have different names)
  * In "Security", set "Screen Lock" to "None" .
  * In "Display", set "Sleep" to max ("After 30 minutes of inactivity").
  * Enable developer options and set "Stay awake" to true.
* Run DroidMate tests requiring device as described in the section below.

### Running tests with the device

To run DroidMate regression tests requiring a device:

1. Ensure `adb devices` shows exactly one Android device is available.
* Ensure the device displays home screen (by just looking at it).
* Run:  
`cd repo/dev/droidmate`  
`gradlew testDevice_api19` // If you are using device with Android 4.4.2  
`gradlew testDevice_api23` // If you are using device with Android 6.0.0 

## Deploying to local maven repository ##

`cd repo/dev/droidmate`  
`gradlew build install`

This step is necessary to be able to run DroidMate usage examples tests, as described in `repo/RUNNING.md` 
