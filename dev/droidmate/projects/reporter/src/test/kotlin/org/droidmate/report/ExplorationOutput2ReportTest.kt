package org.droidmate.report

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.droidmate.test_base.FilesystemTestFixtures
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.UnderConstruction
import org.junit.Test
import org.junit.experimental.categories.Category

class ExplorationOutput2ReportTest {

  @Test
  @Category(UnderConstruction::class)
  fun reports() {

    // KJA 2 use fixtures.f_monitoredSer2 instead of entire dir
    val ser2 = FilesystemTestFixtures.build().f_monitoredSer2
    val reportInputDirReal = ConfigurationForTests().get().reportInputDirPath

    val mockFs = ConfigurationForTests().withMockFileSystem().get()
    val reportInputDirMock = mockFs.reportInputDirPath
    val reportOutputDirMock = mockFs.reportOutputDirPath

    reportInputDirReal.copyDirContentsRecursivelyToDirInDifferentFileSystem(reportInputDirMock)

    val out = OutputDir(reportInputDirMock).read()

    check(out.isNotEmpty(), { "Check failed: out.isNotEmpty()" })

    Jimfs.newFileSystem(Configuration.unix())
    val report = ExplorationOutput2Report(out, reportOutputDirMock)

    // Act
    report.writeOut()

    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }
  }
}