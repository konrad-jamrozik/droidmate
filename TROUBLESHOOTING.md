  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review of this document: 10 May 2017

# Troubleshooting DroidMate runs

All the mentioned log `.txt` files by default are located under `repo/dev/droidmate/output_device1/logs`.

# Troubleshooting IntelliJ setup

* Before working with IntelliJ, make a successful full gradle build.

* In case you run into `Java development kit not set` error or similar after clicking `Refresh all Gradle projects`, just manually point to your local installation of it. Relevant tool window for that will be linked to from the error message. 

* If IntelliJ builds fail erratically, close it, do  
`cd repo/dev/droidmate`  
`gradlew clean build`   
and reopen IntelliJ.

* When opening `repo/dev/droidmate` in IntelliJ, it is expected to have the following error:
> Unsupported Modules Detected: Compilation is not supported for following modules: DummyAndroidApp. Unfortunately you can't have non-Gradle Java modules and Android-Gradle modules in one project.

The `DummyAndroidApp` project is added only to enable Android plugin views, like e.g. logcat.

* If you get on Gradle rebuild:

> Unsupported major.minor version 52.0

Ensure that Gradle is using JDK 8 in: `Settings -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JVM`.

* If the `Refresh all gradle projects` fails with `BuildKt cannot be initalized` or similar, or if opening `repo/dev/droidmate` with IntelliJ doesn't properly load the project structure, most likely the `initalizes` tes of the `dev/droidmate/buildsrc` project fails because you didn't set appropriate environment variables (for Mac OS X, see entry below). Open this project in IntelliJ and run the `initalizes` test. It should fail. Fix the environment variables according to the stdout logs. Then retry `Refresh all gradle projects`.

* On Mac OS X environment variables are not picked up by default by GUI applications. If IntelliJ complains you do not have Java SDK or Android SDK configured, or some environment variable is missing, ensure you ran IntelliJ from command line which has those variables setup. Consider starting searching for help from [this superuser question](http://superuser.com/q/476752/225013).  
