package org.droidmate.report

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.konradjamrozik.createDirIfNotExists
import org.droidmate.test_base.FilesystemTestFixtures
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Path

class ExplorationOutput2ReportTest {
 
  @Test
    // KJA simplify
  fun reports() {

    val ser2 = FilesystemTestFixtures.build().f_monitoredSer2
    val fs = Jimfs.newFileSystem(Configuration.unix())
    val report = ExplorationOutput2Report(
      OutputDir(DirWithFiles(fs, "droidmateOutputDir", ser2).path).notEmptyExplorationOutput2,
      DirWithFiles(fs, "reportOutputDir").path
    )

    // Act
    report.writeOut()

    // Print out for manual assessment
    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }

  }

  // @Test
  // KJA temp test for further simplifications
  fun reports2() {

    val ser2 = FilesystemTestFixtures.build().f_monitoredSer2
    val fs = Jimfs.newFileSystem(Configuration.unix())
    val report = ExplorationOutput2Report(
      OutputDir(DirWithFiles(fs, "droidmateOutputDir", ser2).path).notEmptyExplorationOutput2,
      DirWithFiles(fs, "reportOutputDir").path
    )

    // Act
    report.writeOut()

    // Print out for manual assessment
    report.reportFiles.forEach {
      println(it.toAbsolutePath().toString())
      println(it.text())
    }
  }
}

class DirWithFiles(fs: FileSystem, dirPathString: String, vararg files: Path) {

  val path: Path by lazy {
    val dirPath = fs.getPath(dirPathString)
    dirPath.createDirIfNotExists()
    files.asList().copyFilesToDirInDifferentFileSystem(dirPath)
    dirPath
  }

}
