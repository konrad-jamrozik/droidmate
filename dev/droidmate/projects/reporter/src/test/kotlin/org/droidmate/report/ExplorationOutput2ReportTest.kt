package org.droidmate.report

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.junit.Test

class ExplorationOutput2ReportTest {

  // KJA current work
  @Test
  fun reports() {

    // KJA change the dir and ensure there is some input data in the dir
    val reportInputDirReal = ConfigurationForTests().get().reportInputDirPath
    val reportInputDirMock = ConfigurationForTests().withMockFileSystem().get().reportInputDirPath
    reportInputDirReal.copyDirContentsRecursivelyToDirInDifferentFileSystem(reportInputDirMock)
    val out = ReportDir(reportInputDirMock).readOutput()
    check(out.isNotEmpty())

    Jimfs.newFileSystem(Configuration.unix())
    val report = ExplorationOutput2Report(out, reportInputDirMock)

    // Act
    report.writeOut()

    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }
  }
}


