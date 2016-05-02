package org.droidmate.report

import org.droidmate.test_base.FilesystemTestFixtures
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.UnderConstruction
import org.junit.Test
import org.junit.experimental.categories.Category

class ExplorationOutput2ReportTest {
 
  @Test
  // KJA2-report refactor, review. If test is done, remove tag and add to test suite.
  @Category(UnderConstruction::class)
  fun reports() {

    val mockFs = ConfigurationForTests().withMockFileSystem().get()
    val reportInputDirMock = mockFs.reportInputDirPath
    val reportOutputDirMock = mockFs.reportOutputDirPath

    val ser2 = FilesystemTestFixtures.build().f_monitoredSer2
    listOf(ser2).copyFilesToDirInDifferentFileSystem(reportInputDirMock)

    val explOutput2 = OutputDir(reportInputDirMock).read()
    check(explOutput2.isNotEmpty(), { "Check failed: explOutput2.isNotEmpty()" })

    val report = ExplorationOutput2Report(explOutput2, reportOutputDirMock)

    // Act
    report.writeOut()

    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }
  }
}