  
  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 10 May 2016

# Working with DroidMate code base

## Setting up IntelliJ

DroidMate is developed with IntelliJ IDEA using the directory-based project format (`.idea`  directory). To work with DroidMate, IntelliJ has to be configured with all the dependencies used for daily building (e.g. JDK) plus it has to have the following:

* Gradle plugin.
* Android Support plugin.
* Kotlin plugin.

After opening an IntelliJ project (e.g. `repo/dev/droidmate`), run `Refresh all Gradle projects` from `Gradle` plugin toolbar. After this you should be able to `Build -> Make Project` and run the tests (see section below).

If you run into problems, please see the IntelliJ section in `repo/TROUBLESHOOTING.md`.


### IntelliJ settings

My settings.jar can be obtained from [this GitHub repo](https://github.com/konrad-jamrozik/utilities/tree/master/resources). To import them to IntelliJ click: `File -> Import Settings...`

### Setting up IntelliJ for running single tests

In `Run/Debug configurations` in `Defaults` section set `JUnit` `Working directory` to the absolute path to `repo/dev/droidmate`. 
Otherwise single tests run from IntelliJ won't work as expected.

### DroidMate dependencies documentation and sources

When developing DroidMate one wants to have access to the sources and documentation of the dependencies used in the source code.

When building for the first time, Gradle downloads from maven repos the dependencies to local cache, 
together with docs and sources, readily accessible from IDE.

To get access to Android SDK sources form IDE, download `Sources for Android SDK` for `Android 4.4.2` using Android SDK Manager.

If you still do not have access to some sources and docs, manually add them in IntelliJ `Project sturcture -> Platform settings`

## IntelliJ projects

Following directories are sources which can be opened  as IntelliJ projects (`File -> Open`):

| project in `repo/dev`| description |
| ------- | ----------- |
| droidmate | main sources of DroidMate. |
| apk_fixtures_src | sources of apk fixtures used in the `droidmate` project tests. |
| droidmate_usage_examples | java project showing how to use DroidMate API |

Note that `apk_fixtures_src` is being built as part of the `droidmate` build. 


## Running DroidMate from IntelliJ

DroidMate has a set of predefined run configurations, summarized here. They exist to help you get started with running DroidMate 
from IDE while developing it. If you want to use DroidMate API from your Java program, without editing DroidMate sources, 
please see `repo/RUNNING.md`.

### Application run configs

The `Explore apks` run configs show you example ways of running DroidMate. You can ignore run configs in `Data extraction` and
`Reporting` folders. They are either deprecated or experimental. In both cases they are not supported.

### Gradle run configs

Use `clean` to reset everything, `build install` to build everything and install to local maven repository, and `testDevice`
 to run tests requiring device.
 
### JUnit run configs

`FastRegressionTestSuite` is the main test suite of DroidMate, run by the `:projects:command:test` Gradle task.  
`Explores monitored apk on a real device api19` is being run by `:projects:command:testDevice` Gradle task.

The root of all test suites is `org.droidmate.test_suites.AllTestSuites`.

# Technical documentation 

If you want to understand how to use DroidMate API, please refer to `repo/RUNNING.md`.

The entry class of DroidMate is `DroidmateFrontend` and so it is recommended to start code base exploration from this class.  
You can find it in:

`repo/dev/droidmate/projects/core/src/main/groovy/org/droidmate/frontend/DroidmateFrontend.groovy`

### Tests as documentation ###

Tests of DroidMate serve also as example use cases. If given class has a corresponding test class, it will have a `Test` suffix. So `DroidmateFrontend` has a `DroidmateFrontendTest` class with tests for it. You can navigate to tests of given class (if any) in IntelliJ with `ctrl+shift+T` (`Navigate -> Test` in keymap). Tests always live in `<project dir>/src/test`. Tests of core functionality are located in the `core` project.

Run the tests from IntelliJ as described in section above to be able to navigate to them directly. If you run a Gradle build, you can see the test report in:
`repo/dev/droidmate/projects/core/build/reports/tests/index.html`

## Editing the list of monitored APIs

The list of monitored APIs is located in

`repo/dev/droidmate/projects/resources/appguard_apis.txt`

Lines starting with `#` and empty lines are discarded.

After you make your changes, do a build (see `repo/BUILDING.md`).

To test if DroidMate successfully monitored your modified API list, observe the logcat output
while the explored application is started. In case of Android 4.2.2, you will see 100+ messages
tagged `Instrumentation`. If there were any failures, the messages will say so.

## Providing your own hooks to the monitored APIs

Please see the javadocs in `repo/dev/droidmate/projects/monitor-hook/src/main/java/org/droidmate/monitor/IMonitorHook.java`.