  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review of this document: 13 Jun 2016

# Troubleshooting DroidMate runs

All the mentioned log `.txt` files by default are located under `repo/dev/droidmate/output_device1/logs`.

### Problem: On Android 4.2.2, DroidMate run fails with exception before any exploration takes place. 

#### Symptoms:
`master_log.txt` shows:

<pre>
// ...
TRACE org.droidmate.device.AndroidDevice       setupConnection(emulator-5554) / this.startUiaDaemon()
DEBUG org.droidmate.device.AndroidDevice       startUiaDaemon()
WARN  o.droidmate.tools.AndroidDeviceDeployer  ! Caught AdbWrapperException in setupDevice(deviceIndex: 0). Adding as a cause to an ExplorationException. Then adding to the collected exceptions list.
INFO  o.d.d.ExplorationOutputAnalysisPersister Persisting data from exploration output.
WARN  o.d.d.ExplorationOutputAnalysisPersister Exploration output is empty! Aborting data extraction.
ERROR org.droidmate.frontend.ExceptionHandler  A nonempty ThrowablesCollection was thrown during DroidMate run. Each of the 1 Throwables will now be logged.
// ...
</pre>

`exceptions.txt` shows:
<pre>
// ...
org.droidmate.exceptions.AdbWrapperException: Failed waiting for at least 1 messages on logcat. actual messages count before timeout: 0,  s/n: emulator-5554, messageTag: uiautomator-daemon_server_start_tag, minMessageCount: 1, waitTimeout: 20000, queryDelay: 2000
	// ...
	at org.droidmate.android_sdk.AdbWrapper.waitForMessagesOnLogcat(AdbWrapper.groovy:376) ~[main/:na]
// ...
</pre>

logcat shows:
<pre>
// ...
04-29 18:06:40.416 2267-2267/? E/UiAutomatorTestRunner: uncaught exception
java.lang.IllegalStateException: UiAutomationService android.accessibilityservice.IAccessibilityServiceClient$Stub$Proxy@acfd8658already registered!
  at android.os.Parcel.readException(Parcel.java:1480)
  // ...
// ...
</pre>

#### Diagnosis:
Last run didn't finish properly and uiautomator-daemon service didn't terminate, preventing new instance from starting. Because new instance cannot start, DroidMate throws exception and terminates before exploration can take place.

The uiautomator-daemon most likely didn't terminate because  `org.droidmate.uiautomator_daemon.UiAutomatorDaemon.init` threw an exception.

#### Manual fix:
Open bash or cmd shell and do:
<pre>
$ adb shell

root@generic_x86:/ # ps | grep uia
ps | grep uia
root      2024  2022  302468 33076 ffffffff b773a179 S uiautomator

root@generic_x86:/ # kill 2024
kill 2024
</pre>

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
