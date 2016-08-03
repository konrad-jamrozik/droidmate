// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.monitor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MonitorJavaTemplateTest
{
  @Test
  public void converts()
  {
    assertEquals("null", MonitorJavaTemplate.convert(null));
    assertEquals("[97,98,99]", MonitorJavaTemplate.convert("abc".getBytes()));
  }

}