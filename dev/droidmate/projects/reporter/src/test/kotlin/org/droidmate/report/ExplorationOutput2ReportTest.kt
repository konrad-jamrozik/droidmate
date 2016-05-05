package org.droidmate.report

import org.droidmate.test_base.FilesystemTestFixtures
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.junit.Test

class ExplorationOutput2ReportTest {
 
  @Test
  // KJA simplify
  fun reports() {

    val mockFs = ConfigurationForTests().withMockFileSystem().get()
    val reportInputDirMock = mockFs.reportInputDirPath
    val reportOutputDirMock = mockFs.reportOutputDirPath

    val ser2 = FilesystemTestFixtures.build().f_monitoredSer2
    listOf(ser2).copyFilesToDirInDifferentFileSystem(reportInputDirMock)

    val report = ExplorationOutput2Report(OutputDir(reportInputDirMock).notEmptyExplorationOutput2, reportOutputDirMock)

    // Act
    report.writeOut()

    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }
  }
}