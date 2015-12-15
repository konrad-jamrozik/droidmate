// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tests_misc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;


@RunWith(MyTestRunner.class)
public class JavaScratchpadTest
{
  @Test
  public void someTest()
  {
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
    {
      public void uncaughtException(Thread t, Throwable e)
      {
        // The root logger is configured to print out the "root cause first' stack trace.
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).error("err: ", e);
      }
    });
    throw new RuntimeException("ex1", new Exception("ex2"));
  }
}

