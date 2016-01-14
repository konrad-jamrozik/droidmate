// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apk_inliner

import com.github.konrad_jamrozik.ResourcePath
import groovy.transform.TypeChecked
import org.droidmate.init.InitConstants
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.Files
import java.nio.file.Path

import static groovy.transform.TypeCheckingMode.SKIP

@TypeChecked(SKIP)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class ApkInlinerFrontendTest
{

  /**
   * <p>
   * The test check if apk inliner successfully inlines an apk without throwing an exception. It doesn't check if the inlined
   * functionality works as expected. For that, please refer to tests using {@code org.droidmate.test.MonitoredInlinedApkFixture}.
   *
   * </p>
   */
  @Test
  public void "Inlines apk"()
  {
    Path inputApkFixturesDir = new ResourcePath(InitConstants.apk_fixtures).path
    assert Files.isDirectory(inputApkFixturesDir)
    assert Files.list(inputApkFixturesDir).count() == 1
    Path inputApkFixture = new ApkPath(Files.list(inputApkFixturesDir).find() as Path).path
    assert inputApkFixture.fileName.toString() == "com.estrongs.android.taskmanager.apk"

    Path inputDir = InitConstants.apk_inliner_proj_dir.resolve("tmp-test-toremove_input-apks")
    Path outputDir = InitConstants.apk_inliner_proj_dir.resolve("tmp-test-toremove_output-apks")
    inputDir.deleteDir()
    outputDir.deleteDir()
    Files.createDirectory(inputDir)
    Files.createDirectory(outputDir)

    Files.copy(inputApkFixture, inputDir.resolve(inputApkFixture.getFileName()))

    ApkInlinerFrontend.handleException = {Exception e -> throw e}
    // Act
    ApkInlinerFrontend.main([
      InitConstants.apk_inliner_param_input, inputDir.toAbsolutePath().toString(),
      InitConstants.apk_inliner_param_output_dir, outputDir.toAbsolutePath().toString()
    ] as String[])

    assert Files.list(outputDir).count() == 1
    assert new ApkPath(Files.list(outputDir).find() as Path).path

  }

}