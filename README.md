# DroidMate [![Build Status](https://travis-ci.org/konrad-jamrozik/droidmate.svg?branch=master)](https://travis-ci.org/konrad-jamrozik/droidmate)
  
  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 12 April 2016

# Introduction #

**DroidMate** is an automated GUI execution generator (or: UI fuzzer; dynamic analysis engine) for Android apps.

This file pertains to DroidMate source. You should have found it at DroidMate
repository root dir, denoted in this file as `repo`.

This file explains:

- What DroidMate is and how it works.
- How to build, test and run DroidMate.
- How to setup an IDE for DroidMate development.
- How to navigate DroidMate sources and technical documentation.


# How DroidMate works #

DroidMate fully automatically decides and clicks on Android app's GUI. DroidMate repeatedly reads the device state, makes a decision and clicks on a GUI, until some termination criterion is satisfied. This process is called an **exploration** of the **Application Under Exploration (AUE)**.

DroidMate is fully automatic: after it has been set up and started, the exploration itself does not require human presence.

As input, DroidMate reads a directory containing Android apps (.apk files). It outputs a serialized Java object representing the exploration output. It also outputs .txt files having various human-readable information extracted from the serialized exploration output.

DroidMate can click and long-click the AUE’s GUI, restart the AUE,  press ‘home’ button and  it can terminate the exploration. Any of this is called an **exploration action**. DroidMate’s **exploration strategy** decides which exploration action to execute based on the XML representation of the currently visible device GUI, a **GUI snapshot**, and on the set of Android framework methods that have been called after last exploration action, a set of **API calls**.

For more information, please see the papers available on the website linked above.

# Building, testing and running DroidMate #

DroidMate is built with [Gradle](https://docs.gradle.org/current/userguide/userguide.html). Each DroidMate commit is built with Travis CI, a continuous integration server. You can use it as a reference to troubleshoot your local build setup. Travis CI configuration file: `repo/.travis.yml`. To view the full, detailed log of the build done on the CI server, click on: [![Build Status](https://travis-ci.org/konrad-jamrozik/droidmate.svg?branch=master)](https://travis-ci.org/konrad-jamrozik/droidmate).

## Local build requirements ##

To be able to build DroidMate on your local machine, do the following:

* Install Java Development Kit (JDK) 8, 7 and 6.
  * Set `JAVA8_HOME` environment variable to point to JDK 8 location. Analogously for `JAVA7_HOME` and `JAVA6_HOME`.
* Install Android SDK. Set `ANDROID_HOME` environment variable to point to its location.
* Run SDK Manager of Android SDK with admin rights. Download the following:
  * Android SDK Build-tools 19.1
  * Android 4.4.2 SDK Platform
* Install Apache Ant (newest version should work) and add its `bin` directory to the `PATH` environment variable.
* (optional) Set `GRADLE_USER_HOME` environment variable to a directory in which Gradle will locally cache the dependencies downloaded from maven repository ([Gradle doc about environment variables](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_properties_and_system_properties)).

## Build steps ##

* Setup the local build requirements as described above. 
* Clone this repository. This document denotes the root directory of your clone as `repo`.
* `cd repo`
* On Unix systems, run `chmod +x gradlew`
* Run `./gradlew build`

If the last step finished with `BUILD SUCCESSFUL` you successfully built DroidMate and successfully ran all regression tests that do not require an Android device.

## Testing DroidMate with Android device ##

After your build passes, you should setup an Android device and run tests requiring it.

* Setup an Android device, as described in the [official doc](http://developer.android.com/training/basics/firstapp/running-app.html#RealDevice). To see which Android devices DroidMate supports, consult the device compatibility section below.
* Ensure the "settings" app is on the main home screen on the device. You can drag & drop it from the apps list. If you omit this step, DroidMate will not be able to ensure WiFi is enabled before each app restart during exploration. It will work,  but will issue a warning to logcat.
* Run DroidMate tests requiring device as described in the section below.

## Daily building and testing ##

All actions in this section assume you first did `cd repo/dev/droidmate`

To build DroidMate and run all regression tests that do not require a device:  `./gradlew build`  

To skip tests: `gradlew build -x test`

To run tests only: `gradlew test`

To do a clean build (a rebuild): `gradlew clean build`

### Testing against a device ###

To run DroidMate regression tests requiring a device:

1. Ensure `adb devices` shows exactly one Android device is available.
* Ensure the device displays home screen (by looking at it).
* Run:  
`cd repo/dev/droidmate`  
`./gradlew testDevice`

## Running DroidMate ##

To run DroidMate:  
`cd repo/dev/droimate`  
`./gradlew :projects:command:run` or `./gradlew :p:com:run` for short.

DroidMate will read command line arguments from the first line of
`repo/dev/droidmate/args.txt`

Most likely, the input apks will be taken from `repo/dev/droidmate/apks` (as determined by the first line of `args.txt`)

## Compatibility ##
### OS compatibility ###

DroidMate works on Ubuntu (see Travis CI build) and Windows 10. 

DroidMate also works on Mac OS X, but please see the [troubleshooting Mac OS problems section](#troubleshooting-mac-os-problems).
 
### Android device compatibility ###

DroidMate works with Android 4.4.2 on following devices: 

* Nexus 7 (both 2012 and 2013) 
* Nexus 10 
* Samsung Galaxy S3

Currently, other devices will not work.

Emulators might work, but no guarantees here.

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
| JDK 7    | 79-b15 x64 |
| JDK 6    | 45-b06 x64 |


## DroidMate input ##
### Preparing apks for DroidMate ####

DroidMate cannot run on normal apks, they first have to be `inlined`. To inline a set of apks, do the following:

* Copy the apks to `repo/dev/droidmate/projects/apk-inliner/input-apks`
* Run:  
`cd repo/dev/droidmate`  
`./gradlew :projects:core:prepareInlinedApks` or `./gradlew :p:cor:pIA` for short.  
The apks will be placed in `repo/dev/droidmate/apks/inlined`
* Run DroidMate with cmd line arg of `-apksDir=apks/inlined` to use these apks.

Inlined apks can be distinguished by an `-inlined.apk` suffix in their name.

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

## Troubleshooting Mac OS problems

##### Problem: Missing rt.jar from JDK 1.6 (credit: Mark Schuegraf)

###### Description

In Mac OS The structure of Java JDK 1.6 is different to the later versions in that it doesn't have a directory `jre/lib` that contains `rt.jar` - instead `rt.jar` is called `classes.jar` and is found within the directory `1.6.0.jdk/Contents/Classes`.

This is due to the fact that the only way to use SE 6 on Mac OS is to use an Apple variant of the SDK i.e. https://support.apple.com/kb/DL1572?locale=en_US

There is no Mac OS download available on the Oracle site, due to SE 6 being a core component of OS X Mavericks.

###### Workaround

Simply make a `jre/lib` directory within java home and symlink `rt.jar` within it to `classes.jar.`

# Working with DroidMate code base

DroidMate is developed with IntelliJ IDEA  using the directory-based project format (`.idea`  directory). To work with DroidMate, IntelliJ has to be configured with all the dependencies used for daily building (e.g. JDKs) plus it has to have the following:

* Gradle plugin.
* Android Support plugin.
* Kotlin plugin.

After opening an IntelliJ project (see section below), run `Refresh all gradle projects` from `Gradle` plugin toolbar. After this you should be able to `Build -> Make Project` and run the tests (see section below).

### IntelliJ settings

My settings.jar can be obtained from [this GitHub repo](https://github.com/konrad-jamrozik/utilities/tree/master/resources). To import them to IntelliJ click: `File -> Import Settings...`

## Running tests from IntelliJ

`FastRegressionTestSuite` is the main test suite. It doesn't require a device.

`DroidmateFrontendTest.Explores monitored apk on a real device` runs the most important test that requires a device.

For how these tests relate to Gradle tasks, see `repo/dev/droidmate/projects/core/build.gradle`.
Search in that file for `test {` and `task testDevice`

##### Setting up IntelliJ for running single tests

In `Run/Debug configurations` in `Defaults` section set `JUnit` `Working directory` to the absolute path to your repo root. Otherwise single tests run from IntelliJ won't work as expected.


## Running DroidMate from IntelliJ

IntelliJ `droidmate` project has a set of run configurations whose name starts with `Explore apks`. They serve as a documentation by example.

DroidMate `main()` is also being run by tests in `DroidmateFrontendTest`.

### Dependencies documentation and sources ###

When developing DroidMate one wants to have access to the sources and documentation of the dependencies used in the source code.

When building for the first time, Gradle downloads from maven repos the dependencies to local cache, together with docs and sources, readily accessible from IDE.

To get access to Android SDK sources form IDE, download `Sources for Android SDK` for `Android 4.4.2` using Android SDK Manager.

If you still do not have access to some sources and docs, manually add them in IntelliJ `Project sturcture -> Platform settings`

### IntelliJ projects

Following directories are sources which can be opened  as IntelliJ projects (`File -> Open`):

* `repo/dev/droidmate` -- the `droidmate` project: main sources of DroidMate.
* `repo/dev/init` -- the `init` project: sources for the initial build.
* `repo/dev/apk_fixtures_src` -- the `apk_fixures_src` project: sources of apk fixtures used in the `droidmate` project tests.

## Troubleshooting IntelliJ ##

* If IntelliJ builds fail erratically, close it, do full clean and build from Gradle and reopen IntelliJ.

* When opening `repo/dev/droidmate` in IntelliJ, it is expected to have the following error:
> Unsupported Modules Detected: Compilation is not supported for following modules: DummyAndroidApp. Unfortunately you can't have non-Gradle Java modules and Android-Gradle modules in one project.

The `DummyAndroidApp` project is added only to enable Android plugin views, like e.g. logcat.

* If you get on Gradle rebuild:
> Unsupported major.minor version 52.0

Ensure that Gradle is using JDK 8 in: `Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JVM`.

# Technical documentation  #

Technical docs will be  located in `repo/dev/droidmate/doc`. As of 7 April 2016 only severely outdated documentation is available.

The entry class of DroidMate is `DroidmateFrontend` and so it is recommended to start code base exploration from this class. You can find it in

`repo/dev/droidmate/projects/core/src/main/groovy/org/droidmate/frontend/DroidmateFrontend.groovy`

### Tests as documentation ###

Tests of DroidMate serve also as example use cases. If given class has a corresponding test class, it will have a `Test` suffix. So `DroidmateFrontend` has a `DroidmateFrontendTest` class with tests for it. You can navigate to tests of given class (if any) in IntelliJ with `ctrl+shift+T` (`Navigate -> Test` in keymap). Tests always live in `<project dir>/src/test`. Tests of core functionality are located in the `core` project.

Run the tests from IntelliJ as described in section above to be able to navigate to them directly. If you run a Gradle build, you can see the test report in:
`repo/dev/droidmate/projects/core/build/reports/tests/index.html`