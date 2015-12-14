// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.tests_misc;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.LoggerFactory;

public class MyTestRunner extends BlockJUnit4ClassRunner
{

  public MyTestRunner(Class<?> klass) throws InitializationError
  {
    super(klass);
  }

  @Override
  public void run(RunNotifier notifier) {
    notifier.addFirstListener(new RunListener() {
      @Override
      public void testFailure(Failure failure) throws Exception {
        Throwable exception = failure.getException();
        // The root logger is configured to print out the "root cause first' stack trace.
        LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).error("err: ", exception);
      }
    });
    super.run(notifier);
  }

}
