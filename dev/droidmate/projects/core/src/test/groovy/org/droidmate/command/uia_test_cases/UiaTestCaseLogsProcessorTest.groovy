// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.command.uia_test_cases

import groovy.transform.TypeChecked
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.ExplorationOutputCollectorFactory
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.Storage
import org.droidmate.misc.TimeProvider
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import static java.nio.file.Files.readAllLines
import static org.droidmate.deprecated_still_used.ApkExplorationOutputTestHelper.assertNonemptyAndValid

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class UiaTestCaseLogsProcessorTest extends DroidmateGroovyTestCase
{
  @Test
  void "Processes logs"()
  {
    def timeProvider = new TimeProvider()
    def storage = new Storage(Configuration.default.droidmateOutputDirPath)
    def processor = new UiaTestCaseLogsProcessor(new ExplorationOutputCollectorFactory(timeProvider, storage))

    IApkExplorationOutput apkExplorationOutput = processor.process(readAllLines(fixtures.f_uiaTestCaseLog))
    assertNonemptyAndValid apkExplorationOutput, /* includeTerminateAction: */ false, /* expectApiLogs */ true, /* expectGuiSnapshots */ false, /* expectUiaTestCase */ true
  }

}
