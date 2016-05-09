// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.frontend

import com.google.common.base.Throwables
import org.droidmate.android_sdk.AaptWrapperStub
import org.droidmate.command.ExploreCommand
import org.droidmate.common.BuildConstants
import org.droidmate.configuration.Configuration
import org.droidmate.configuration.ConfigurationBuilder
import org.droidmate.device_simulation.AndroidDeviceSimulator
import org.droidmate.device_simulation.DeviceSimulation
import org.droidmate.device_simulation.IDeviceSimulation
import org.droidmate.exceptions.ExceptionSpec
import org.droidmate.exceptions.ITestException
import org.droidmate.exceptions.ThrowablesCollection
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.exploration.output.DroidmateOutputDir
import org.droidmate.exploration.strategy.ExplorationStrategy
import org.droidmate.filesystem.MockFileSystem
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.misc.TimeGenerator
import org.droidmate.storage.Storage2
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.RequiresDevice
import org.droidmate.test_suite_categories.RequiresSimulator
import org.droidmate.tools.DeviceToolsMock
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.Files
import java.nio.file.Path

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class DroidmateFrontendTest extends DroidmateGroovyTestCase
{
  /**
   * <p>
   * This test checks if DroidMate correctly handles complex failure scenario. This test runs on three apps mocks and on a
   * simulated device. The apps behave in the following way:
   *
   * </p><p>
   * The first one finishes exploration successfully, no exceptions get thrown. Exploration results get serialized.
   *
   * </p><p>
   * The second app is faulty, making it impossible to perform some exploration action on it. This results in an exception during
   * exploration loop. The exception gets wrapped into a collection of apk exploration exceptions
   * (the collection is itself an exception), to be reported by exception handler at the end of DroidMate's run.
   * Exploration results still get serialized.
   *
   * </p><p>
   * The installation of the third apk on the device finishes successfully. However, when the exploration of it starts to run,
   * the device fails altogether. Thus, before the proper exploration loop starts, the call to 'hasPackageInstalled' to the
   * device fails. In addition, no exploration results are obtained (because the loop didn't start) and so nothing gets
   * serialized. DroidMate then tries to recover by uninstalling the apk, which also fails, because device is unavailable. This
   * attempt suppresses the original exception. Now DroidMate tries to undeploy the device, including stopping uiautomator daemon,
   * but it also fails, suppressing the exception of apk undeployment (which also suppressed another exception:
   * the 'has package installed')
   *
   * </p><p>
   * In the end, both the set of apk exploration exceptions (having one exception, from the exploration of second app) and
   * the device undeployment exception (having suppressed exception having suppressed exception) both get wrapped into a throwable
   * collection, also being an exception. This exception is then passed to exception handler, which logs all the relevant
   * information to stderr and exceptions.txt.
   *
   * </p>
   */
  @Category([RequiresSimulator])
  @Test
  public void "Handles exploration and fatal device exceptions"()
  {
    def mockedFs = new MockFileSystem([
      "mock_1_noThrow_outputOk",
      "mock_2_throwBeforeLoop_outputNone",
      "mock_3_throwInLoop_outputPartial",
      "mock_4_throwsOUndeps_outputOk",
      "mock_5_neverExplored_outputNone",
    ])
    def apks = mockedFs.apks
    def apk1 = apks.findSingle {it.fileName == "mock_1_noThrow_outputOk.apk"}
    def apk2 = apks.findSingle {it.fileName == "mock_2_throwBeforeLoop_outputNone.apk"}
    def apk3 = apks.findSingle {it.fileName == "mock_3_throwInLoop_outputPartial.apk"}
    def apk4 = apks.findSingle {it.fileName == "mock_4_throwsOUndeps_outputOk.apk"}

    def exceptionSpecs = [

      // Thrown during Exploration.run()->tryDeviceHasPackageInstalled()
      new ExceptionSpec("hasPackageInstalled", apk2.packageName),

      // Thrown during Exploration.explorationLoop()->ResetAppExplorationAction.run()
      // The call index is 2 because 1st call is made to close 'app has stopped' dialog box before the exploration loop starts,
      // i.e. in org.droidmate.command.exploration.Exploration.tryWarnDeviceDisplaysHomeScreen
      new ExceptionSpec("perform", apk3.packageName, /* call index */ 2),

      // Thrown during ApkDeployer.tryUndeployApk().
      // The call index is 2 because 1st call is made during org.droidmate.tools.ApkDeployer.tryReinstallApk
      // No more apks should be explored after this one, as this is an apk undeployment failure.
      new ExceptionSpec("uninstallApk", apk4.packageName, /* call index */ 2),

      // Thrown during AndroidDeviceDeployer.tryTearDown()
      new ExceptionSpec("closeConnection", apk4.packageName),
    ]

    def expectedApkPackageNamesOfSer2FilesInOutputDir = [apk1.packageName, apk3.packageName, apk4.packageName]

    exploreOnSimulatorAndAssert(mockedFs, exceptionSpecs, expectedApkPackageNamesOfSer2FilesInOutputDir)
  }

  @Category([RequiresSimulator])
  @Test
  public void "Handles assertion error during exploration loop"()
  {
    def mockedFs = new MockFileSystem([
      "mock_1_throwsAssertInLoop_outputNone",
    ])
    def apk1 = mockedFs.apks.findSingle {it.fileName == "mock_1_throwsAssertInLoop_outputNone.apk"}

    def exceptionSpecs = [

      // Thrown during Exploration.explorationLoop()
      // Note that this is an AssertionError
      new ExceptionSpec("perform", apk1.packageName, /* call index */ 3, /* throwsEx */ true, /* exceptionalReturnBool */ null, /* throwsAssertionError */ true),
    ]

    List<String> expectedApkPackageNamesOfSer2FilesInOutputDir = []

    exploreOnSimulatorAndAssert(mockedFs, exceptionSpecs, expectedApkPackageNamesOfSer2FilesInOutputDir)
  }

  private void exploreOnSimulatorAndAssert(
    MockFileSystem mockedFs,
    ArrayList<ExceptionSpec> exceptionSpecs,
    ArrayList<String> expectedApkPackageNamesOfSer2FilesInOutputDir)
  {
    def cfg = new ConfigurationForTests().withFileSystem(mockedFs.fs).get()
    def timeGenerator = new TimeGenerator()
    def deviceToolsMock = new DeviceToolsMock(
      cfg,
      new AaptWrapperStub(mockedFs.apks),
      AndroidDeviceSimulator.build(timeGenerator, mockedFs.apks*.packageName, exceptionSpecs, /* unreliableSimulation */ true))

    def spy = new ExceptionHandlerSpy()

    // Act
    int exitStatus = DroidmateFrontend.main(
      cfg.args, {ExploreCommand.build(cfg, {ExplorationStrategy.build(cfg)}, timeGenerator, deviceToolsMock)},
      mockedFs.fs,
      spy
    )

    assert exitStatus != 0

    assert spy.handledThrowable instanceof ThrowablesCollection
    assert spy.throwables.size() == exceptionSpecs.size()
    assert spy.throwables.collect {Throwables.getRootCause(it) as ITestException}*.exceptionSpec == exceptionSpecs

    Path outputDir = new DroidmateOutputDir(cfg.droidmateOutputDirPath).path

    assertSer2FilesInDirAre(outputDir, expectedApkPackageNamesOfSer2FilesInOutputDir)
  }

  /**
   * <p>
   * This test runs DroidMate against a {@code AndroidDeviceSimulator}.
   * Because a device simulator is used, this test doesn't require a device (real or emulated) to be available.
   * Because no device is used, also no {@code Apk} is necessary.
   * Thus, an in-memory mock {@code FileSystem} is used.
   * The file system contains one apk stub to be used as input for the test.
   * An {@code AaptWrapper} stub is used to provide the apk stub metadata.
   * </p>
   */
  @Category([RequiresSimulator])
  @Test
  public void "Explores on a device simulator"()
  {
    def mockedFs = new MockFileSystem(["mock_app1"])
    def cfg = new ConfigurationForTests().withFileSystem(mockedFs.fs).get()
    def apks = mockedFs.apks
    def timeGenerator = new TimeGenerator()
    def simulator = AndroidDeviceSimulator.build(
      timeGenerator, apks*.packageName,
      /* exceptionsSpec */ [], /* unreliableSimulation */ true)
    def deviceToolsMock = new DeviceToolsMock(cfg, new AaptWrapperStub(apks), simulator)

    // Act
    int exitStatus = DroidmateFrontend.main(
      cfg.args, {ExploreCommand.build(cfg, {ExplorationStrategy.build(cfg)}, timeGenerator, deviceToolsMock)},
      mockedFs.fs,
      new ExceptionHandler()
    )

    assert exitStatus == 0

    def expectedDeviceSimulation = simulator.currentSimulation
    def actualDeviceSimulation = getDeviceSimulation(cfg.droidmateOutputDirPath)
    actualDeviceSimulation.assertEqual(expectedDeviceSimulation)

  }

  @Category([RequiresDevice])
  @Test
  public void "Explores monitored apk on a real device api 19"()
  {
    String[] args = new ConfigurationForTests().forDevice().setArgs([
      Configuration.pn_apksNames, "[$BuildConstants.monitored_inlined_apk_fixture_api19_name]",
      Configuration.pn_widgetIndexes, "[0, 1]",
    ]).get().args

    exploreOnRealDevice(args, "api19")
  }


  @Category([RequiresDevice])
  @Test
  public void "Explores monitored apk on a real device api 23"()
  {
    String[] args = new ConfigurationForTests().forDevice().setArgs([
      Configuration.pn_apksNames, "[$BuildConstants.monitored_inlined_apk_fixture_api23_name]",
      Configuration.pn_widgetIndexes, "[0, 1, 2, 2, 2]",
    ]).get().args

    exploreOnRealDevice(args, "api23")
  }

  /**
   * <p>
   * This tests runs DroidMate against a device (real or emulator) and deploys on it a monitored apk fixture. It assumes the apk
   * fixture with appropriate name will be present in the read apks dirs.
   *
   * </p><p>
   * This test also assumes the fixture will have two widgets to be clicked, and it will first click the first one,
   * then the second one, then terminate the exploration.
   *
   * </p><p>
   * The test will make DroidMate output results to {@code BuildConstants.test_temp_dir_name}.
   * To ensure logs are also output there, run this test with VM arg of {@code -DlogsDir="temp_dir_for_tests/logs"}.
   * Note that {@code logsDir} is defined in {@code org.droidmate.common.logging.LogbackConstants.getLogsDirPath}.
   *
   * </p>
   */
  private void exploreOnRealDevice(String[] args, String api)
  {
    DroidmateOutputDir outputDir = new DroidmateOutputDir(new ConfigurationBuilder().build(args).droidmateOutputDirPath)
    outputDir.clearContents()

    // Act
    int exitStatus = DroidmateFrontend.main(args, /* commandProvider = */ null)
    
    assert exitStatus == 0, "Exit status != 0. Please inspect the run logs for details, including exception thrown"

    IApkExplorationOutput2 apkOut = outputDir.readOutput().findSingle()

    List<List<IApiLogcatMessage>> apiLogs = apkOut?.apiLogs
    if (api == "api19")
    {
      assert apiLogs.size() == 4

      def resetAppApiLogs = apiLogs[0]
      def clickApiLogs = apiLogs[1]
      def launchActivity2Logs = apiLogs[2]
      def terminateAppApiLogs = apiLogs[3]

      assert resetAppApiLogs*.methodName == ["onResume"]
      assert clickApiLogs*.methodName == ["openConnection"]
      assert launchActivity2Logs*.methodName == ["startActivityForResult", "onResume"]
      assert terminateAppApiLogs.empty
    }
    else if (api == "api23")
    {
      // Api logs' structure (Android 6):
      //  [0] Reset
      //  [1] API: OpenURL
      //  [2] API: CameraOpen (first time, open dialog)
      //  [3] Runtime dialog close (resume app)
      //  [4] API: CameraOpen
      //  [5] Launch activity 2
      //  [6] Terminate
      assert apiLogs.size() == 7

      def resetAppApiLogs = apiLogs[0]
      def clickApiLogs = apiLogs[1]
      def openPermissionDialogApiLogs = apiLogs[2]
      def onResumeApiLogs = apiLogs[3]
      def cameraApiLogs = apiLogs[4]
      def launchActivity2Logs = apiLogs[5]
      def terminateAppApiLogs = apiLogs[6]

      assert resetAppApiLogs*.methodName == ["onResume"]
      assert clickApiLogs*.methodName == ["openConnection"]
      assert openPermissionDialogApiLogs.empty
      assert onResumeApiLogs*.methodName == ["onResume"]
      assert cameraApiLogs*.methodName == ["open"]
      assert launchActivity2Logs*.methodName == ["startActivityForResult", "onResume"]
      assert terminateAppApiLogs.empty
    }
    else throw new UnexpectedIfElseFallthroughError()
  }

  private IDeviceSimulation getDeviceSimulation(Path outputDirPath)
  {
    IApkExplorationOutput2 apkOut = new DroidmateOutputDir(outputDirPath).readOutput().findSingle()
    def deviceSimulation = new DeviceSimulation(apkOut)
    return deviceSimulation
  }

  private boolean assertSer2FilesInDirAre(Path dir, List<String> packageNames)
  {
    List<Path> serFiles = Files.list(dir).findAll {it.fileName.toString().endsWith(Storage2.ser2FileExt)}

    assert serFiles.size() == packageNames.size()
    packageNames.each {def packageName ->
      assert serFiles.any {it.fileName.toString().contains(packageName)}
    }
  }

}
