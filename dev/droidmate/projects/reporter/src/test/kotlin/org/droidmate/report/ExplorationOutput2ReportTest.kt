package org.droidmate.report

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.droidmate.test_suite_categories.UnderConstruction
import org.junit.Test
import org.junit.experimental.categories.Category

class ExplorationOutput2ReportTest {

  // KJA current work
  @Test
  @Category(UnderConstruction::class)
  fun reports() {

    // KJA change the dir and ensure there is some input data in the dir
    val reportInputDirReal = ConfigurationForTests().get().reportInputDirPath
    val fsMock = ConfigurationForTests().withMockFileSystem().get()
    val reportInputDirMock = fsMock.reportInputDirPath
    val reportOutputDirMock = fsMock.reportOutputDirPath
    reportInputDirReal.copyDirContentsRecursivelyToDirInDifferentFileSystem(reportInputDirMock)
    val out = OutputDir(reportInputDirMock).readOutput()
    check(out.isNotEmpty())

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


