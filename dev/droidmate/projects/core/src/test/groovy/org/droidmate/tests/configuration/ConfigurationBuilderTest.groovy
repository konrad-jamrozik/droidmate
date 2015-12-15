// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tests.configuration

import groovy.transform.TypeChecked
import org.droidmate.configuration.ConfigurationBuilder
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.FileSystems

import static groovy.transform.TypeCheckingMode.SKIP

@TypeChecked(SKIP)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class ConfigurationBuilderTest extends DroidmateGroovyTestCase
{
  @Test
  void "Builds configuration"()
  {
    // Act
    new ConfigurationBuilder().build(new String[0], FileSystems.getDefault())
  }
}
