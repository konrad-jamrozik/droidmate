// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.deprecated_still_used

import groovy.transform.TypeChecked
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.Files
import java.nio.file.Path

@Deprecated
@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class DeprecatedClassesDeserializerTest extends DroidmateGroovyTestCase
{
  @Test
  void "Deserializes legacy apk exploration output"()
  {
    // Act
    // KJA broken because parts of ApkExplorationOutput have been removed
    // deserializeApkExplorationOutput(fixtures.f_legacySer)
  }

  private static ApkExplorationOutput deserializeApkExplorationOutput(Path serializedOutputFile)
  {
    def stream = new DeprecatedClassesDeserializer(Files.newInputStream(serializedOutputFile))
    def apkout = stream.readObject() as ApkExplorationOutput
    stream.close()
    return apkout
  }

}
