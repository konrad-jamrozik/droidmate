## Architectural overview of DroidMate ##

TODO/OUTDATED 

The main code of DroidMate is in the "core" project, which also is the
frontend for DroidMate and so, can be built into executable distribution.

The "aut-addon" is an Android project built with Android ant script.
The resulting .dex file is the put into 's resources dir, to be used in the
instrumentation of the apk. Thus, the source code of aut-addon effectively is
the source code of the bytecode that will be attached to the apk on which
DroidMate will operate.

The "uiautomator-daemon" project is an Android uiautomator scripts project built
with Android ant script, resulting in jar file that can be run on Android
(Virtual) Device (AVD) using uiautomator. The output .jar is, like output
of aut-addon, is placed in core's resources dir. DroidMate deploys
uiautomator-daemon.jar to the device, establishes TCP connection with it and
uses it to extract the device xml GUI hierarchy and to click on the AVD.

The "common" project contains code reused by two or more of the aforementioned
projects.

All the remaining projects (scratchpad, javaScratchpad, soot-instrumenter) are
irrelevant.

Note that all the operations like copying the output .jar file happen when
appropriate tasks are called from the build.gradle script.
