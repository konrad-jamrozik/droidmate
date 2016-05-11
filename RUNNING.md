  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 10 May 2016

# API usage examples

To understand how to use DroidMate API, please refer to:
`dev/droidmate_usage_examples/src/main/java/org/droidmate_usage_examples/Main.java`  
`dev/droidmate_usage_examples/src/test/java/org/droidmate_usage_examples/MainTest.java`

Or to make your life easier, open in IntelliJ IDEA:

`dev/droidmate_usage_examples/src/main/java/org/droidmate_usage_examples`

Note that to run any tests first you will have to deploy the code to local Maven repository, as described in `repo/BUILDING.md`.

# Running DroidMate from sources

Ensure you have built DroidMate as described in `repo/BUILDING.md`.

To run DroidMate:  
`cd repo/dev/droidmate`  
`gradlew :projects:command:run` or `gradlew :p:com:run` for short.

DroidMate will read command line arguments from the first line of
`repo/dev/droidmate/args.txt`

Most likely, the input apks will be taken from `repo/dev/droidmate/apks` (as determined by the first line of `args.txt`)


# DroidMate input

DroidMate reads as input all `.apk` files located in `repo/dev/droidmate/apks` 

## Preparing apks for DroidMate ####

DroidMate can run on normal apks, but it is intended to run on `inlined` apks. When run on inlined apks, DroidMate is able to 
monitor which methods of Android framework these apks access.

To inline apks, run DroidMate with `-inline` argument. The original apks will be retained.

Inlined apks can be distinguished by an `-inlined` suffix in their name.

## Obtaining apks ###

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

# Configuring emulators

Ensure the emulator:
 
* is a Nexus (e.g. Nexus 7),
* has Google APIs,
* runs Android 4.2.2 (API 19),
* has no frame,
* has VM heap of at least 256 MB,
* has at least 1 GB of RAM.
