Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de  
Date of last full review: Dec 3, 2014

## Exploration I/O ##

The core exploration logic happens in `ExplorationExecutor` which takes as input both an `Apk` and the `AndroidDevice` on which it is deployed. It outputs `ApkExplorationData`.

`ApkExplorationData` contains information about `ApiCoverageLog`s gathered, `ExplorationAction`s conducted, `DeviceGuiSnapshot`s encountered, and some other minor data. 

Later on in `Droidmate` the collection of `ApkExplorationData` is later converted to `CsvExplorationData` by `ExplorationDataCsvOutputter` to be then output to `.csv`.
 
## Exploration execution loop overview ##

`ExplorationExecutor` is responsible for conducting the exploration of an `apk` on an `AndroidDevice` `device`  and returning `ApkExplorationData` from it. 

### Exploration setup ###
First it creates the necessary infrastructure: 

- **`ExplorationDataCollector`** that will collect all the output apk exploration data as soon as it is available so if anything crashes, the data is not lost.

- **`ExplorationStrategy`** that decides what `ExplorationAction` to conduct based on `GuiState`, which in turn represents the current state of `device` GUI.

- **`DeviceExplorationDriver`** that can translate the abstract `ExplorationAction` to a set of commands to be executed on the `device` and return the resulting `GuiState`.

### Exploration loop ###
Then the exploration-proper takes place in a loop:
 
- The loop starts with `ResetAppExplorationAction` ensuring the device will end up in the main activity of the subject `apk`.
- The `driver` executes current `explorationAction`, obtaining `guiState` which is then feed to `strategy` to obtain next action which then loops.
- The loop ends with `TerminateExplorationAction` which might happen either because the `strategy` determined it doesn't know what to do in current state or some of the exploration bounds were reached, like maximum number of operations.

After the loop finished, the collected exploration data is returned.