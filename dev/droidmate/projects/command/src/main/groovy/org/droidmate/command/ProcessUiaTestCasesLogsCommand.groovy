// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command

import groovy.util.logging.Slf4j
import org.droidmate.command.uia_test_cases.IUiaTestCaseLogsProcessor
import org.droidmate.command.uia_test_cases.UiaTestCaseLogsProcessor
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.*
import org.droidmate.misc.TimeProvider

import java.nio.file.Files
import java.nio.file.Path

import static java.nio.file.Files.readAllLines

// KJA to remove soon
@Slf4j
class ProcessUiaTestCasesLogsCommand extends DroidmateCommand
{

  final IStorage                            storage
  final IUiaTestCaseLogsProcessor           uiautomatorTestCaseLogsProcessor

  ProcessUiaTestCasesLogsCommand(IUiaTestCaseLogsProcessor uiautomatorTestCaseLogsProcessor, IStorage storage)
  {
    this.storage = storage
    this.uiautomatorTestCaseLogsProcessor = uiautomatorTestCaseLogsProcessor
  }

  static ProcessUiaTestCasesLogsCommand build(Configuration cfg)
  {
    def timeProvider = new TimeProvider()
    def storage = new Storage(cfg.droidmateOutputDirPath)
    def processor = new UiaTestCaseLogsProcessor(new ExplorationOutputCollectorFactory(timeProvider, storage))

    return new ProcessUiaTestCasesLogsCommand(processor, storage)
  }

  /**
   * <p>
   * Extracts and persists as text files comparison of DroidMate runs with uiautomator test cases.
   *
   * </p><p>
   * The uiautomator test cases are taken from {@code Configuration.uiaTestCasesLogsDirPath} while DroidMate runs come from
   * {@code Storage.getSerializedRuns ( )}
   *
   * </p><p>
   * The comparison is always made between one DroidMate run with given package name and all uiautomator test cases having the
   * same package name. Thus, if there are two or more DroidMate runs with the same package name, the execution fails. Also,
   * if there are no uiautomator test cases for given DroidMate run, the execution also fails.
   *
   * </p>
   */
  @Override
  void execute(Configuration cfg)
  {
    List<IApkExplorationOutput> uiaTestCases = Files.list(cfg.uiaTestCasesLogsDirPath).collect {Path path ->
      log.info("Processing uiautomator test cases logs from file: " + path)
      return uiautomatorTestCaseLogsProcessor.process(readAllLines(path))
    }

    List<String> pkgNames = extractPackageNames(storage.serializedRuns)

    uiaTestCases.each {
      if (!(it.appPackageName in pkgNames))
        log.warn("The uia test case for app ${it.appPackageName} doesn't have a corresponding DroidMate run, so it will be skipped.")
    }

    List<ExplorationOutput> listOfDroidmateRunsWithTheirUiaTestCases =
      groupDroidmateRunsWithTheirUiaTestCases(pkgNames, storage.serializedRuns, uiaTestCases)

    // KJA perister.persist was removed
//    listOfDroidmateRunsWithTheirUiaTestCases.eachWithIndex {ExplorationOutput explorationOutput, int i ->
//      log.info("Processing run ${(i + 1)}/${pkgNames.size()}.")
//      persister.persist(explorationOutput)
//    }
  }

  private List<String> extractPackageNames(Collection<Path> serializedDroidmateRuns)
  {
    int pkgNameStartIndex = "YYYY MMM DD HHMM ".size()
    int pkgNameEndIndex = Storage.serFileExt.size() + 1

    List<String> pkgNames = serializedDroidmateRuns.collect {it.fileName.toString()[pkgNameStartIndex..-pkgNameEndIndex]}

    assert pkgNames.unique().size() == pkgNames.size():
      "Each of the DroidMate runs should have a different package name, as only one dm run can be compared against " +
        "a set of use cases pertaining to it."
    return pkgNames
  }

  private List<ExplorationOutput> groupDroidmateRunsWithTheirUiaTestCases(
    Collection<String> pkgNames,
    Collection<Path> serializedDroidmateRuns,
    List<IApkExplorationOutput> uiaTestCases)
  {
    return pkgNames.collect {String pkgName ->

      Path dmRunPath = serializedDroidmateRuns.find {it.fileName.toString().contains(pkgName)}
      IApkExplorationOutput dmRun = storage.deserializeApkExplorationOutput(dmRunPath)

      Collection<IApkExplorationOutput> dmRunUiaTestCases = uiaTestCases.findAll {it.appPackageName == dmRun.appPackageName}

      assert dmRunUiaTestCases.size() > 0:
        "There has to be at least one uia test case for dm run with package name $dmRun.appPackageName but none was found."

      return dmRunUiaTestCases.plus(dmRun) as ExplorationOutput
    }
  }


}
