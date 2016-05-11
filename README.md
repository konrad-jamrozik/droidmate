# DroidMate [![Build Status](https://travis-ci.org/konrad-jamrozik/droidmate.svg?branch=master)](https://travis-ci.org/konrad-jamrozik/droidmate)
  
  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 10 May 2016

# Introduction #

**DroidMate** is an automated GUI [execution generator | UI fuzzer | dynamic analysis engine] for Android apps.

This file pertains to DroidMate source. You should have found it at DroidMate
repository root dir, denoted in this file as `repo`.

This file explains:

- What DroidMate is and how it works.
- With which operating systems and tools DroidMate is compatible.

Furthermore:

- `repo/BUILDING.md` explains how to build and test DroidMate.
- `repo/RUNNING.md` explains how to:
  - use DroidMate API from your Java project, with examples;
  - run DroidMate from built sources; 
  - obtain input apks;
  - configure the emulator.
- `repo/DEVELOPING.md` explains how to: 
  - setup an IDE for DroidMate development;
  - navigate DroidMate sources and technical documentation;
  - edit the list of monitored APIs.
- `repo/TROUBLESHOOTING.md` explains how to work around known bugs & problems.

# How DroidMate works #

DroidMate fully automatically explores behavior of an Android app by clicking on its GUI. DroidMate repeatedly reads the device state, makes a decision and clicks on a GUI, until some termination criterion is satisfied. This process is called an **exploration** of the **Application Under Exploration (AUE)**.

DroidMate is fully automatic: after it has been set up and started, the exploration itself does not require human presence.

As input, DroidMate reads a directory containing Android apps (.apk files). It outputs a serialized Java object representing the exploration output. It also outputs .txt files having various human-readable information extracted from the serialized exploration output.

DroidMate can click and long-click the AUE’s GUI, restart the AUE,  press ‘home’ button and  it can terminate the exploration. Any of this is called an **exploration action**. DroidMate’s **exploration strategy** decides which exploration action to execute based on the XML representation of the currently visible device GUI, i.e. a **GUI snapshot**, and on the set of Android framework methods that have been called after last exploration action, i.e. a set of **API calls**.

For more information, please see the papers available on the website linked above.


## Compatibility ##
### OS compatibility ###

DroidMate works on Ubuntu, Windows 10 and Mac OS X.
 
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
 cannot work on _inlined_ (see `repo/RUNNING.md`) apks and thus, cannot monitor calls to Android framework. This is due to the fact ArtHook, the library
 used in DroidMate for monitoring when running on API 23, is compatible only with ARM architecture, not x86.

Following local setup is proven to work with DroidMate:

| Tech. | Version |
| ----- | ------- |
| Windows  | 10 |
| IntelliJ | 2016.1 #IU-145.258 |
| JDK 8    | 77-b03 x64 |
