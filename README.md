
  Copyright (c) 2012-2016 Saarland University
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik

  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 19 jan 2016

# Introduction #

**DroidMate** is an automated GUI execution generator for Android apps.

This file pertains to DroidMate source. You should have found it at DroidMate
repository root dir, denoted in this file as `.` (dot).

This file explains:

- What is DroidMate and how it works.
- How to build, test and run DroidMate.
- How to setup an IDE for development.
- How to navigate DroidMate sources and technical documentation.


# How DroidMate works #

DroidMate fully automatically decides and clicks on Android app's GUI. DroidMate repeatedly reads the device state, makes a decision and clicks on a GUI, until some termination criterion is satisfied. This process is called an **exploration** of the **Application Under Exploration (AUE)**.

DroidMate is fully automatic: after it has been set up and started, the exploration itself does not require human presence.

As input, DroidMate reads a directory containing Android apps (.apk files). It outputs a serialized data structure containing the exploration output. It also outputs .txt files having various human-readable information extracted from the serialized exploration output.

DroidMate can click and long-click the AUE’s GUI, restart the AUE,  press ‘home’ button and  it can terminate the exploration. Any of this is called an **exploration action**. DroidMate’s **exploration strategy** decides which exploration action to execute based on the XML representation of the currently visible device GUI, a **GUI snapshot**, and on the set of Android framework methods that have been called after last exploration action, a set of **API calls**.

# Building, testing and running DroidMate #

DroidMate is built with [Gradle](https://docs.gradle.org/current/userguide/userguide.html).

## Compatibility ##

DroidMate build process was tested on Windows 10, Windows 7, Mac OS and Ubuntu.

DroidMate was tested with Nexus 7 2012 running Android 4.4. It also worked with an emulator. It should work with other Android devices running Android 4.4.

In case you run into problems, please see the [Troubleshooting Mac OS problems](#troubleshooting-mac-os-problems) section.

As of March 29, 2016, DroidMate uses the following versions of tools that are independent from local setup:

| Tech.         | Version |
| ------------- | ------- |
| Groovy        | 2.4.6   |
| Gradle        | 2.12    |
| Kotlin        | 1.0.1-1 |
| Android Plugin for Gradle | 1.5.0 |

As of March 29, 2016, following local setup is proven to work with DroidMate:

| Tech.         | Version |
| ------------- | ------- |
| Windows  | 10 |
| IntelliJ | 2016.1 #IU-145.258 |
| JDK 8    | 77-b03 x64 |
| JDK 7    | 79-b15 x64 |
| JDK 6    | 45-b06 x64 |

## First build (just after cloning from repo)

To build DroidMate for the first time, follow these steps:

### 1. Setup the dependencies ###
1. Install Java Development Kit (JDK) 8, 7 and 6.
* Install Android SDK.
* In SDK Manager (with admin rights) of Android SDK, download the following:
Android SDK Build-tools 19.1
Android 4.4.2 SDK Platform
* To make `adb` (Android Debug Bridge) runnable, add `(...)/android-sdk/platform-tools` to the PATH system environment variable.
* To make `aapt` (Android Asset Packaging Tool) runnable, add `(...)/android-sdk/build-tools/19.1.0` to the PATH system environment variable.
* Install Apache Ant (newest version should work) and add its `bin` directory to the PATH system environment variable.

### 2. Do the one-time local setup ###

1. Setup local installation constants by following instructions given in `./dev/init/src/main/groovy/org/droidmate/init/LocalInitConstantsTemplate.groovy`
**IMPORTANT** When following the instructions, be double sure that you followed to the letter the step 1. in the class groovydoc!
* (optional) set `GRADLE_USER_HOME` system environment variable to a directory in which Gradle  will locally cache the dependencies downloaded from maven repository. [Gradle doc link](https://docs.gradle.org/current/userguide/gradle_command_line.html).
* Run initial build setup:
`./dev/init/gradlew build`
Note: on Linux and Mac OS will need to first do `chmod +x gradlew`

### 3. Do the build ###

1. Run a daily build as described in the section below.

If this step finished with `BUILD SUCCESSFUL` you successfully built DroidMate and successfully ran all regression tests that do not require an Android device.

### 4. Connect and test the device ###

Now you should setup an Android device and run tests requiring it:

1. Setup an Android device, as described in the [official doc](http://developer.android.com/training/basics/firstapp/running-app.html#RealDevice).
* Ensure the "settings" app is on the main home screen on the device. You can drag & drop it from the apps list. If you omit this step, DroidMate will not be able to ensure WiFi is enabled before each app restart during exploration. It will work however, just issuing a warning to logcat.
* Run DroidMate tests requiring device as described in the section below.

## Daily building and testing ##

To build DroidMate and run all regression tests that do not require a device:

`./dev/droidmate/gradlew build`

To skip tests: `gradlew build -x test`

To run tests only: `gradlew test`

To do a clean build (a rebuild): `gradlew clean build`

### Testing against a device ###

To run DroidMate regression tests requiring a device:

1. Ensure `adb devices` shows the device is available.
* Ensure the device displays home screen.
* Run `./dev/droidmate/gradlew testDevice`.

## Running DroidMate ##

Run DroidMate with
`./dev/droidmate/gradlew :projects:command:run`, or
`./dev/droidmate/gradlew :p:com:run` for short.

DroidMate will read command line arguments from the first line of
`./dev/droidmate/args.txt`

Most likely, the input apks will be taken from `./dev/droidmate/apks` (as determined by the first line of args.txt)

### Preparing apks for DroidMate ####

DroidMate cannot run on normal apks, they first have to be `inlined`. To inline a set of apks, do the following:

* Copy them to `./dev/droidmate/projects/apk-inliner/input-apks`
* Run the task
`./dev/droidmate/gradlew :projects:core:prepareInlinedApks`
or `./dev/droidmate/gradlew :p:cor:pIA` for short.
The apks will be placed in `./dev/droidmate/apks/inlined`
* Run DroidMate with cmd line arg of `-apksDir=apks/inlined` to use these apks.

Inlined apks can be distinguished by an `-inlined.apk` suffix in their name.

### Obtaining apks ###

You can obtain `.apk` files of the apps in following ways:

* Use a dedicated app for that. See [androidpit.com/how-to-download-apk-file-from-google-play](https://www.androidpit.com/how-to-download-apk-file-from-google-play)
* Copy the artificial apk fixtures coming from DroidMate, that can be found in `./dev/droidmate/projects/core/src/test/resources/fixtures/apks`
after DroidMate was successfully built. Their sources are available in the `./dev/apk-fixtures-src` project.
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

DroidMate is developed with IntelliJ IDEA 16 EAP, using the directory-based project format (`.idea`  directory). To work with DroidMate, IntelliJ has to be configured with all the dependencies used for daily building (e.g. JDKs) plus it has to have the following:

* Gradle plugin.
* Android Support plugin.
* Kotlin plugin EAP ([installation instructions](https://discuss.kotlinlang.org/t/kotlin-early-access-preview/1437))

After opening an IntelliJ project (see section below), run `Refresh all gradle projects` from `Gradle` plugin toolbar. After this you should be able to `Build -> Make Project` and run the tests (see section below).

### IntelliJ settings

My settings.jar can be obtained from [this GitHub repo](https://github.com/konrad-jamrozik/utilities/tree/master/resources). To import them to IntelliJ click: `File -> Import Settings...`

## Running tests from IntelliJ

`FastRegressionTestSuite` is the main test suite. It doesn't require a device.

`DroidmateFrontendTest.Explores monitored apk on a real device` runs the most important test that requires a device.

For how these tests relate to Gradle tasks, see `./dev/droidmate/projects/core/build.gradle`.
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

* `./dev/droidmate` -- main sources of DroidMate.
* `./dev/init` -- sources for the initial build.
* `./dev/apk-fixtures-src` -- sources of apk fixtures used in the `droidmate` project tests.

## Troubleshooting IntelliJ ##

* If IntelliJ builds fail erratically, close it, do full clean and build from Gradle and reopen IntelliJ.

* When opening `./dev/droidmate` in IntelliJ, it is expected to have the following error:
> Unsupported Modules Detected: Compilation is not supported for following modules: DummyAndroidApp. Unfortunately you can't have non-Gradle Java modules and Android-Gradle modules in one project.

The `DummyAndroidApp` project is added only to enable Android plugin views, like e.g. logcat.

* If you get on Gradle rebuild:
> Unsupported major.minor version 52.0

Ensure that Gradle is using JDK 8 in: Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JVM.

# Technical documentation  #

Technical docs will be  located in `./dev/droidmate/doc`. As of 14 Dec 2015 only severely outdated documentation is available.

The entry class of DroidMate is `DroidmateFrontend` and so it is recommended to start code base exploration from this class. You can find it in

`./dev/droidmate/projects/core/src/main/groovy/org/droidmate/frontend/DroidmateFrontend.groovy`

### Tests as documentation ###

Tests of DroidMate serve also as example use cases. If given class has a corresponding test class, it will have a `Test` suffix. So `DroidmateFrontend` has a `DroidmateFrontendTest` class with tests for it. You can navigate to tests of given class (if any) in IntelliJ with `ctrl+shift+T` (`Navigate -> Test` in keymap). Tests always live in `<project dir>/src/test`. Tests of core functionality are located in the `core` project.

Run the tests from IntelliJ as described in section above to be able to navigate to them directly. If you run a Gradle build, you can see the test report in:
`./dev/droidmate/projects/core/build/reports/tests/index.html`