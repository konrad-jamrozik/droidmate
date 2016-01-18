// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.droidmate.common.logcat.TimeFormattedLogcatMessage
import org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime

import static org.droidmate.deprecated_still_used.TimestampedExplorationActionTestHelper.newTimestampedResetAppExplorationAction

@Deprecated
@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class ApkExplorationOutputTest extends DroidmateGroovyTestCase
{


  @Test
  void "Serializes and deserializes exploration output"()
  {
    Path serializedOutputFile = getSerializedOutputFile()
    ExplorationOutput explorationOutput = buildExplorationOutputFixture()

    // Act 1
    serialize(serializedOutputFile, explorationOutput)
    // Act 2
    ExplorationOutput deserializedExplorationOutput = deserializeExplorationOutput(serializedOutputFile)

    assertCaughtExceptions(explorationOutput, deserializedExplorationOutput)
    removeCaughtExceptions(explorationOutput, deserializedExplorationOutput)
    assert explorationOutput == deserializedExplorationOutput
  }



  private static ExplorationOutput deserializeExplorationOutput(Path serializedOutputFile)
  {
    ExplorationOutput deserializedExplorationOutput = null
    serializedOutputFile.withObjectInputStream {
      deserializedExplorationOutput = it.readObject() as ExplorationOutput
    }
    return deserializedExplorationOutput
  }

  private static Object serialize(Path serializedOutputFile, ExplorationOutput explorationOutput)
  {
    return serializedOutputFile.withObjectOutputStream {
      it.writeObject(explorationOutput)
    }
  }

  @TypeChecked(TypeCheckingMode.SKIP)
  private static void assertCaughtExceptions(ExplorationOutput explorationOutput, ExplorationOutput deserializedExplorationOutput)
  {
    explorationOutput*.caughtException.eachWithIndex {Exception exception, int i ->
      def deserializedEx = deserializedExplorationOutput*.caughtException[i]
      assert (exception == null && deserializedEx == null) || (exception.class == deserializedEx.class)
    }
  }

  private static void removeCaughtExceptions(ExplorationOutput explorationOutput, ExplorationOutput deserializedExplorationOutput)
  {
    explorationOutput.each {IApkExplorationOutput it -> it.caughtException = null}
    deserializedExplorationOutput.each {IApkExplorationOutput it -> it.caughtException = null}
  }

  private static Path getSerializedOutputFile()
  {
    FileSystem fs = Jimfs.newFileSystem(Configuration.unix())
    Path workDir = fs.getPath("/work")
    assert Files.isDirectory(workDir)
    Path serializedOutputFile = workDir.resolve("exploration_output.ser")
    return serializedOutputFile
  }

  private static ExplorationOutput buildExplorationOutputFixture()
  {
    LocalDateTime monitorInitTime1 = LocalDateTime.parse("${TimeFormattedLogcatMessage.assumedDate.year}-02-03T04:12:23.100")
    LocalDateTime monitorInitTime2 = LocalDateTime.parse("${TimeFormattedLogcatMessage.assumedDate.year}-05-09T06:03:44.353")
    ExplorationOutput explorationOutput = ExplorationOutputBuilder.build() {
      apk(monitorInitTime: monitorInitTime1) {
        apiLogs(mssSinceMonitorInit: [100, 200], methodNames: ["m1", "m2"])
        apiLogs(mssSinceMonitorInit: [300], methodNames: ["m3"])
      }
      apk(monitorInitTime: monitorInitTime2) {
        apiLogs(mssSinceMonitorInit: [400], methodNames: ["m4"])
      }
    }
    explorationOutput << ApkExplorationOutput.create(
      appPackageName: "apk3",
      monitorInitTime: monitorInitTime2,
      actions: [newTimestampedResetAppExplorationAction()],
      guiSnapshots: [UiautomatorWindowDumpTestHelper.newHomeScreenWindowDump()],
      caughtException: new DeviceGuiSnapshotVerificationException(
        VerifiableDeviceAction.newLaunchActivityVerifiableDeviceAction("dummy1", "dummy2"),
        UiautomatorWindowDumpTestHelper.newHomeScreenWindowDump()),
      explorationEndTime: LocalDateTime.now()
    )
    return explorationOutput
  }
}