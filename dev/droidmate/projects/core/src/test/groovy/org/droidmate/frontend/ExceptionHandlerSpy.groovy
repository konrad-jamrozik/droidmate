// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.frontend

import org.droidmate.android_sdk.ApkExplorationExceptionsCollection
import org.droidmate.exceptions.ITestException
import org.droidmate.exceptions.ThrowablesCollection

class ExceptionHandlerSpy
{

  @Delegate
  ExceptionHandler exceptionHandler = new ExceptionHandler()

  Throwable handledThrowable
  List<ITestException> testDeviceExceptions = []

  @Override
  int handle(Throwable e)
  {
    this.handledThrowable = e
    this.testDeviceExceptions = extractTestDeviceExceptions(e)
    exceptionHandler.handle(e)
  }

  @SuppressWarnings("GroovyUnusedDeclaration")
  // Actually used, thanks to groovy's dispatch on method param type.
  private static List<ITestException> extractTestDeviceExceptions(ApkExplorationExceptionsCollection e)
  {
    e.exceptions.collect {
      if (!(it.exception instanceof ITestException))
        throw new Exception(it.exception)

      (it.exception as ITestException)
    }
  }

  @SuppressWarnings("GroovyUnusedDeclaration") // Actually used, thanks to groovy's dispatch on method param type.
  private static List<ITestException> extractTestDeviceExceptions(ThrowablesCollection e)
  {
    return e.throwables.collect { extractTestDeviceExceptions(it) }.flatten() as List<ITestException>
  }

  private static List<ITestException> extractTestDeviceExceptions(Throwable e)
  {
    List<ITestException> out = []
    if (e instanceof ITestException)
      out << e

    if (e.suppressed.size() == 0)
      return out
    else
      out + (e.suppressed.collect { extractTestDeviceExceptions(it) }.flatten() as List<ITestException>)
  }

}
