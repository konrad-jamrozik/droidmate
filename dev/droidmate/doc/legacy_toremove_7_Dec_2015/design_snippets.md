Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de  
Date of last full review: Dec 3, 2014

## Logging the API call data ##

An excerpt from an API mapping:

    Permission:android.permission.CHANGE_WIFI_STATE
	17 Callers:
	<android.net.wifi.WifiManager: boolean reassociate()>
	<android.net.wifi.WifiManager: boolean startScan()>
	<android.net.wifi.WifiManager: void setCountryCode(java.lang.String,boolean)>
	<android.net.ConnectivityManager: int startUsingNetworkFeature(int,java.lang.String)>
	<android.net.wifi.WifiManager: boolean disableNetwork(int)>
	<android.net.wifi.WifiManager: int updateNetwork(android.net.wifi.WifiConfiguration)>
	<android.net.wifi.WifiManager: boolean removeNetwork(int)>
	<android.net.wifi.WifiManager: boolean reconnect()>
	<android.net.wifi.WifiManager: int addNetwork(android.net.wifi.WifiConfiguration)>
	<android.net.wifi.p2p.WifiP2pManager: android.net.wifi.p2p.WifiP2pManager$Channel initialize(android.content.Context,android.os.Looper,android.net.wifi.p2p.WifiP2pManager$ChannelListener)>

To avoid introducing needless formats, the logcat API log could be in exactly the same format. The format will be constructed inside the bodies of the `@Redirect`ed methods as they contain the necessary information.

What about the permission required? It should also be logged immediately, e.g. after line break or vertical bar `|` or similar. The instrumentation code will be generated from the API mapping file anyway, so it can associate with each generated method the permission it requires.

#### Design details ####
The logcat message construction will be called by the tool-generated instrumentation code as well as in hand-coded instrumentation in apk fixtures for testing purposes. The messages will be parsed in DroidMate's `core` module. Thus, the code has to live in a shared, common library: `lib-common`.

The log interface will be `IApiLog` with `LogcatApiLog` implementation. The interface will be used to obtain semantic data like method name, parameters, permission required, etc. 

The logcat implementation will be able to construct the logcat message from logical elements given to it by tool-generated instrumentation method and parse it back, which will be done on the `core` module side, by `LogcatApiLogsReader` implementing `IApiLogsProvider`.

## Outputting and visualizing the data ##
The gathered API logs have to be output from DroidMate run, so they are persisted beyond the run. Right now the logs, along with other data gathered during the exploration, are output to a .csv file which is read by manually-prepared .xls sheet generating charts based on the data. Each time the .csv file changes, it can be reloaded to the .xls sheet with `DATA->Connections->Refresh all`.

The class responsible for outputting the data is `ExplorationDataCsvOutputter`.

More information available at `OneNote / DroidMate / Documentation / Output specification`