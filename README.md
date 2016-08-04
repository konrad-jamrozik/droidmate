# DroidMate ![GNU GPL v3](https://www.gnu.org/graphics/gplv3-88x31.png)[![Build Status](https://travis-ci.org/konrad-jamrozik/droidmate.svg?branch=master)](https://travis-ci.org/konrad-jamrozik/droidmate)

DroidMate, an automated execution generator for Android apps.  
Copyright (C) 2012-2016 Konrad Jamrozik

This program is free software. 

Contact: jamrozik@st.cs.uni-saarland.de  

* www.droidmate.org  
* [DroidMate publication](http://www.boxmate.org/files/DroidMate_MOBILESoft_2016.pdf)  
* ["Mining Sandboxes" publication](http://www.boxmate.org/files/boxmate-preprint.pdf)  
* ["Guarantees from Testing" talk by Andreas Zeller](https://www.youtube.com/watch?v=eJyIKt7xuw4)

Date of last full review of this document: 13 Jun 2016

# Introduction #

**DroidMate** is an automated execution generator / GUI fuzzer / dynamic analysis engine for Android apps.

This file pertains to DroidMate source. You should have found it at DroidMate
repository root dir, denoted in this file as `repo`.

This file explains:

- What DroidMate is and overview of how it works.
- With which operating systems and Android devices DroidMate is compatible.

Furthermore:

- `repo/BUILDING.md` explains how to build and test DroidMate.
- `repo/RUNNING.md` explains how to:
  - use DroidMate API from your Java project, with examples;
  - run DroidMate directly from built sources (this method is not recommended, use API instead); 
  - obtain input apks;
  - configure the emulator.
- `repo/DEVELOPING.md` explains how to: 
  - setup an IDE for DroidMate development;
  - navigate DroidMate sources and technical documentation;
  - edit the list of monitored APIs;
  - provide your own hooks to the monitored APIs.
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

Primary development of DroidMate is done on Windows 10. Ubuntu is used in the CI server. Mac OS X is actively being used by DroidMate users.
 
### Android devices and emulators compatibility ###

DroidMate works on Android 6.0 (API 23), on physical devices. It also works on Android 4.4.2 (API 19), both physical devices and emulators. 

DroidMate works on API 23 emulators, but with limitations. It works fully on the slow, ARM-based emulators. In the fast x86 emulators, DroidMate cannot work with _inlined_ (see `repo/RUNNING.md`) apks and thus, cannot monitor calls to Android framework. This is due to the fact ArtHook, the library used in DroidMate for monitoring when running on API 23, is compatible only with ARM architecture, not x86.

DroidMate works on following devices:

API 23:
* Nexus 7 2013
* Nexus 5X

API 19:
* Nexus 7 2012
* Nexus 10
* Samsung Galaxy S3

If DroidMate doesn't recognize a device it defaults to Nexus 7. You can change the default by editing [DeviceModel](https://github.com/konrad-jamrozik/droidmate/blob/master/dev/droidmate/projects/core/src/main/groovy/org/droidmate/device/model/DeviceModel.groovy#L76-L79). In your device model you just have to ensure the package name of the home screen is correct. You can check the package name by doing the following:

* launch Android Device Monitor, e.g. with `android-sdk/tools/monitor.bat`; 
* select running device. If device is running, `adb devices` will show it;
* click on `Dump View Hierarchy for UI Automator`;
* click on the top level `FrameLayout` and look at `package`.
