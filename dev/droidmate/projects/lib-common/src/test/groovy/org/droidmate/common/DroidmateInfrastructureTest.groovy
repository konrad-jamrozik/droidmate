// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common;

import groovy.transform.TypeChecked
import joptsimple.OptionParser
import joptsimple.OptionSet
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters;

import static groovy.transform.TypeCheckingMode.SKIP

import static org.junit.Assert.*;
@TypeChecked(SKIP)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
public class DroidmateInfrastructureTest
{


  @Test
  public void supportsShortOptions() {
    OptionParser parser = new OptionParser( "aB?*." );

    OptionSet options = parser.parse( "-a", "-B", "-?" );

    assertTrue( options.has( "a" ) );
    assertTrue( options.has( "B" ) );
    assertTrue( options.has( "?" ) );
    assertFalse( options.has( "." ) );
  }

  @Test
  public void acceptsLongOptions() {
    OptionParser parser = new OptionParser();
    parser.accepts( "flag" );
    parser.accepts( "verbose" );

    OptionSet options = parser.parse( "-flag" );

    assertTrue( options.has( "flag" ) );
    assertFalse( options.has( "verbose" ) );
  }
}