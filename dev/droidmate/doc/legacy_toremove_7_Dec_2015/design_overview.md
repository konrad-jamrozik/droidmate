Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de  
Date of last full review: Dec 5, 2014

# DroidMate design overview #

One of the core objectives of DroidMate is to determine which **permission-requiring calls to Android API (API calls)** have been made during the **exploration** of **application under exploration (AUE)**. 

To obtain the API calls data, DroidMate has to execute several steps in a sequence, grouped into tasks.

### Terminology remarks ###

**(Android) device**: exploration happens either on a real world Android Device like Nexus 7 tablet, or Android Emulator / Android Virtual Device (AVD). In both cases we call it **Android device** or just a **device**. The device has to have Android API 19 as of this writing.

An Android API call is synonymous to a call to one of its methods, e.g. [WifiManager.addNetwork()](http://developer.android.com/reference/android/net/wifi/WifiManager.html#addNetwork%28android.net.wifi.WifiConfiguration%29). 

An AUE is synonymous to the `.apk` file containing it.

The data obtained from API calls is then analyzed by human to answer research questions, documented elsewhere.

More terminology will be defined in the document as needed.

## Task: Generate a DroidMate monitor ##

**Implementation status**: **TODO**. **PoCo (Proof of Concept)** done.  
**Gradle project:** `monitor-generator` **(monitor generator)**  
**Input:** Android permission-requiring API list **(API list)**  
**Output:** `dm_monitor.apk` **(DroidMate monitor, monitor apk)**

The monitoring code defines which API calls will be **monitored** during the AUE exploration. To **monitor** means to intercept an API call during runtime, execute the code defined in monitor for given API method and continue normal execution. The DroidMate monitor outputs to logback the signature of the monitored API call.

The API list is [PScout's Android 4.1.1 API mapping](http://pscout.csl.toronto.edu/download.php?file=results/jellybean_publishedapimapping ).

## Task: Inline AppGuard loader into AUE ##

**Implementation status**: **TODO**. PoCo done.  
**Gradle project:** `apk-inliner` **(apk inliner)**  
**Input:** AUE  
**Input resources:** AppGuard loader, path to DroidMate monitor on the device  
**Output:** monitored AUE  


The apk inliner inlines the `appguard-loader.dex` **(AppGuard loader)** bytecode into the AUE, making it a **monitored AUE**. The AppGuard loader ensures that when the monitored AUE is started on the device, it will load and activate the DroidMate monitor.

The **path to DroidMate monitor on the device** tells the AppGuard loader from where to load the DroidMate monitor.

## Task: Setup a device for exploration ##

**Implementation status**: **TODO**. Mostly done. Only `dm_monitor.apk` pushing left.  
**Gradle project:** `core`   
**Groovy class:**  `AndroidDeviceDeployer`  
**Input:** Android device, `dm_monitor.apk`  
**Input resources:** `uiautomator-daemon.jar`    
**Output:** Set-up Android device **(set-up device)**  

For a monitored AUE to be explored, it first needs to be deployed on a properly set-up device. 

The device has to be started manually by human and has to be reachable through Android Debug Bridge **(ADB)**. The device also has to have a world-writable dir that matches the path to DroidMate monitor on the device dir used during the AUE inlining. As of this writing the dir can be created by installing and launching a `WorldWritableDir.apk` I got from Philipp von Styp-Rekovsky.

DroidMate needs to be able to click on the GUI of the monitored AUE. This is done with the UiAutomator framework. The setup includes pushing the `uiautomator-daemon.jar` to the device and starting through ADB an UiAutomator instance from it, which launches a TCP server that then receives GUI exploration commands from DroidMate. 

For the monitored AUE monitoring to work, the `dm_monitor.apk` has to be pushed to the device to location defined in the inlining task. 

## Task: Deploy the monitored AUE ##

**Implementation status**: Done.  
**Gradle project:** `core`   
**Groovy class:**  `ApkDeployer`  
**Input:** monitored AUE, set-up Android device      
**Output:** explorable AUE

This task installs the monitored AUE on the set-up device. It also ensures the explorable AUE will be uninstalled after the exploration finishes.

## Task: Explore the explorable AUE ##

**Implementation status**: Done.  
**Gradle project:** `core`   
**Groovy class:**  `ExplorationExecutor`  
**Input:** explorable AUE, set-up Android device      
**Output:** `ApkExplorationData` **(exploration data)** (including API calls logs)

The exploration first it launches the monitored AUE's *launchable activity*, obtaining its name using the AAPT tool from Android SDK.

Then it is conducted in a loop: after each *exploration action* conducted on the device, the *device state* is read (GUI structure, API calls logs) and *exploration strategy* is used to determine what is the next *exploration action*. The exploration terminates when the strategy determines so. 

When the exploration finishes it outputs various **exploration data**, including API calls logs sent to the `core` module by DroidMate monitor after each exploration action. Also the explorable AUE gets undeployed by `ApkDeployer`.

For details on exploration process, please see appropriate design doc. 

## Task: Prepare the exploration data for human analysis ##

**Implementation status**: **TODO**, partially done.  
**Gradle project:** `core`   
**Groovy class:**  `ExplorationDataCsvOutputter`  
**Input:** `ApkExplorationData`      
**Output:** `.csv` file with the data to be analyzed.
