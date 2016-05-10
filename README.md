# DroidMate [![Build Status](https://travis-ci.org/konrad-jamrozik/droidmate.svg?branch=master)](https://travis-ci.org/konrad-jamrozik/droidmate)
  
  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 12 April 2016

# Introduction #

**DroidMate** is an automated GUI [execution generator | UI fuzzer | dynamic analysis engine] for Android apps.

This file pertains to DroidMate source. You should have found it at DroidMate
repository root dir, denoted in this file as `repo`.

This file explains:

- What DroidMate is and how it works.
- With which operating systems and tools DroidMate is compatible.

Furthermore:

// TODO
- `repo/BUILDING.md` explains how to build and test DroidMate.
- `repo/RUNNING.md` explains how to use DroidMate API from your Java project, with examples.
- `repo/DEVELOPING.md` explains how to setup an IDE for DroidMate development and how to navigate DroidMate sources and technical documentation.
- `repo/TROUBLESHOOTING.md` explains known bugs & problems and how to work around them.

# How DroidMate works #

DroidMate fully automatically explores behavior of an Android app by clicking on its GUI. DroidMate repeatedly reads the device state, makes a decision and clicks on a GUI, until some termination criterion is satisfied. This process is called an **exploration** of the **Application Under Exploration (AUE)**.

DroidMate is fully automatic: after it has been set up and started, the exploration itself does not require human presence.

As input, DroidMate reads a directory containing Android apps (.apk files). It outputs a serialized Java object representing the exploration output. It also outputs .txt files having various human-readable information extracted from the serialized exploration output.

DroidMate can click and long-click the AUE’s GUI, restart the AUE,  press ‘home’ button and  it can terminate the exploration. Any of this is called an **exploration action**. DroidMate’s **exploration strategy** decides which exploration action to execute based on the XML representation of the currently visible device GUI, i.e. a **GUI snapshot**, and on the set of Android framework methods that have been called after last exploration action, i.e. a set of **API calls**.

For more information, please see the papers available on the website linked above.

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

To do a clean build (a rebuild): `gradlew clean build`

### Testing against a device ###

To run DroidMate regression tests requiring a device:

1. Ensure `adb devices` shows exactly one Android device is available.
* Ensure the device displays home screen (by just looking at it).
* Run:  
`cd repo/dev/droidmate`  
`gradlew testDevice`

## Running DroidMate ##

To run DroidMate:  
`cd repo/dev/droidmate`  
`gradlew :projects:command:run` or `gradlew :p:com:run` for short.

DroidMate will read command line arguments from the first line of
`repo/dev/droidmate/args.txt`

Most likely, the input apks will be taken from `repo/dev/droidmate/apks` (as determined by the first line of `args.txt`)

## Compatibility ##
### OS compatibility ###

DroidMate works on Ubuntu (see Travis CI build), Windows 10 and Mac OS X.
 
### Android devices and emulators compatibility ###

DroidMate works with Android 4.4.2 (API 19), both physical devices and emulators. 

It works on following devices:

* Nexus 7 (both 2012 and 2013) 
* Nexus 10 
* Samsung Galaxy S3

Other devices are not recognized by DroidMate, but may work, as long as the package name of the home screen is the same 
as for Nexus 7, i.e. `com.android.launcher`. You can check the package name by doing the following:

* launch Android Device Monitor, e.g. with `android-sdk/tools/monitor.bat`; 
* select running device. If device is running, `adb devices` will show it;
* click on `Dump View Hierarchy for UI Automator`;
* click on the top level `FrameLayout` and look at `package`.

DroidMate also has an experimental support Android for 6.0 (API 23). However, when running on the fast x86 emulators, DroidMate
 cannot work on [inlined apks](#preparing-apks-for-droidmate) and thus, cannot monitor calls to Android framework. This is due to the fact ArtHook, the library
 used in DroidMate for monitoring when running on API 23, is compatible only with ARM architecture, not x86.

### Library compatibility ###

DroidMate uses the following versions of tools that are independent from local setup:

| Tech.         | Version |
| ------------- | ------- |
| Groovy        | 2.4.6   |
| Gradle        | 2.12    |
| Kotlin        | 1.0.1-2 |
| Android Plugin for Gradle | 1.5.0 |

Following local setup is proven to work with DroidMate:

| Tech.         | Version |
| ------------- | ------- |
| Windows  | 10 |
| IntelliJ | 2016.1 #IU-145.258 |
| JDK 8    | 77-b03 x64 |

## DroidMate input ##

DroidMate reads as input all `.apk` files located in `repo/dev/droidmate/apks` 

### Preparing apks for DroidMate ####

DroidMate can run on normal apks, but it is intended to run on `inlined` apks. When run on inlined apks, DroidMate is able to 
monitor which methods of Android framework these apks access.

To inline apks, run DroidMate with `-inline` argument. The original apks will be retained.

Inlined apks can be distinguished by an `-inlined` suffix in their name.

### Obtaining apks ###

You can obtain .apk files of the apps in following ways:

* Use a dedicated app for that. See [androidpit.com/how-to-download-apk-file-from-google-play](https://www.androidpit.com/how-to-download-apk-file-from-google-play)
* Copy the artificial apk fixtures coming from DroidMate, that can be found in `repo/dev/droidmate/projects/core/src/test/resources/fixtures/apks`
after DroidMate was successfully built. Their sources are available in the `repo/dev/apk_fixtures_src` project.
* Download `Samples for SDK` using Android SDK Manager and build them.
* Install an app from Google Play Store to an Android device and then pull the app from the device using `adb` from Android SDK. For example, the [currency converter](https://play.google.com/store/apps/details?id=com.frank_weber.forex2) has `id=com.frank_weber.forex2` in its URL, denoting its package name. After you install it on the device, you can pull it in the following way: <pre>
$ adb shell pm path com.frank_weber.forex2
package:/data/app/com.frank_weber.forex2-1.apk
$ adb pull /data/app/com.frank_weber.forex2-1.apk
3674 KB/s (2361399 bytes in 0.627s)
// The file is now in the current dir
</pre>

