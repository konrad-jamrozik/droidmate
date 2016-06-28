// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.output

import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.exploration.data_aggregators.ExplorationOutput2Fixture
import org.droidmate.exploration.data_aggregators.IExplorationOutput2Fixture
import org.droidmate.filesystem.MockFileSystem
import org.droidmate.storage.IWritableDirectory
import org.droidmate.storage.WritableDirectory
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_helpers.configuration.ConfigurationForTests
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.Files
import java.nio.file.Path

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class WritableExplorationOutput2AnalysisTest extends DroidmateGroovyTestCase
{
  @Test
  void "Writes exploration output 2 analysis"()
  {
    IExplorationOutput2Fixture fixture = new ExplorationOutput2Fixture()
    ExplorationOutput2 out2 = fixture.fixture
    IWritableExplorationOutput2Analysis analysis = new WritableExplorationOutput2Analysis(out2, fixture.timeTicks, fixture.timeTickSizeInMs)

    def cfg = new ConfigurationForTests().withFileSystem(new MockFileSystem([]).fs).get()
    IWritableDirectory writableDirectory = new WritableDirectory(cfg.droidmateOutputDirPath)

    // Act
    analysis.write(writableDirectory)

    List<Path> writtenFiles = Files.list(writableDirectory.dir).collect() as List<Path>

    assert writtenFiles.size() == fixture.expectedChartFileContents.size()
    assert writtenFiles.size() == analysis.writeTargetsNames.size()

    analysis.writeTargetsNames.each {
      assert it in writtenFiles*.fileName*.toString()
    }

    writtenFiles.eachWithIndex {Path file, int i ->
      assert Files.size(file) > 0
      assert file.text == fixture.expectedChartFileContents[i]
    }
  }
}
