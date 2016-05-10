  Copyright (c) 2012-2016 Saarland University  
  All rights reserved.

  Author: Konrad Jamrozik, github.com/konrad-jamrozik
  
  This file is part of the "DroidMate" project.

  www.droidmate.org

  Date of last full review: 10 May 2016

# Troubleshooting

All the mentioned log `.txt` files by default are located under `repo/dev/droidmate/output_device1/logs`.

### Uiautomator-daemon service did not terminate ###

Still occurring as of 10 May 2016.

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
Last run didn't finish properly and uiautomator-daemon service wasn't killed properly. Most likely `org.droidmate.uiautomator_daemon.UiAutomatorDaemon.init` threw an exception.

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
